package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private SocketChannel socketChannel;
    private Selector readSelector;

    void client() {
        LOGGER.info("starting client");

        try {
            socketChannel = SocketChannel.open(new InetSocketAddress(InetAddress.getLoopbackAddress(), GameServer.PORT));
            socketChannel.configureBlocking(false);

            readSelector = Selector.open();
            socketChannel.register(readSelector, SelectionKey.OP_READ);

            for (;;) {
                readSelector.selectNow();

                Set<SelectionKey> readKeys = readSelector.selectedKeys();
                Iterator<SelectionKey> it = readKeys.iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    SocketChannel channel = (SocketChannel) key.channel();

                    //Packet packet = readPacket(channel);
                    Packet packet = null;
                    if (packet != null && packet.size() > 0) {
                        // On the client you should know the structrue of the packet
                        LOGGER.info((String) packet.get());
                    }
                }

                Thread.sleep(200);
            }

        } catch (IOException e) {
            LOGGER.severe("cannot connect to server");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
