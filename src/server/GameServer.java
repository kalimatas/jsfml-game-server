package server;

import java.util.logging.Logger;

public class GameServer {
    public static final int PORT = 4444;
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    public static void main(String... args) {
        int mode = 1;
        if (args.length == 1) {
            try {
                mode = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                mode = 1;
            }
        }

        switch (mode) {
            case 1:
                //gs.client();
                break;
            case 2:
                //gs.server();
                break;
            default:
                throw new RuntimeException("unknown mode");
        }
    }
}
