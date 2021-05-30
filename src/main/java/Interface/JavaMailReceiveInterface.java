package Interface;

import com.sun.mail.util.MailConnectException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class test2 extends JFrame {
    static String[] messagecontent=new String[]{" "," "};
    static int id;
    String host;
    String type;

    public static void main(String[] args) {
        test2 t = new test2();
        t.run();
    }

    public void run() {
        JButton jb1 = new JButton("接收");
        jb1.setBounds(420, 410, 65, 30);
        JButton jb2 = new JButton("返回");
        jb2.setBounds(500, 410, 65, 30);
        // 创建表格内容的容器
        Vector<Vector<Object>> contextList = new Vector<>();
        // 创建表头的数据容器
        Vector<Object> titleList = new Vector<>();
        titleList.add("编号");
        titleList.add("主题");
        titleList.add("发件人");
        titleList.add("时间");
        titleList.add("是否含有附件");
        // 创建DefaultTableMode模型 管理数据容器
        DefaultTableModel model = new DefaultTableModel(contextList, titleList);
        // 创建表格
        JTable table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 30, 565, 370);
        jb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection conn;
                try {
                    //加载驱动程序
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    //连接数据库
                    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=UTC", "root", "7355608");
                    //对数据库进行操作
                    String sql = "SELECT mailtype FROM user WHERE address=? AND password=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setObject(1, Login.jtf1.getText());
                    ps.setObject(2, String.valueOf(Login.jpf2.getPassword()));
                    ps.execute();
                    ResultSet re = ps.getResultSet();
                    //如果有数据，re.next()返回true
                    if (re.next()) {
                        type = re.getString(1);
                    }
                    re.close();
                    ps.close();
                } catch (ClassNotFoundException | SQLException a) {
                    a.printStackTrace();
                }
                if (type.equals("163")) {
                    String host = "pop.163.com";
                    String port = "995";
                    Properties properties = new Properties();
                    // server setting
                    properties.put("mail.pop3.host", host);
                    properties.put("mail.pop3.port", port);
                    // SSL setting
                    properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    properties.setProperty("mail.pop3.socketFactory.fallback", "false");
                    properties.setProperty("mail.pop3.socketFactory.port", port);
                    Session session = Session.getDefaultInstance(properties);
                    try {
                        // connects to the message store
                        Store store = session.getStore("pop3");
                        store.connect(Login.jtf1.getText(), String.valueOf(Login.jpf2.getPassword()));
                        // opens the inbox folder
                        Folder folderInbox = store.getFolder("INBOX");
                        folderInbox.open(Folder.READ_ONLY);
                        // fetches new messages from server
                        Message[] arrayMessages = folderInbox.getMessages();
                        for (int i = 0; i < arrayMessages.length; i++) {
                            Message message = arrayMessages[i];
                            Address[] fromAddress = message.getFrom();
                            String from = fromAddress[0].toString();
                            String subject = message.getSubject();
                            String sentDate = message.getSentDate().toString();
                            String contentType = message.getContentType();
                            String messageContent = "";
                            String yn = "否";
                            if (contentType.contains("multipart")) {
                                // content may contain attachments
                                Multipart multiPart = (Multipart) message.getContent();
                                int numberOfParts = multiPart.getCount();
                                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                        // this part is attachment
                                        yn = "是";
                                        String fileName = part.getFileName();
                                        part.saveFile("D:\\" + File.separator + fileName);
                                    } else {
                                        // this part may be the message content
                                        messageContent = part.getContent().toString();
                                        messageContent = delHTMLTag(messageContent);
                                        messagecontent[i] = messageContent;
                                    }
                                }
                            } else if (contentType.contains("text/plain")) {
                                Object content = message.getContent();
                                if (content != null) {
                                    messageContent = content.toString();
                                    messagecontent[i] = messageContent;
                                }
                            } else if (contentType.contains("text/html")) {
                                Object content = message.getContent();
                                if (content != null) {
                                    messageContent = content.toString();
                                    messageContent = delHTMLTag(messageContent);
                                    messagecontent[i] = messageContent;
                                }
                            }
                            Vector<Object> list = new Vector<>();
                            list.addElement(i);
                            list.addElement(subject);
                            list.addElement(from);
                            list.addElement(sentDate);
                            list.addElement(yn);
                            //将数据放入数据容器中
                            model.addRow(list);
                        }
                        JOptionPane.showMessageDialog(null, "成功接收邮件！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        // disconnect
                        folderInbox.close(false);
                        store.close();
                    } catch (NoSuchProviderException ex) {
                        System.out.println("No provider for pop3.");
                        ex.printStackTrace();
                    }catch (AuthenticationFailedException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "邮箱地址或授权码错误!", "警告", JOptionPane.WARNING_MESSAGE);
                    } catch (MessagingException ex) {
                        JOptionPane.showMessageDialog(null, "网络连接超时!", "警告", JOptionPane.WARNING_MESSAGE);
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }else{
                    host = "pop.qq.com";
                    String port = "995";
                    Properties properties = new Properties();
                    // server setting
                    properties.put("mail.pop3.host", host);
                    properties.put("mail.pop3.port", port);
                    // SSL setting
                    properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    properties.setProperty("mail.pop3.socketFactory.fallback", "false");
                    properties.setProperty("mail.pop3.socketFactory.port", port);
                    Session session = Session.getDefaultInstance(properties);
                    try {
                        // connects to the message store
                        Store store = session.getStore("pop3");
                        store.connect(Login.jtf1.getText(), String.valueOf(Login.jpf2.getPassword()));
                        // opens the inbox folder
                        Folder folderInbox = store.getFolder("INBOX");
                        folderInbox.open(Folder.READ_ONLY);
                        // fetches new messages from server
                        Message[] arrayMessages = folderInbox.getMessages();
                        for (int i = 0; i < arrayMessages.length; i++) {
                            Message message = arrayMessages[i];
                            Address[] fromAddress = message.getFrom();
                            String from = fromAddress[0].toString();
                            String subject = message.getSubject();
                            String sentDate = message.getSentDate().toString();
                            String contentType = message.getContentType();
                            String messageContent = "";
                            String yn = "否";
                            if (contentType.contains("multipart")) {
                                // content may contain attachments
                                Multipart multiPart = (Multipart) message.getContent();
                                int numberOfParts = multiPart.getCount();
                                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                        // this part is attachment
                                        yn = "是";
                                        String fileName = part.getFileName();
                                        part.saveFile("D:\\" + File.separator + fileName);
                                    } else {
                                        // this part may be the message content
                                        messageContent = part.getContent().toString();
                                        messageContent = delHTMLTag(messageContent);
                                        messagecontent[i] = messageContent;
                                    }
                                }
                            } else if (contentType.contains("text/plain")) {
                                Object content = message.getContent();
                                if (content != null) {
                                    messageContent = content.toString();
                                    messagecontent[i] = messageContent;
                                }
                            } else if (contentType.contains("text/html")) {
                                Object content = message.getContent();
                                if (content != null) {
                                    messageContent = content.toString();
                                    messageContent = delHTMLTag(messageContent);
                                    messagecontent[i] = messageContent;
                                }
                            }
                            Vector<Object> list = new Vector<>();
                            list.addElement(i);
                            list.addElement(subject);
                            list.addElement(from);
                            list.addElement(sentDate);
                            list.addElement(yn);
                            //将数据放入数据容器中
                            model.addRow(list);
                        }
                        JOptionPane.showMessageDialog(null, "成功接收邮件！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        // disconnect
                        folderInbox.close(false);
                        store.close();
                    } catch (NoSuchProviderException ex) {
                        System.out.println("No provider for pop3.");
                        ex.printStackTrace();
                    } catch (AuthenticationFailedException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "邮箱地址或授权码错误!", "警告", JOptionPane.WARNING_MESSAGE);
                    }catch (MessagingException ex) {
                        JOptionPane.showMessageDialog(null, "网络连接超时!", "警告", JOptionPane.WARNING_MESSAGE);
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    id= (int) table.getValueAt(row, 0);
                    new MailContent();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Menu();
            }
        });
        Container container = this.getContentPane();
        container.add(scrollPane);
        container.add(jb1);
        container.add(jb2);
        this.setLayout(null);
        this.setTitle("表格添加数据");
        this.setVisible(true);
        this.setSize(600, 500);
        this.setLocationRelativeTo(null);// 设置居中显示
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //去掉html相关标签正则表达式方法
    public static String delHTMLTag(String inputString) {
        // 含html标签的字符串
        String htmlStr = inputString;
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        java.util.regex.Pattern p_special;
        java.util.regex.Matcher m_special;
        //定义script的正则表达式
        String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
        //定义style的正则表达式
        String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
        // 定义HTML标签的正则表达式
        String regEx_html = "<[^>]+>";
        // 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        String regEx_special = "\\&[a-zA-Z]{1,10};";
        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        m_script = p_script.matcher(htmlStr);
        // 过滤script标签
        htmlStr = m_script.replaceAll("");
        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        m_style = p_style.matcher(htmlStr);
        // 过滤style标签
        htmlStr = m_style.replaceAll("");
        p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        m_html = p_html.matcher(htmlStr);
        // 过滤html标签
        htmlStr = m_html.replaceAll("");
        p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
        m_special = p_special.matcher(htmlStr);
        // 过滤特殊标签
        htmlStr = m_special.replaceAll("");
        textStr = htmlStr;
        return textStr;
    }
}