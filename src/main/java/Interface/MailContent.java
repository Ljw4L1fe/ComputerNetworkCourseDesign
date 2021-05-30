package Interface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MailContent {
    //定义Swing组件
    JFrame jf=new JFrame("邮件");
    JLabel jl=new JLabel("邮件内容：");
    JTextArea jta=new JTextArea();
    JScrollPane jsp = new JScrollPane();
    JPanel jp0=new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

    public MailContent(){
        //定义Swing组件布局
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        jf.setLayout(null);
        jf.setSize(700, 600);
        jf.setLocation(400, 150);
        jta.setLineWrap(true);
        jp0.add(jl);
        jp1.add(jta);
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
    }

    public static void main(String[] args) {
        new MailContent();
    }
}
