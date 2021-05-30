package Interface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JavaMailSendInterface {
    JFrame jf = new JFrame("发送邮件界面");
    JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jp3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jp4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel jp5 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JLabel jl1 = new JLabel("收件人地址：");
    JLabel jl2 = new JLabel("邮件主题：");
    JLabel jl3 = new JLabel("邮件内容");
    JTextField jtf1 = new JTextField(50);
    JTextField jtf2 = new JTextField(50);
    JButton jb1=new JButton("发送");
    JButton jb2=new JButton("返回");
    JTextArea jta = new JTextArea(25, 60);
    JScrollPane jsp=new JScrollPane();

    public JavaMailSendInterface() {
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(null);
        jf.setSize(700, 600);
        jta.setLineWrap(true);
        jp1.add(jl1);
        jp1.add(jtf1);
        jp2.add(jl2);
        jp2.add(jtf2);
        jp3.add(jl3);
        jp4.add(jta);
        jp5.add(jb1);
        jp5.add(jb2);
        jp4.setBorder(new EmptyBorder(5,5,5,5));
        jp4.setLayout(new BorderLayout(0,0));
        jsp.setViewportView(jta);
        jp4.add(jsp);
        Box vBox = Box.createVerticalBox();
        vBox.add(jp1);
        vBox.add(jp2);
        vBox.add(jp3);
        vBox.add(jp4);
        vBox.add(jp5);
        jf.setContentPane(vBox);
    }

    public static void main(String[] args) {
new JavaMailSendInterface();
    }
}
