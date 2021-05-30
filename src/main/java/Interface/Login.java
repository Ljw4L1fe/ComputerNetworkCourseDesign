package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class Login {
    //定义Swing组件
    JFrame jf = new JFrame("登陆界面");
    JLabel jl1 = new JLabel("邮箱账号：");
    JLabel jl2 = new JLabel("邮箱授权码：");
    JButton jb1 = new JButton("登陆");
    JButton jb2 = new JButton("注册");
    public static JTextField jtf1 = new JTextField();
    public static JPasswordField jpf2 = new JPasswordField();

    public Login() {
        //定义Swing组件布局
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(null);
        jf.setSize(400, 300);
        jf.setLocationRelativeTo(null);
        jf.add(jl1);
        jf.add(jtf1);
        jf.add(jl2);
        jf.add(jpf2);
        jf.add(jb1);
        jf.add(jb2);
        jl1.setBounds(20, 30, 100, 50);
        jl1.setFont(new Font("Dialog", 1, 15));
        jtf1.setBounds(100, 45, 260, 25);
        jl2.setBounds(20, 90, 100, 50);
        jl2.setFont(new Font("Dialog", 1, 15));
        jpf2.setBounds(110, 105, 250, 25);
        jb1.setBounds(70, 170, 100, 50);
        jb2.setBounds(220, 170, 100, 50);
        jf.setVisible(true);
        //登陆功能
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
                    String sql = "SELECT * FROM user WHERE address=? AND password=?";
                    //使用PrepareStatement的方式注入sql方法
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setObject(1, jtf1.getText());
                    ps.setObject(2, String.valueOf(jpf2.getPassword()));
                    ps.execute();
                    ResultSet re = ps.getResultSet();
                    //如果有数据，re.next()返回true
                    if (re.next()) {
                        jf.setVisible(false);
                        new Menu();
                    } else {
                        JOptionPane.showMessageDialog(null, "账号或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                    re.close();
                    ps.close();
                } catch (ClassNotFoundException | SQLException a) {
                    a.printStackTrace();
                }
            }
        });
        //跳转注册界面
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.dispose();
                new Register();
            }
        });
    }

    public static void main(String[] args) {
        new Login();
    }
}