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
    private ServerSocketChannel serverSocketChannel = null;
    private InetSocketAddress isa = null;
    private SocketChannel socketChannel = null;
    private Charset charset = Charset.forName("UTF-8");

    public NIOHttpServer(int port) {
        PORT = port;
        setRunThread();
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

    private void setRunThread(){
        runThread = new Thread(()->{
            setUp();
            while (running) {
                accept();
                read();
                write();
            }
            close();
        });
    }

    private void setUp(){
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(true);
            serverSocketChannel.bind(new InetSocketAddress(PORT));
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    private  void close(){
        try {
            if (serverSocketChannel != null && serverSocketChannel.isOpen() && running) {
                serverSocketChannel.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void accept(){
        try {
            socketChannel = serverSocketChannel.accept();
            isa = (InetSocketAddress) socketChannel.getRemoteAddress();
            System.out.println(new Date() + " : "+isa.getHostName());
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    private void read(){
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            int byteCount = socketChannel.read(buffer);
            buffer.flip();
            charset.decode(buffer).toString();
        }
        catch ( Exception e) {
            System.out.println(e);
        }
    }

    private void write(){
        try {
            byte[] data = "ok".getBytes();
            String content = "HTTP/1.1 200 OK\n" +
                    "Server: Java HTTP NIO Server from hhcompany : 1.0\n" +
                    "Date: " + new Date()+
                    "Content-type: " +"text/plain" + "\n"+
                    "Content-length: " + data.length +"\n"+
                    "\n";
            content+= "ok";
            ByteBuffer byteBuffer = null;
            byteBuffer = charset.encode(content);
            socketChannel.write(byteBuffer);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

}