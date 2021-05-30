package Interface;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaMailSendInterface {
    //定义Swing组件
    JFrame jf = new JFrame("发送邮件界面");
    JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jp3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jp4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel jp5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jp6 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JLabel jl1 = new JLabel("收件人地址：");
    JLabel jl2 = new JLabel("邮件主题：    ");
    JLabel jl3 = new JLabel("邮件内容");
    JLabel jl4 = new JLabel("未选择文件");
    JTextField jtf1 = new JTextField(50);
    JTextField jtf2 = new JTextField(50);
    JButton jb1 = new JButton("发送");
    JButton jb2 = new JButton("返回");
    JButton jb3 = new JButton("选择文件");
    JTextArea jta = new JTextArea(25, 60);
    JScrollPane jsp = new JScrollPane();
    JFileChooser jfc = new JFileChooser();
    //邮箱类型
    String type;

    public JavaMailSendInterface() {
        //定义Swing组件布局
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(null);
        jf.setSize(700, 650);
        jf.setLocationRelativeTo(null);
        jta.setLineWrap(true);
        jp1.add(jl1);
        jp1.add(jtf1);
        jp2.add(jl2);
        jp2.add(jtf2);
        jp3.add(jl3);
        jp4.add(jta);
        jp5.add(jb3);
        jp5.add(jl4);
        jp6.add(jb1);
        jp6.add(jb2);
        jp4.setBorder(new EmptyBorder(5, 5, 5, 5));
        jp4.setLayout(new BorderLayout(0, 0));
        jsp.setViewportView(jta);
        jp4.add(jsp);
        Box vBox = Box.createVerticalBox();
        vBox.add(jp1);
        vBox.add(jp2);
        vBox.add(jp3);
        vBox.add(jp4);
        vBox.add(jp5);
        vBox.add(jp6);
        jf.setContentPane(vBox);
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
                //判断邮箱类型，选择适合的host
                if (type.equals("QQ")) {
                    String check0 = "^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\\.[a-z]{2,}$";
                    Pattern regex0 = Pattern.compile(check0);
                    Matcher matcher0 = regex0.matcher(jtf1.getText());
                    boolean isMatched0 = matcher0.matches();
                    if (isMatched0) {
                        if (jtf2.getText().length() != 0) {
                            if (jta.getText().length() != 0) {
                                //收件人邮箱
                                String to = jtf1.getText();
                                //发件人邮箱
                                String from = Login.jtf1.getText();
                                //获取系统属性
                                Properties properties = new Properties();
                                //发送邮件协议
                                properties.setProperty("mail.transport.protocol", "SMTP");
                                //设置邮件服务器
                                properties.setProperty("mail.host", "smtp.qq.com");
                                //设置邮件服务器端口
                                properties.setProperty("mail.smtp.port", "465");
                                //开启SSL加密
                                MailSSLSocketFactory sf = null;
                                try {
                                    sf = new MailSSLSocketFactory();
                                } catch (GeneralSecurityException generalSecurityException) {
                                    generalSecurityException.printStackTrace();
                                }
                                assert sf != null;
                                sf.setTrustAllHosts(true);
                                properties.put("mail.smtp.ssl.enable", "true");
                                properties.put("mail.smtp.ssl.socketFactory", sf);
                                //设置邮件服务器是否需要登录认证
                                properties.setProperty("mail.smtp.auth", "true");
                                //验证账号及授权码
                                Authenticator auth = new Authenticator() {
                                    public PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication(Login.jtf1.getText(), String.valueOf(Login.jpf2.getPassword()));
                                    }
                                };
                                // 获取默认session对象
                                Session session = Session.getInstance(properties, auth);
                                try {
                                    //创建默认的 MimeMessage 对象
                                    MimeMessage message = new MimeMessage(session);
                                    //头部头字段
                                    message.setFrom(new InternetAddress(from));
                                    //邮件接收人
                                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                                    //主题名称
                                    message.setSubject(jtf2.getText());
                                    //创建多重消息
                                    Multipart multipart = new MimeMultipart();
                                    //创建正文部分
                                    BodyPart messageBodyPart1 = new MimeBodyPart();
                                    //消息内容
                                    messageBodyPart1.setText(jta.getText());
                                    //将正文部分添加进多重消息中
                                    multipart.addBodyPart(messageBodyPart1);
                                    //判断是否选择了附件
                                    if (jfc.getSelectedFile() != null) {
                                        //创建附件部分
                                        BodyPart messageBodyPart2 = new MimeBodyPart();
                                        //设置附件数据源
                                        DataSource source = new FileDataSource(new File(jfc.getSelectedFile().getAbsolutePath()));
                                        messageBodyPart2.setDataHandler(new DataHandler(source));
                                        //设置附件的文件名
                                        messageBodyPart2.setFileName(jfc.getSelectedFile().getName());
                                        //将附件部分添加进多重消息中
                                        multipart.addBodyPart(messageBodyPart2);
                                    }
                                    //设置邮件的消息部分
                                    message.setContent(multipart);
                                    //发送消息
                                    Transport.send(message);
                                    JOptionPane.showMessageDialog(null, "邮件发送成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                } catch (MessagingException | NullPointerException mex) {
                                    JOptionPane.showMessageDialog(null, "邮件发送失败！", "错误", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "邮件正文为空！", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "邮件主题为空！", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "收件人邮箱地址格式错误！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    String check0 = "^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\\.[a-z]{2,}$";
                    Pattern regex0 = Pattern.compile(check0);
                    Matcher matcher0 = regex0.matcher(jtf1.getText());
                    boolean isMatched0 = matcher0.matches();
                    if (isMatched0) {
                        if (jtf2.getText() != null) {
                            if (jta.getText() != null) {
                                //收件人邮箱
                                String to = jtf1.getText();
                                //发件人邮箱
                                String from = Login.jtf1.getText();
                                //获取系统属性
                                Properties properties = new Properties();
                                //发送邮件协议
                                properties.setProperty("mail.transport.protocol", "SMTP");
                                //设置邮件服务器
                                properties.setProperty("mail.host", "smtp.163.com");
                                //设置邮件服务器端口
                                properties.setProperty("mail.smtp.port", "465");
                                //开启SSL加密
                                MailSSLSocketFactory sf = null;
                                try {
                                    sf = new MailSSLSocketFactory();
                                } catch (GeneralSecurityException generalSecurityException) {
                                    generalSecurityException.printStackTrace();
                                }
                                assert sf != null;
                                sf.setTrustAllHosts(true);
                                properties.put("mail.smtp.ssl.enable", "true");
                                properties.put("mail.smtp.ssl.socketFactory", sf);
                                //设置邮件服务器是否需要登录认证
                                properties.setProperty("mail.smtp.auth", "true");
                                // 验证账号及授权码
                                Authenticator auth = new Authenticator() {
                                    public PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication(Login.jtf1.getText(), String.valueOf(Login.jpf2.getPassword()));
                                    }
                                };
                                //获取默认session对象
                                Session session = Session.getInstance(properties, auth);
                                try {
                                    //创建默认的 MimeMessage 对象
                                    MimeMessage message = new MimeMessage(session);
                                    //头部头字段
                                    message.setFrom(new InternetAddress(from));
                                    //邮件接收人
                                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                                    //主题名称
                                    message.setSubject(jtf2.getText());
                                    //创建多重消息
                                    Multipart multipart = new MimeMultipart();
                                    //创建正文部分
                                    BodyPart messageBodyPart1 = new MimeBodyPart();
                                    //消息内容
                                    messageBodyPart1.setText(jta.getText());
                                    //将正文部分添加进多重消息中
                                    multipart.addBodyPart(messageBodyPart1);
                                    //判断是否选择了附件
                                    if (jfc.getSelectedFile() != null) {
                                        //创建附件部分
                                        BodyPart messageBodyPart2 = new MimeBodyPart();
                                        //设置附件数据源
                                        DataSource source = new FileDataSource(new File(jfc.getSelectedFile().getAbsolutePath()));
                                        messageBodyPart2.setDataHandler(new DataHandler(source));
                                        //设置附件的文件名
                                        messageBodyPart2.setFileName(jfc.getSelectedFile().getName());
                                        //将附件部分添加进多重消息中
                                        multipart.addBodyPart(messageBodyPart2);
                                    }
                                    //设置邮件的消息部分
                                    message.setContent(multipart);
                                    //发送消息
                                    Transport.send(message);
                                    JOptionPane.showMessageDialog(null, "邮件发送成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                } catch (MessagingException | NullPointerException mex) {
                                    JOptionPane.showMessageDialog(null, "邮件发送失败！", "错误", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "邮件正文为空！", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "邮件主题为空！", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "收件人邮箱地址格式错误！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        //返回菜单界面
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.dispose();
                new Menu();
            }
        });
        //选择附件功能
        jb3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int val = jfc.showOpenDialog(null);
                if (val == jfc.APPROVE_OPTION) {
                    jl4.setText("选择了文件：" + jfc.getSelectedFile().getName());
                }
            }
        });
    }

    public static void main(String[] args) {
        new JavaMailSendInterface();
    }

}
