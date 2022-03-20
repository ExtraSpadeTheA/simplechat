import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Created by SongFei on 2019/3/24.
 */
public class Client {

    private Socket server = null;

    private OutputStream os;
    private PrintWriter pw;
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader br;

    private void start() {
        try {
            // 连接服务器
            server = new Socket("127.0.0.1", 5555);//创建一个流socket并将其连接到指定 IP 地址的指定端口号。
            System.out.println("连接服务器成功，身份证：" + server.getLocalSocketAddress());
            // 启动接受消息的线程
            new ReceiveMessageListener().start();
            // 启动发送消息的线程
            new SendMessageListener().start();
        } catch (SocketException e) {
            System.out.println("服务器" + server.getRemoteSocketAddress() + "嗝屁了");
        } catch (IOException e) {
            // log
        }
    }

    /**
     * 发送消息的线程
     */
    class SendMessageListener extends Thread {
        @Override
        public void run() {
            try {
                // 监听idea的console输入
                Scanner scanner = new Scanner(System.in);
                // 循环处理，只要有输入内容就立即发送
                while (true) {
                    sendMsg(scanner.next());
                }
            } catch (SocketException e) {
                System.out.println("服务器" + server.getRemoteSocketAddress() + "嗝屁了");
            } catch (IOException e) {
                // log
            }
        }
    }

    /**
     * 接受消息的线程
     */
    class ReceiveMessageListener extends Thread {
        @Override
        public void run() {
            try {
                // 循环监听，除非掉线，或者服务器宕机了
                while (true) {
                    System.out.println(receiveMsg());
                }
            } catch (SocketException e) {
                System.out.println("服务器" + server.getRemoteSocketAddress() + "嗝屁了");
            } catch (IOException e) {
                // log
            }
        }
    }

    /**
     * 发送消息
     *
     * @param msg 消息内容
     * @throws IOException
     */
    private void sendMsg(String msg) throws IOException {
        os = server.getOutputStream();
        pw = new PrintWriter(os);
        pw.println(msg);// 这里需要特别注意，对方用readLine获取消息，就必须用print而不能用write，否则会导致消息获取不了
        pw.flush();
    }

    /**
     * 接受消息
     *
     * @return 消息内容
     * @throws IOException
     */
    private String receiveMsg() throws IOException {
        is = server.getInputStream();
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        return br.readLine();
    }

    public static void main(String[] args) {
        new Client().start();
    }

}