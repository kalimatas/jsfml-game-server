package server;

import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

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

    private boolean isConnected = true;
    private SocketChannel socketChannel;
    private Selector readSelector;
    private Clock tickClock = new Clock();

    public Client() throws IOException {
        LOGGER.info("starting client");

        socketChannel = SocketChannel.open(new InetSocketAddress(InetAddress.getLoopbackAddress(), GameServer.PORT));
        socketChannel.configureBlocking(false);

        readSelector = Selector.open();
        socketChannel.register(readSelector, SelectionKey.OP_READ);
    }

    public void close() throws IOException {
        socketChannel.close();
        isConnected = false;
    }

    public void update(Time dt) throws IOException {
        if (!isConnected) return;

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
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (packet != null) {
                PacketType packetType = (PacketType) packet.get();
                handleIncomingPacket(packetType, packet);
            }
        }

        if (tickClock.getElapsedTime().compareTo(Time.getSeconds(1.f / 20.f)) > 0) {
            Packet intervalUpdatePacket = new Packet();
            intervalUpdatePacket.append(PacketType.INTERVAL_UPDATE);
            intervalUpdatePacket.append(42);

            PacketReaderWriter.send(socketChannel, intervalUpdatePacket);

            tickClock.restart();
        }
    }

    public void handleEvent(Event event) throws IOException {
        if (isConnected && (event.type == Event.Type.KEY_PRESSED)) {
            KeyEvent keyEvent = event.asKeyEvent();

            if (keyEvent.key == Keyboard.Key.SPACE) {
                Packet spacePacket = new Packet();
                spacePacket.append(PacketType.SPACE_BUTTON);
                PacketReaderWriter.send(socketChannel, spacePacket);
            }

            if (keyEvent.key == Keyboard.Key.M) {
                Packet mPacket = new Packet();
                mPacket.append(PacketType.M_BUTTON);
                PacketReaderWriter.send(socketChannel, mPacket);
            }
        }
    }

    private void handleIncomingPacket(PacketType packetType, Packet packet) {
        LOGGER.info("handling packet: " + packetType);

        switch (packetType) {
            case BROADCAST_MESSAGE:
                LOGGER.info("got broadcast message: " + packet.get());
                break;
        }
    }
}
