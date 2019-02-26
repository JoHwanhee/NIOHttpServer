import com.sun.deploy.net.HttpRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NIOHttpServerTest
{
    private NIOHttpServer nioHttpServer = null;

    @Before
    public void setUp(){
        nioHttpServer = new NIOHttpServer(8080);
        nioHttpServer.start();
    }

    @Test
    public void localhost_접근시_http_response가_있어야함(){
        try {
            URL url = new URL("http://localhost:8080");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            System.out.println("Response code: " + conn.getResponseCode());

            Assert.assertTrue(conn.getResponseCode() == 200);

            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            System.out.println(sb.toString());
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    @After
    public void dispose(){
        nioHttpServer.stop();
    }
}
