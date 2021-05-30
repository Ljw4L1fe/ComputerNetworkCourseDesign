package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register {
    //定义Swing组件
    JFrame jf = new JFrame("注册界面");
    JLabel jl1 = new JLabel("邮箱账号：");
    JLabel jl2 = new JLabel("邮箱类型：");
    JLabel jl3 = new JLabel("授权码密码：");
    JLabel jl4 = new JLabel("确认授权码：");
    JButton jb1 = new JButton("注册");
    JButton jb2 = new JButton("重置");
    JButton jb3 = new JButton("返回");
    JTextField jtf1 = new JTextField(19);
    JPasswordField jpf1 = new JPasswordField(17);
    JPasswordField jpf2 = new JPasswordField(17);
    ButtonGroup bg = new ButtonGroup();
    JRadioButton jrb1 = new JRadioButton("QQ");
    JRadioButton jrb2 = new JRadioButton("163");

    public Register() {
        //定义Swing组件布局
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(null);
        jf.setSize(400, 300);
        jf.setLocationRelativeTo(null);
        jf.add(jl1);
        jf.add(jtf1);
        jf.add(jl2);
        jf.add(jpf1);
        jf.add(jl3);
        jf.add(jpf2);
        jf.add(jb1);
        jf.add(jb2);
        jf.add(jb3);
        bg.add(jrb1);
        bg.add(jrb2);
        jf.add(jl4);
        jf.add(jrb1);
        jf.add(jrb2);
        jl1.setBounds(20, 10, 100, 50);
        jl1.setFont(new Font("Dialog", 1, 13));
        jtf1.setBounds(85, 24, 250, 25);
        jtf1.setFont(new Font("Dialog", 1, 13));
        jl2.setBounds(65, 45, 100, 50);
        jl2.setFont(new Font("Dialog", 1, 13));
        jrb1.setBounds(150, 48, 100, 40);
        jrb1.setFont(new Font("Dialog", 1, 13));
        jrb2.setBounds(260, 48, 100, 40);
        jrb2.setFont(new Font("Dialog", 1, 13));
        jl3.setBounds(20, 75, 100, 50);
        jl3.setFont(new Font("Dialog", 1, 13));
        jpf1.setBounds(99, 90, 238, 25);
        jpf1.setFont(new Font("Dialog", 1, 13));
        jl4.setBounds(20, 120, 100, 50);
        jl4.setFont(new Font("Dialog", 1, 13));
        jpf2.setBounds(99, 134, 238, 25);
        jb1.setBounds(60, 190, 80, 40);
        jb1.setFont(new Font("Dialog", 1, 13));
        jb2.setBounds(155, 190, 80, 40);
        jb2.setFont(new Font("Dialog", 1, 13));
        jb3.setBounds(250, 190, 80, 40);
        jb3.setFont(new Font("Dialog", 1, 13));
        jf.setVisible(true);
        //注册功能
        jb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //验证邮箱地址格式正则表达式
                String check1 = "[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+";
                Pattern regex1 = Pattern.compile(check1);
                Matcher matcher1 = regex1.matcher(jtf1.getText());
                boolean isMatched1 = matcher1.matches();
                //验证邮箱地址格式
                if (isMatched1) {
                    //验证是否选择了邮箱类型
                    if ((jrb1.isSelected() || jrb2.isSelected())) {
                        //验证密码是否为空
                        if (jpf1.getPassword().length != 0 && jpf2.getPassword().length != 0) {
                            //验证输入的两次密码是否一致
                            if (Arrays.equals(jpf1.getPassword(), jpf2.getPassword())) {
                                Connection conn;
                                try {
                                    //加载驱动程序
                                    Class.forName("com.mysql.cj.jdbc.Driver");
                                    //连接数据库
                                    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=UTC", "root", "7355608");
                                    //对数据库进行操作
                                    String sql1 = "INSERT INTO user (address, password, mailtype) VALUES (?,?,?)";
                                    String sql2 = "SELECT * FROM user WHERE address=?";
                                    //使用PrepareStatement的方式注入sql方法
                                    PreparedStatement ps1 = conn.prepareStatement(sql1);
                                    PreparedStatement ps2 = conn.prepareStatement(sql2);
                                    ps1.setObject(1, jtf1.getText());
                                    ps1.setObject(2, String.valueOf(jpf1.getPassword()));
                                    ps2.setObject(1, jtf1.getText());
                                    ps2.execute();
                                    ResultSet re2 = ps2.getResultSet();
                                    //判断哪个单选框被选中
                                    if (jrb1.isSelected()) {
                                        ps1.setObject(3, jrb1.getText());
                                    } else {
                                        ps1.setObject(3, jrb2.getText());
                                    }
                                    //若邮箱地址已在数据库中存在，则证明该邮箱已被注册
                                    if (re2.next()) {
                                        ps1.close();
                                        JOptionPane.showMessageDialog(null, "该邮箱地址已被注册！", "错误", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        ps1.execute();
                                        ps1.close();
                                        jf.dispose();
                                        new Login();
                                    }
                                } catch (ClassNotFoundException | SQLException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "两次输入的密码不相同！", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "请选择邮箱类型！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "邮箱地址格式不正确！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //重置输入框和单选框
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtf1.setText(null);
                jpf1.setText(null);
                jpf2.setText(null);
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
    }

    public static void main(String[] args) {
        new Register();
    }
}
