package eu.ioservices.canopus.gateway;

import com.sun.net.httpserver.HttpServer;
import eu.ioservices.canopus.gateway.handling.StaticRequestProcessor;
import eu.ioservices.canopus.gateway.routing.Router;
import eu.ioservices.canopus.http.HttpMethod;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class GatewayTest {
    private static final int HTTP_SERVER_PORT = 1234;
    private static final int GATEWAY_PORT = 1235;

    private static HttpServer HTTP_SERVER;
    private static String HTTP_SERVER_RESPONSE_BODY = "OK";
    private static int EXPECTED_RESPONSE_CODE = 200;

    private static void startServer(int port, String context) throws IOException {
        HTTP_SERVER = HttpServer.create(new InetSocketAddress(port), 0);
        HTTP_SERVER.createContext(context, httpExchange -> {
            httpExchange.sendResponseHeaders(200, HTTP_SERVER_RESPONSE_BODY.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(HTTP_SERVER_RESPONSE_BODY.getBytes());
            os.close();
        });
        HTTP_SERVER.setExecutor(null);
        HTTP_SERVER.start();
    }

    private static void stopServer() {
        HTTP_SERVER.stop(0);
    }

    @Test
    public void handlingTest() throws IOException {
        String inPath = "/inPath";
        String targetPath = "/targetPath";
        String targetUrl = "http://127.0.0.1:" + HTTP_SERVER_PORT + targetPath;

        startServer(HTTP_SERVER_PORT, targetPath);

        final Router router = new Router();
        router.addRoute(HttpMethod.GET, inPath, targetUrl);
        final Gateway gateway = new Gateway(router, new StaticRequestProcessor());
        gateway.port(GATEWAY_PORT).listen();

        HttpURLConnection con = (HttpURLConnection) new URL(targetUrl).openConnection();
        assertEquals(EXPECTED_RESPONSE_CODE, con.getResponseCode());

        String response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder respBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                respBuilder.append(inputLine);
            }
            response = respBuilder.toString();
        }
        assertEquals(HTTP_SERVER_RESPONSE_BODY, response);

        stopServer();
    }
}
