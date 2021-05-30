package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu {
    //定义Swing组件
    JFrame jf = new JFrame("菜单");
    JLabel jl1 = new JLabel("附件保存路径：");
    static JLabel jl2 = new JLabel("D:\\myfile");
    JButton jb1 = new JButton("发送邮件");
    JButton jb2 = new JButton("接收邮件");
    JButton jb3 = new JButton("返回");
    JButton jb4 = new JButton("保存路径");
    JFileChooser jfc = new JFileChooser();

    public Menu() {
        //定义Swing组件布局
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(null);
        jf.setSize(400, 300);
        jf.setLocationRelativeTo(null);
        jf.add(jb1);
        jf.add(jb2);
        jf.add(jb3);
        jf.add(jb4);
        jf.add(jl1);
        jf.add(jl2);
        jb1.setBounds(19, 50, 100, 50);
        jb2.setBounds(144, 50, 100, 50);
        jb3.setBounds(267, 50, 100, 50);
        jb4.setBounds(20, 150, 100, 50);
        jl1.setBounds(130, 150, 90, 50);
        jl2.setBounds(210, 150, 185, 50);
        jf.setVisible(true);
        //跳转发送邮件界面
        jb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.setVisible(false);
                new JavaMailSendInterface();
            }
        });
        //跳转接收邮件界面
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.setVisible(false);
                new JavaMailReceiveInterface().run();
            }
        });
        //返回登录界面
        jb3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.dispose();
                new Login();
            }
        });
        //选择附件保存路径
        jb4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int val = jfc.showOpenDialog(null);
                if (val == jfc.APPROVE_OPTION) {
                    jl2.setText(jfc.getCurrentDirectory().getAbsolutePath());
                }
            }
        });
    }

    public static void main(String[] args) {
        new Menu();
    }
}
