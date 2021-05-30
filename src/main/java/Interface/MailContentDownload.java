package Interface;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class MailContentDownload {
    //定义Swing组件
    JFrame jf = new JFrame("邮件");
    JLabel jl = new JLabel("邮件内容：");
    JTextArea jta = new JTextArea();
    JButton jb = new JButton("下载附件");
    JScrollPane jsp = new JScrollPane();
    JPanel jp0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public MailContentDownload() {
        //定义Swing组件布局
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        jf.setLayout(null);
        jf.setSize(700, 600);
        jf.setLocation(400, 150);
        jta.setLineWrap(true);
        jp0.add(jl);
        jp1.add(jta);
        jp2.add(jb);
        jp1.setBorder(new EmptyBorder(5, 5, 5, 5));
        jp1.setLayout(new BorderLayout(0, 0));
        jsp.setViewportView(jta);
        jp1.add(jsp);
        jta.setText(JavaMailReceiveInterface.messagecontent[JavaMailReceiveInterface.id]);
        Box vBox = Box.createVerticalBox();
        vBox.add(jp0);
        vBox.add(jp1);
        vBox.add(jp2);
        jf.setContentPane(vBox);
        jf.setLocationRelativeTo(null);
        //下载附件功能
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JavaMailReceiveInterface.type.equals("163")) {//判断邮箱类型
                    String host = "pop.163.com";
                    String port = "995";
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
                        Message message = arrayMessages[JavaMailReceiveInterface.id];
                        String contentType = message.getContentType();
                        if (contentType.contains("multipart")) {
                            //若邮件消息包含multipart类型，则可能包含附件
                            //将邮件消息强制转化为multipart类型
                            Multipart multiPart = (Multipart) message.getContent();
                            //获得part数量
                            int numberOfParts = multiPart.getCount();
                            //循环邮件消息的每一个part
                            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                                //判断每一个part是否是附件
                                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                    //这个part是附件
                                    String fileName;
                                    fileName = part.getFileName();
                                    //下载附件
                                    part.saveFile(Menu.jl2.getText() + File.separator + fileName);
                                    JOptionPane.showMessageDialog(null, "附件保存在" + Interface.Menu.jl2.getText(), "提示", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                    } catch (NoSuchProviderException | IOException ex) {
                        ex.printStackTrace();
                    } catch (AuthenticationFailedException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "邮箱地址或授权码错误!", "警告", JOptionPane.WARNING_MESSAGE);
                    } catch (MessagingException ex) {
                        JOptionPane.showMessageDialog(null, "网络连接超时!", "警告", JOptionPane.WARNING_MESSAGE);
                        ex.printStackTrace();
                    }
                }else{
                    String host = "pop.qq.com";
                    String port = "995";
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
                        Message message = arrayMessages[JavaMailReceiveInterface.id];
                        String contentType = message.getContentType();
                        if (contentType.contains("multipart")) {
                            //若邮件消息包含multipart类型，则可能包含附件
                            //将邮件消息强制转化为multipart类型
                            Multipart multiPart = (Multipart) message.getContent();
                            //获得part数量
                            int numberOfParts = multiPart.getCount();
                            //循环邮件消息的每一个part
                            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                                //判断每一个part是否是附件
                                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                    //这个part是附件
                                    String fileName;
                                    fileName = part.getFileName();
                                    //下载附件
                                    part.saveFile(Menu.jl2.getText() + File.separator + fileName);
                                    JOptionPane.showMessageDialog(null, "附件保存在" + Interface.Menu.jl2.getText(), "提示", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                    } catch (NoSuchProviderException | IOException ex) {
                        ex.printStackTrace();
                    } catch (AuthenticationFailedException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "邮箱地址或授权码错误!", "警告", JOptionPane.WARNING_MESSAGE);
                    } catch (MessagingException ex) {
                        JOptionPane.showMessageDialog(null, "附件下载失败!", "警告", JOptionPane.WARNING_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        new MailContentDownload();
    }

}
