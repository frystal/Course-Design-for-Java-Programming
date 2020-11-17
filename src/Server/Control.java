import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.swing.*;

class Control extends JFrame implements ActionListener
{
    JLabel users = new JLabel("用户名列表");
    public JTextArea users_con ;
    JLabel out = new JLabel("下线用户: ");
    public JTextField out_id = new JTextField(15);
    JScrollPane scroll = new JScrollPane( users_con);
    JButton log_out = new JButton("下线");
    Server ser = new Server();
    Crypto cry = new Crypto();


    public Control()  //初始化窗体
    {
        setLayout(new FlowLayout());
        log_out.addActionListener(this);
        users_con = new JTextArea(12,42);
        users_con.setEditable(false);
        add(users);
        add(users_con);
        add(out);
        add(out_id);
        add(log_out);
        this.setSize(500,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("服务器控制");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent event)
    {
        log_out.setText("下线");
        try{
            if(event.getActionCommand().equals("下线"))
            {
                Iterator<User> at=Server.user_list.iterator();
                while(at.hasNext())
                {
                    User sr=at.next();
                    if(sr.getName().equals(out_id.getText())&&sr.getStat().equals("online"))  //匹配到用户名,就将其状态改为下线并刷新窗体
                    {
                        PrintWriter pw = sr.getPw();
                        pw.println(cry.encrypt("!message!",sr.getMD5()));
                        pw.println(cry.encrypt("you are kicked off!",sr.getMD5()));
                        pw.flush();
                        ser.SendMessage("!offline!");
                        ser.SendMessage(sr.getName()+":leave chatroom");
                        sr.getSock().close();//关闭此用户的socket
                        sr.setStat("offline");
                        JOptionPane.showMessageDialog(null, "已成功将"+out_id.getText()+"下线","success", JOptionPane.PLAIN_MESSAGE);
                        fresh();
                    }
                }
                JOptionPane.showMessageDialog(null, "该用户"+out_id.getText()+"不存在!","error", JOptionPane.PLAIN_MESSAGE);
            }
        }catch(Exception ex) { ex.printStackTrace(); }

    }
    public  void fresh()  // 刷新窗体
    {
        users_con.setText("");
        Iterator<User> it=Server.user_list.iterator();
        while(it.hasNext())
        {
            User use=it.next();
            if(use.getStat().equals("online"))
            {
                users_con.append(use.getName() + "\n");
            }
        }
    }
}
