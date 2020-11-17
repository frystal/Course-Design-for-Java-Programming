import java.awt.*;

import java.awt.event.*;

import javax.swing.*;

import javax.swing.border.*;

public class Login extends JFrame implements ActionListener
{


    public String users = null;
    public Init_soc soc;
    JLabel user = new JLabel("用户名:");
    JTextField user_con = new JTextField(15);
    JLabel pass = new JLabel("密码:");
    JPasswordField pass_con = new JPasswordField(15);

    JLabel host = new JLabel("地址:");
    JTextField host_con = new JTextField(15);
    JLabel port = new JLabel("端口:");
    JTextField port_con = new JTextField(15);

    Box boxv1 = null;
    Box boxv2 = null;
    Box boxv3 = null;
    Box base1 = null;
    Box base2 = null;

    JButton log = new JButton("登陆");
    JButton reg = new JButton("注册");

    public Login()//显示登录界面
    {
        super();
        log.addActionListener(this);
        reg.addActionListener(this);

        setLayout(new FlowLayout());

        host_con.setText("127.0.0.1");
        port_con.setText("1234");


        boxv1 =Box.createVerticalBox();
        boxv1.add(user);
        boxv1.add(Box.createVerticalStrut(12));
        boxv1.add(pass);
        boxv1.add(Box.createVerticalStrut(12));
        boxv1.add(host);
        boxv1.add(Box.createVerticalStrut(12));
        boxv1.add(port);

        boxv2 =Box.createVerticalBox();
        boxv2.add(Box.createVerticalStrut(15));
        boxv2.add(user_con);
        boxv2.add(Box.createVerticalStrut(8));
        boxv2.add(pass_con);
        boxv2.add(Box.createVerticalStrut(8));
        boxv2.add(host_con);
        boxv2.add(Box.createVerticalStrut(8));
        boxv2.add(port_con);
        boxv2.add(Box.createVerticalStrut(8));

        base1 = Box.createHorizontalBox();
        base1.add(Box.createHorizontalStrut(10));
        base1.add(boxv1);
        base1.add(Box.createHorizontalStrut(10));
        base1.add(boxv2);
        add(base1);

        boxv3 =Box.createHorizontalBox();
        boxv3.add(Box.createHorizontalStrut(10));
        boxv3.add(log);
        boxv3.add(Box.createHorizontalStrut(10));
        boxv3.add(reg);
        boxv3.add(Box.createHorizontalStrut(10));

        base2 = Box.createVerticalBox();
        base2.add(Box.createVerticalStrut(10));
        base2.add(boxv3);

        add(base2);

        this.setSize(250,230);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("客户端");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);

    }

    public void actionPerformed(ActionEvent event)//事件触发
    {
        log.setText("登陆");
        reg.setText("注册");
        if(event.getActionCommand().equals("登陆"))
        {
            if (user_con.getText().equals("")||pass_con.getPassword().equals(""))
            {
                JOptionPane.showMessageDialog(null, "请输入用户名或密码！");
            }
            else if (host_con.getText().equals("")||port_con.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "请输入目标地址和端口！");
            }
            else
            {
                users = user_con.getText() + ":" + pass_con.getText();
                soc = new Init_soc(users, host_con.getText(), Integer.valueOf(port_con.getText()));
                dispose();
            }
        }
        if(event.getActionCommand().equals("注册"))
        {
            if (user_con.getText().equals("")||pass_con.getPassword().equals(""))
            {
                JOptionPane.showMessageDialog(null, "请输入用户名或密码！");
            }
            else if (host_con.getText().equals("")||port_con.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "请输入目标地址和端口！");
            }
            else
            {
                users = user_con.getText() + ":" + pass_con.getText()+"!new!";
                soc = new Init_soc(users, host_con.getText(), Integer.valueOf(port_con.getText()));
                dispose();
            }
        }
    }

    public static void main(String[] args)
    {
        new Login();
    }

}

