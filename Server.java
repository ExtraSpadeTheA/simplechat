import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//import javafx.beans.binding.SetBinding;

public class Server {
    private static List<Socket> clients = new ArrayList<>();

    private void start(){
        try{
            ServerSocket server = new ServerSocket(5555);//创建绑定到特定端口的服务器套接字
            System.out.println("服务开启,等待客户端连接中...");
            while(true){
                Socket client = server.accept();
                System.out.println("客户端[" + client.getRemoteSocketAddress()+"]连接成功,当前在线用户" + clients.size() + "个");
                clients.add(client);
                //为每个客户端开启一个线程处理
                new MessageListener(client).start();
            }
        } catch (IOException e) {
        }
    }

// 消息的处理线程
class MessageListener extends Thread {
    // 将连接上的客户端传递进来
    private Socket client;

    private OutputStream os;
    private PrintWriter pw;
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader br;

    public MessageListener(Socket socket) {
        this.client = socket;
    }

    @Override
    public void run() {
        try {
            sendMsg(0, "[系统消息]：欢迎" + client.getRemoteSocketAddress() + "来到聊天室,当前共有" + clients.size() + "人在聊天");
            while (true) {
                sendMsg(1, "[" + client.getRemoteSocketAddress() + "]" + receiveMsg());
            }
        } catch (IOException e) {
           
        }
    }

    private void sendMsg(int type, String msg) throws IOException {
        if (type != 0) {
            System.out.println("处理消息:" + msg);
        }
        for (Socket socket : clients) {
            if (type != 0 && socket == client) {
                continue;
            }
            os = socket.getOutputStream();
            pw = new PrintWriter(os);
            pw.println(msg);// 使用readline获取消息,必须使用print而非write
            pw.flush();// 立即将缓冲区内消息输出
        }
    }

    private String receiveMsg() throws IOException {
        is = client.getInputStream();
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        return br.readLine();
    }
}

public static void main(String[] args){
    new Server().start();
}
}