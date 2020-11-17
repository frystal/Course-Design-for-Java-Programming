import javax.swing.*;
import java.net.*;
import java.io.*;

public class Init_soc //客户端线程
{
    public static String users;
    public static String host;
    public static int port;
    public static Socket socket;
    public Init_soc(String users,String host,int port)
    {
        this.users = users;
        this.host = host;
        this.port = port;
        try{
            socket=new Socket(host,port);
            Thread t=new Thread(new Recove(socket , users));
            t.start();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception
    {
        new Init_soc(users,host,port);
    }
}


class Recove implements Runnable  // 完成对登陆状况的判断
{
    public String username;
    public String password;
    public String users;
    Login login = new Login();
    private Socket socket;
    private PrintWriter pw;
    public BufferedReader br;
    Crypto cry = new Crypto();

    public Recove(Socket socket,String users)
    {
        try{
            String[] temp = users.split("!new");
            String[] user = temp[0].split(":");
            this.users =users;
            this.socket=socket;
            this.username=user[0];
            this.password = user[1];
            this.pw =  new PrintWriter(socket.getOutputStream());//创建输出流
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void run()
    {
        if(check())
        {
            try
            {
                Window gm = new Window(users, socket);   // 启动聊天室主体
                pw.println(username);//发送用户信息
                pw.println("!friendlist!");//发送好友列表标识
                pw.flush();
                pw.println("!chatroom!");//发送进入聊天室标识
                pw.println(cry.encrypt( username + " : " + "enter the chatroom",cry.MD5(username+":"+password)));//发送进入聊天室信息
                System.out.println(cry.MD5(username+":"+password));
                pw.flush();
                login.dispose();
            }catch(Exception ex){ ex.printStackTrace(); }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "用户名或密码错误","ERROE !", JOptionPane.PLAIN_MESSAGE);
        }
    }

    public boolean check()
    {
        String[] temp = users.split("!new");
        if(temp.length==1)
        {
            String enc = cry.MD5(users);
            pw.println(enc);
            pw.flush();
            try
            {
                String ret = br.readLine();
                if (ret.equals("!OK!"))
                {
                    return true;
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(null, "用户名或密码错误","ERROE !", JOptionPane.PLAIN_MESSAGE);
        }
        else
        {
            pw.println(users);
            pw.flush();
            try
            {
                String ret = br.readLine();
                if (ret.equals("!OK!"))
                {
                    return true;
                }
            }catch (Exception ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(null, "该用户已存在!","ERROE !", JOptionPane.PLAIN_MESSAGE);
        }
        return false;
    }
}







