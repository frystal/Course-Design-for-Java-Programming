import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.net.*;
import java.io.*;


class Window extends JFrame implements ActionListener
{

    public Init_soc soc;
    public PrintWriter pw;
    public String username;
    public String password;
    public String md5;
    public String host;
    public int port;
    public String message;

    public BufferedReader br;
    public String msg;
    public Socket socket;

    static DefaultListModel listModel = new DefaultListModel();
    static JList list = new JList(listModel);
    JScrollPane list_scrol = new JScrollPane(list);
    JButton refresh = new JButton("刷新");

    static JTextArea mainchat_con = new JTextArea(12, 30);
    JScrollPane mainchat_scrol = new JScrollPane(mainchat_con);

    JLabel target = new JLabel("发送对象: ");
    static JComboBox target_list = new JComboBox();

    JTextField send_con = new JTextField(15);
    JButton send = new JButton("发送");

    Box boxv1 = null;
    Box boxv2 = null;
    Box boxv3 = null;
    Box boxv4 = null;

    Box base1 = null;
    Box base2 = null;

    Crypto cry = new Crypto();


    public Window(String users, Socket socket)
    {    //. 初始化窗体,并进行发送和监听

        String[] temp = users.split("!new");
        String[] user = temp[0].split(":");

        this.username = user[0];
        this.password = user[1];
        this.md5 = cry.MD5(temp[0]);
        this.socket = socket;

        setLayout(new FlowLayout());

        send.addActionListener(this);
        refresh.addActionListener(this);
        mainchat_con.setEditable(false);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(10);
        list.setFixedCellHeight(20);
        list.setFixedCellWidth(100);

        boxv1 = Box.createVerticalBox();
        boxv1.add(Box.createVerticalStrut(8));
        boxv1.add(list_scrol);
        boxv1.add(Box.createVerticalStrut(10));
        boxv1.add(refresh);
        boxv1.setBorder(new TitledBorder("好友列表"));

        target_list.addItem("all");
        boxv4 = Box.createHorizontalBox();
        boxv4.add(target);
        boxv4.add(Box.createHorizontalStrut(8));
        boxv4.add(target_list);

        boxv3 = Box.createHorizontalBox();
        boxv3.add(send_con);
        boxv3.add(Box.createHorizontalStrut(8));
        boxv3.add(send);

        boxv2 = Box.createVerticalBox();
        boxv2.add(Box.createVerticalStrut(8));
        boxv2.add(mainchat_scrol);
        boxv2.add(Box.createVerticalStrut(15));
        boxv2.add(boxv4);
        boxv2.add(Box.createVerticalStrut(8));
        boxv2.add(boxv3);
        boxv2.setBorder(new TitledBorder("公频"));

        base1 = Box.createHorizontalBox();
        base1.add(Box.createHorizontalStrut(10));
        base1.add(boxv1);
        base1.add(Box.createHorizontalStrut(10));
        base1.add(boxv2);
        add(base1);

        this.setSize(530, 370);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("客户端");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                try
                {
                    PrintWriter pw = new PrintWriter(socket.getOutputStream());
                    pw.println("!offline!");
                    pw.println(cry.encrypt(username + ":leave chatroom", md5));
                    pw.flush();
                    dispose();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
        try
        {
            Thread t = new Thread(new Listen(socket, users));  // 启动监听进程
            t.start();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void actionPerformed(ActionEvent event) // 发送消息
    {
        send.setText("发送");
        refresh.setText("刷新");
        try
        {
            pw = new PrintWriter(soc.socket.getOutputStream());
            if (event.getActionCommand().equals("发送"))
            {
                if (!send_con.getText().equals(""))
                {
                    String name = String.valueOf(target_list.getSelectedItem());
                    message = "from " + username + " to " + name + " : " + send_con.getText();
                    pw.println("!message!");
                    String enc = cry.encrypt(username + ":" + name + "!message!" + message, md5);
                    pw.println(enc);
                    pw.flush();
                }
            }
            else if (event.getActionCommand().equals("刷新"))
            {
                pw = new PrintWriter(soc.socket.getOutputStream());
                pw.println("!refresh");
                pw.flush();
            }
        } catch (Exception e) { e.printStackTrace(); }
        send_con.setText("");
    }


    class Listen implements Runnable  // 监听服务器端消息
    {
        public String username;
        public String password;
        public String md5;
        private Socket socket;
        private BufferedReader keybord;
        public BufferedReader br;
        private PrintWriter pw;
        private String msg;
        Crypto cry = new Crypto();

        public Listen(Socket socket, String users)
        {
            try
            {
                String[] temp = users.split("!new");
                String[] user = temp[0].split(":");
                this.socket = socket;
                this.username = user[0];
                this.password = user[1];
                this.md5 = cry.MD5(temp[0]);
            } catch (Exception e) { e.printStackTrace(); }
        }

        public void run()  // 开始监听
        {
            try
            {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((msg = br.readLine()) != null)
                {
                    String message = cry.decrypt(msg, md5);
                    if (message.equals("!friendlist!"))
                    {
                        listModel.clear();
                        target_list.removeAllItems();
                        target_list.addItem("all");
                        message = br.readLine();
                        message = cry.decrypt(message, md5);
                        String[] str = message.split(":");//将接收到的所有用户信息分隔开
                        for (String ss : str)
                        {
                            listModel.addElement(ss);
                            target_list.addItem(ss);
                        }
                    }
                    else if (message.equals("!message!"))
                    {
                        message = cry.decrypt(br.readLine(), md5);
                        mainchat_con.append(message + "\n");
                    }
                    else if (message.equals("!chatroom!"))
                    {
                        message = cry.decrypt(br.readLine(), md5);
                        mainchat_con.append(message + "\n");
                    }
                    else if (message.equals("!refresh!"))
                    {
                        listModel.clear();
                        target_list.removeAllItems();
                        target_list.addItem("all");
                        message = br.readLine();
                        message = cry.decrypt(message, md5);
                        String[] sr = message.split(":");
                        for (String sst : sr)
                        {
                            listModel.addElement(sst);
                            target_list.addItem(sst);
                        }
                    }
                    else if (message.equals("!offline!"))//下线
                    {
                        message = br.readLine();
                        message = cry.decrypt(message, md5);
                        mainchat_con.append(message + "\n");
                    }
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
}
