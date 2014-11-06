package server;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;

import java.io.IOException;
import java.util.logging.Logger;

public class GameServer {
    public static final int PORT = 4444;
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());
    private static final Time timePerFrame = Time.getSeconds(1.0f / 60.0f);

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
        Server server = null;
        if (mode == 2) {
            server = new Server();
        }

        // Start client
        Client client = new Client();

        // Create window and run application
        RenderWindow window = new RenderWindow(new VideoMode(200, 120), "Network", WindowStyle.CLOSE);
        window.setKeyRepeatEnabled(false);
        window.setVerticalSyncEnabled(true);

        Clock clock = new Clock();
        Time timeSinceLastUpdate = Time.ZERO;

        while (window.isOpen()) {
            Time dt = clock.restart();
            timeSinceLastUpdate = Time.add(timeSinceLastUpdate, dt);
            while (timeSinceLastUpdate.asMicroseconds() > timePerFrame.asMicroseconds()) {
                timeSinceLastUpdate = Time.sub(timeSinceLastUpdate, timePerFrame);

                for (Event event : window.pollEvents()) {
                    client.handleEvent(event);

                    if (event.type == Event.Type.CLOSED) {
                        client.close();
                        if (server != null) server.close();

                        window.close();
                    }
                }

                client.update(timePerFrame);
            }

            window.clear();
            window.display();
        }
    }
}
