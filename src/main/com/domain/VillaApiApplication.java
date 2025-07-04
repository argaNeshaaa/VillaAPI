package main.com.domain;

import main.com.domain.Server.Main;

public class VillaApiApplication {
     public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        System.out.printf("Listening on port: %s...\n", port);
        new Main(port);
    }
}
