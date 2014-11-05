package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private ServerSocketChannel serverSocketChannel;
    private List<SocketChannel> clients = new LinkedList<>();

    void server() {
        LOGGER.info("starting server");

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), GameServer.PORT));
        } catch (IOException e) {
            LOGGER.severe("cannot init server");
            e.printStackTrace();
        }

        LOGGER.info("running server");
        while (true) {
            acceptNewConnections();

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void acceptNewConnections() {
        SocketChannel clientChannel;
        try {
            while ((clientChannel = serverSocketChannel.accept()) != null) {
                LOGGER.info("got connection from " + clientChannel.socket().getInetAddress() + "\n");

                // add client to the list
                clients.add(clientChannel);
                clientChannel.configureBlocking(false);

                // new client messsage
                sendBroadcastingMessage("New client " + clientChannel.socket().getInetAddress() + "!\n");
            }
        } catch (IOException e) {
            LOGGER.warning("error while accept()");
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.warning("error in acceptNewConnections");
            e.printStackTrace();
        }
    }

    private void sendBroadcastingMessage(String message) {

    }
}
