import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;

public class NIOHttpServer {
    static final File WEB_ROOT = new File(".");
    static final String DEFAULT_FILE = "src/index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";

    private int PORT = 8080;
    private boolean running = false;
    private Thread runThread = null;

    public NIOHttpServer(int port) {
        PORT = port;

        runThread = new Thread(()->{
            ServerSocketChannel serverSocketChannel = null;

            try {
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(true);
                serverSocketChannel.bind(new InetSocketAddress(PORT));

                while (running) {
                    System.out.println("[연결 기다림]");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    InetSocketAddress isa = (InetSocketAddress) socketChannel.getRemoteAddress();
                    System.out.println("[연결 수락함] " + isa.getHostName());

                    ByteBuffer buffer = ByteBuffer.allocate(4096);
                    int byteCount = socketChannel.read(buffer);
                    System.out.println(byteCount);
                    buffer.flip();

                    Charset charset = Charset.forName("UTF-8");
                    String data = charset.decode(buffer).toString();

                    System.out.println(data);

                    byte[] data2 = "ok".getBytes();
                    String content = "HTTP/1.1 200 OK\n" +
                            "Server: Java HTTP NIO Server from hhcompany : 1.0\n" +
                            "Date: " + new Date()+
                            "Content-type: " +"text/plain" + "\n"+
                            "Content-length: " + data2.length +"\n"+
                            "\n";

                    content+= "ok";

                    ByteBuffer byteBuffer = null;
                    byteBuffer = charset.encode(content);

                    socketChannel.write(byteBuffer);
                    System.out.println("[데이터 보내기 성공]");
                }

            } catch (Exception e) {
                System.err.println("Server Connection error : " + e.getMessage());
            }

            if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                try {
                    serverSocketChannel.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void start(){
        if(!running){
            running = true;
            runThread.start();
        }
    }

    public void stop(){
        if(running){
            running = false;
        }
    }
}