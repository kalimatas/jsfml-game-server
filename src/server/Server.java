package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Server extends Thread {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private volatile boolean isRunning = true;
    private ServerSocketChannel serverSocketChannel;
    private Selector readSelector;
    private List<SocketChannel> clients = new LinkedList<>();

    public Server() throws IOException {
        LOGGER.info("starting server");

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), GameServer.PORT));
        readSelector = Selector.open();

        start();
    }

    public void run() {
        while (isRunning) {
            try {
                handleIncomingConnections();
                handleIncomingPackets();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                // Sleep to prevent server from consuming 100% CPU
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        isRunning = false;
        serverSocketChannel.close();
    }

    private void handleIncomingConnections() throws IOException {
        SocketChannel clientChannel;
        while ((clientChannel = serverSocketChannel.accept()) != null) {
            InetAddress clientAddress = clientChannel.socket().getInetAddress();
            LOGGER.info("Got connection from " + clientAddress + "\n");

            clientChannel.configureBlocking(false);
            clientChannel.register(readSelector, SelectionKey.OP_READ);

            // add client to the list
            clients.add(clientChannel);

            // new client messsage
            sendBroadcastingMessage("New client " + clientAddress + "!\n");
        }
    }

    private void handleIncomingPackets() throws IOException {
        readSelector.selectNow();

        Set<SelectionKey> readKeys = readSelector.selectedKeys();
        Iterator<SelectionKey> it = readKeys.iterator();

        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();

            SocketChannel channel = (SocketChannel) key.channel();

            Packet packet = null;
            try {
                packet = PacketReaderWriter.receive(channel);
            } catch (NothingToReadException e) {
                e.printStackTrace();
            }

            if (packet != null) {
                // Interpret packet and react to it
                handleIncomingPacket(packet, channel);
            }
        }
    }

    private void handleIncomingPacket(Packet packet, SocketChannel channel) {
        PacketType packetType = (PacketType) packet.get();

        switch (packetType) {
            case INTERVAL_UPDATE:
                LOGGER.info("handling interval packet: " + (int) packet.get());
                break;
            case SPACE_BUTTON:
                LOGGER.info("handling SPACE button");
                break;
            case M_BUTTON:
                LOGGER.info("handling M button");
                break;
        }
    }

    private void sendBroadcastingMessage(String message) throws IOException {
        for (SocketChannel channel : clients) {
            Packet packet = new Packet();
            packet.append(PacketType.BROADCAST_MESSAGE);
            packet.append(message);

            PacketReaderWriter.send(channel, packet);
        }
    }
}
