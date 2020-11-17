import java.net.*;
import java.io.*;
import java.util.*;


public class Server
{
    private int PORT=1234;
    private ServerSocket server;
    public static ArrayList<User> user_list =new ArrayList<User>();//定义用户集合
    public User user_ini;
    Crypto cry = new Crypto();

    public void init()
    {
        try
        {
            FileInputStream fis=new FileInputStream(System.getProperty("user.dir")+"\\1");  // 打开存贮的固定用户信息
            InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line= null;
            String[] data;
            line=br.readLine();
            while (line!=null)
            {
                data=line.split(":");
                user_ini =new User(data[0]);
                user_ini.setMd5(data[1]);
                user_list.add(user_ini);
                line=br.readLine();
            }
            br.close();
            isr.close();
            fis.close();
        }catch(Exception ex){ ex.printStackTrace(); }
    }

    public void getServer(Control control)
    {
        try{
            server=new ServerSocket(PORT);
            System.out.println("服务器启动，开始监听......");   //初始化服务器端
            while(true)
            {
                Socket client=server.accept();
                if(checker(client))
                {
                    Thread t = new Thread(new Chat(client,control));
                    t.start();
                }
            }
        }catch(Exception ex){ ex.printStackTrace(); }
    }

    public static void main(String[] args)
    {
        Control control = new Control();
        Server ser = new Server();
        ser.init();
        ser.getServer(control);
    }

    public boolean checker(Socket socket)   // 检测登陆和注册
    {
        try
        {
            PrintWriter pw =new PrintWriter(socket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String enc = br.readLine();
            String[] temp = enc.split("!new");
            if(temp.length==1)   // 登陆
            {
                String md5 = "";
                Iterator<User> it = Server.user_list.iterator();
                while (it.hasNext())
                {
                    User user = it.next();
                    md5 = user.getMD5();
                    if (md5.equals(enc))
                    {
                        pw.println("!OK!");
                        pw.flush();
                        user.setStat("online");
                        user.setSock(socket);
                        user.setPw(pw);
                        return true;
                    }
                }
                pw.println("!WRONG!");
                pw.flush();
            }
            else  // 注册
            {
                Iterator<User> it = user_list.iterator();
                String[] temp_user = temp[0].split(":");
                while (it.hasNext())
                {
                    User user = it.next();
                    if (user.getName().equals(temp_user[0]))
                    {
                        pw.println("!WRONG!");
                        pw.flush();
                        return false;
                    }
                }
                User reg_user = new User(temp_user[0]);
                reg_user.setMd5(cry.MD5(temp[0]));
                reg_user.setStat("online");
                reg_user.setSock(socket);
                reg_user.setPw(pw);
                user_list.add(reg_user);
                pw.println("!OK!");
                pw.flush();
                return true;
            }
        }catch(Exception ex){ ex.printStackTrace(); }
        return false;
    }


    class Chat implements Runnable
    {
        Socket socket;
        Control control;
        private BufferedReader br;
        private PrintWriter pw;
        private String rec_msg;
        private String msg_long ="";
        User chat_user;

        public Chat(Socket socket,Control control)  // 初始化每个用户的信息
        {
            try{
                this.br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.pw =new PrintWriter(socket.getOutputStream());
                rec_msg = br.readLine();
                Iterator<User> it=Server.user_list.iterator();
                while(it.hasNext())
                {
                    User use=it.next();
                    if(use.getName().equals(rec_msg))
                    {
                       this.chat_user = use;
                       chat_user.setSock(socket);
                       chat_user.setPw(pw);
                    }
                }
                this.socket=socket;
                this.control = control;
            }catch(Exception ex){ ex.printStackTrace(); }
        }

        public void run()  // 运行监听客户端信息
        {
            try{
                while((rec_msg=br.readLine())!=null)
                {
                    switch (rec_msg)
                    {
                        case("!friendlist!"): // 更新好友列表
                        {
                            Iterator<User> it=Server.user_list.iterator();
                            while(it.hasNext())
                            {
                                User use = it.next();
                                if (use.getStat().equals("online"))
                                {
                                    rec_msg = use.getName() + ":";
                                    msg_long += rec_msg;
                                }
                            }
                            SendMessage("!friendlist!");
                            SendMessage(msg_long);
                            control.fresh();
                            break;
                        }
                        case("!chatroom!"):  // 进入聊天室标识
                        {
                            rec_msg = ReceiveMessage(br.readLine(),chat_user);
                            SendMessage("!chatroom!");
                            SendMessage(rec_msg);
                            break;
                        }
                        case("!message!"):  //发送信息
                        {
                            rec_msg = ReceiveMessage(br.readLine(),chat_user);
                            String[] rec=rec_msg.split("!message!");
                            String[] rec_username=rec[0].split(":");
                            if(!rec_username[1].equals("all"))
                            {
                                Iterator<User> iu = Server.user_list.iterator();//遍历用户集合
                                while (iu.hasNext())
                                {
                                    User se = iu.next();
                                    if (rec_username[1].equals(se.getName()))
                                    {
                                        try
                                        {
                                            PrintWriter pwriter = se.getPw();
                                            pwriter.println(cry.encrypt("!message!", se.getMD5()));
                                            pwriter.println(cry.encrypt(rec[1], se.getMD5()));
                                            pwriter.flush();
                                        } catch (Exception ex) { ex.printStackTrace(); }
                                    }
                                    else if (rec_username[0].equals(se.getName()))
                                    {
                                        try
                                        {
                                            PrintWriter pwr = chat_user.getPw();
                                            pwr.println(cry.encrypt("!message!", chat_user.getMD5()));
                                            pwr.println(cry.encrypt(rec[1], chat_user.getMD5()));
                                            pwr.flush();
                                        } catch (Exception ex) { ex.printStackTrace(); }
                                    }
                                }
                            }
                            else
                            {
                                SendMessage("!message!");
                                SendMessage(rec[1]);
                            }
                            break;
                        }
                        case("!offline!"):
                        {
                            rec_msg = ReceiveMessage(br.readLine(),chat_user);
                            SendMessage("!offline!");
                            SendMessage(rec_msg);
                            String msg_user = rec_msg.split(":")[0];
                            Iterator<User> at=Server.user_list.iterator();
                            control.users_con.setText("");
                            while(at.hasNext())
                            {
                                User use=at.next();
                                if(use.getName().equals(msg_user))
                                {
                                    use.setStat("offline");
                                    use.getSock().close();
                                }
                            }
                            control.fresh();
                            break;
                        }
                        case("!refresh!"):
                        {
                            String msg_all="";
                            Iterator<User> iter=Server.user_list.iterator();
                            while(iter.hasNext())
                            {
                                User uus=iter.next();
                                rec_msg=uus.getName();
                                msg_all+=rec_msg;
                            }
                            SendMessage("!refresh!");
                            SendMessage(msg_all);
                            break;
                        }

                    }
                }
            }catch(IOException ex){ ex.printStackTrace(); }
        }
    }

    public void SendMessage(String message) // 发送加密信息
    {
        try{
            Iterator<User> users=user_list.iterator();
            while(users.hasNext())
            {
                User user =  users.next();
                if(user.getStat().equals("online"))
                {
                    PrintWriter pw = user.getPw();
                    message = cry.encrypt(message,user.getMD5());
                    pw.println(message);
                    pw.flush();
                }
            }
        }catch(Exception ex){ ex.printStackTrace(); }
    }

    public String ReceiveMessage(String message,User user) // 接受信息解密
    {
        return cry.decrypt(message,user.getMD5());
    }

}
