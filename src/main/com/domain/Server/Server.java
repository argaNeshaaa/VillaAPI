package main.com.domain.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private HttpServer server;

    private class RequestHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) {
            Server.processHttpExchange(httpExchange);
        }
    }

    public Server(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 128);
        server.createContext("/", new RequestHandler());
        server.start();
    }

    public static void processHttpExchange(HttpExchange httpExchange) {
        Request req = new Request(httpExchange);
        Response res = new Response(httpExchange);

        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();
        System.out.printf("path: %s\n", path);

        // Handle request dan autentikasi dalam block try-catch dibawah. Tapi apa semua harus diletakkan disini?
        try {
            Map<String, Object> reqJsonMap = req.getJSON();
            System.out.println("first_name => " + reqJsonMap.get("first_name"));
            System.out.println("email => " + reqJsonMap.get("email"));
            System.out.println("Done!");
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        // Handle response disini jika tidak ada yg menghandle
        if (!res.isSent()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resJsonMap = new HashMap<>();
            resJsonMap.put("message", "Request Success");

            String resJson = "";
            try {
                resJson = objectMapper.writeValueAsString(resJsonMap);
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
            res.setBody(resJson);
            res.send(HttpURLConnection.HTTP_OK);
            return;
        }

        httpExchange.close();
    }
}
