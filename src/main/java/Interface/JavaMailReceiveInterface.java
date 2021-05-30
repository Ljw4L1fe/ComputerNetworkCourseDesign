package Interface;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class JavaMailReceiveInterface extends JFrame {
    //存放邮箱正文消息的数组
    static String[] messagecontent = new String[100];
    //邮箱编号
    static int id;
    //邮箱host
    String host;
    //邮箱类型
    static String type;
    //邮箱端口
    String port = "995";
    public Multipart multiPart;
    public static MimeBodyPart part;

    public static void main(String[] args) {
        JavaMailReceiveInterface t = new JavaMailReceiveInterface();
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
                        //获得邮箱类型type
                        type = re.getString(1);
                    }
                    re.close();
                    ps.close();
                } catch (ClassNotFoundException | SQLException a) {
                    a.printStackTrace();
                }
                //判断邮箱类型，选择适合的host
                if (type.equals("163")) {
                    host = "pop.163.com";
                } else {
                    host = "pop.qq.com";
                }
                Properties properties = new Properties();
                //服务器设置
                properties.put("mail.pop3.host", host);
                properties.put("mail.pop3.port", port);
                //SSL设置
                properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.setProperty("mail.pop3.socketFactory.fallback", "false");
                properties.setProperty("mail.pop3.socketFactory.port", port);
                Session session = Session.getDefaultInstance(properties);
                try {
                    //连接邮件的store
                    Store store = session.getStore("pop3");
                    store.connect(Login.jtf1.getText(), String.valueOf(Login.jpf2.getPassword()));
                    //打开INBOX文件夹(所有收到的信件将保存在INBOX文件夹中)
                    Folder folderInbox = store.getFolder("INBOX");
                    //设置只读
                    folderInbox.open(Folder.READ_ONLY);
                    //从服务器中获取邮件，保存在arrayMessages数组中
                    Message[] arrayMessages = folderInbox.getMessages();
                    //从arrayMessages数组循环获取邮件数据
                    for (int i = 0; i < arrayMessages.length; i++) {
                        Message message = arrayMessages[i];
                        Address[] fromAddress = message.getFrom();
                        //邮件发件人
                        String from = fromAddress[0].toString();
                        //邮件主题
                        String subject = message.getSubject();
                        //邮件发件时间
                        String sentDate = message.getSentDate().toString();
                        //邮件消息类型
                        String contentType = message.getContentType();
                        String messageContent = "";
                        String yn = "否";
                        //判断邮件消息是否包含multipart类型
                        if (contentType.contains("multipart")) {
                            //若邮件消息包含multipart类型，则可能包含附件
                            //将邮件消息强制转化为multipart类型
                            multiPart = (Multipart) message.getContent();
                            //获得part数量
                            int numberOfParts = multiPart.getCount();
                            //循环邮件消息的每一个part
                            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                                //判断每一个part是否是附件
                                part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                    //这个part是附件
                                    yn = "是";
                                } else {
                                    //将消息转化为字符串
                                    messageContent = part.getContent().toString();
                                    //将字符串消去html相关标签
                                    messageContent = delHTMLTag(messageContent);
                                    //将正文内容存进messagecontent数组中
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
                        //将数据放入数据容器中
                        Vector<Object> list = new Vector<>();
                        list.addElement(i);
                        list.addElement(subject);
                        list.addElement(from);
                        list.addElement(sentDate);
                        list.addElement(yn);
                        model.addRow(list);
                    }
                    JOptionPane.showMessageDialog(null, "成功接收邮件！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    //关闭连接
                    folderInbox.close(false);
                    store.close();
                } catch (NoSuchProviderException | IOException ex) {
                    ex.printStackTrace();
                } catch (AuthenticationFailedException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "邮箱地址或授权码错误!", "警告", JOptionPane.WARNING_MESSAGE);
                } catch (MessagingException ex) {
                    JOptionPane.showMessageDialog(null, "网络连接超时!", "警告", JOptionPane.WARNING_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        //给table添加双击事件
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    //获得双击选择的行数
                    int row = table.rowAtPoint(e.getPoint());
                    //获得双击选中的行的第一列数据，编号
                    id = (int) table.getValueAt(row, 0);
                    //获得双击选中的行的第五列数据，是否含有附件
                    String yon = (String) table.getValueAt(row, 4);
                    //判断邮件是否含有附件，选择不同的查看内容界面
                    if (yon == "是") {
                        new MailContentDownload();
                    } else {
                        new MailContent();
                    }
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
        //返回菜单界面
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
        this.setTitle("收件箱");
        this.setVisible(true);
        this.setSize(600, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //去掉html相关标签正则表达式方法
    public static String delHTMLTag(String inputString) {
        //含html标签的字符串
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
        //定义HTML标签的正则表达式
        String regEx_html = "<[^>]+>";
        //定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        String regEx_special = "\\&[a-zA-Z]{1,10};";
        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        m_script = p_script.matcher(htmlStr);
        //过滤script标签
        htmlStr = m_script.replaceAll("");
        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        m_style = p_style.matcher(htmlStr);
        //过滤style标签
        htmlStr = m_style.replaceAll("");
        p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        m_html = p_html.matcher(htmlStr);
        //过滤html标签
        htmlStr = m_html.replaceAll("");
        p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
        m_special = p_special.matcher(htmlStr);
        //过滤特殊标签
        htmlStr = m_special.replaceAll("");
        textStr = htmlStr;
        return textStr;
    }
}