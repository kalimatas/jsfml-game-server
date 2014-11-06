package server;

import java.io.IOException;
import java.util.logging.Logger;

public class GameServer {
    public static final int PORT = 4444;
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    public static void main(String... args) throws IOException {
        int mode = 1;
        if (args.length == 1) {
            try {
                mode = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                mode = 1;
            }
        }

        // Start server
        if (mode == 2) {
            new Server();
        }

        // Start client
        Client client = new Client();

        // Create window and run application
    }
}
