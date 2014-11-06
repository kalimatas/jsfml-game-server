package server;

import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private boolean isConnected = true;
    private SocketChannel socketChannel;
    private Clock tickClock = new Clock();

    private static int intervalCounter = 1;

    public Client() throws IOException {
        LOGGER.info("starting client");

        socketChannel = SocketChannel.open(new InetSocketAddress(InetAddress.getLoopbackAddress(), GameServer.PORT));
        socketChannel.configureBlocking(false);
    }

    public void close() throws IOException {
        socketChannel.close();
        isConnected = false;
    }

    public void update(Time dt) throws IOException {
        if (!isConnected) return;

        if (tickClock.getElapsedTime().compareTo(Time.getSeconds(1.f / 20.f)) > 0) {
            LOGGER.info("client interval update: " + intervalCounter);

            Packet intervalUpdatePacket = new Packet();
            intervalUpdatePacket.append(PacketType.INTERVAL_UPDATE);
            intervalUpdatePacket.append(intervalCounter++);

            PacketReaderWriter.send(socketChannel, intervalUpdatePacket);

            tickClock.restart();
        }
    }

    public void handleEvent(Event event) throws IOException {
        if (isConnected && (event.type == Event.Type.KEY_PRESSED)) {
            LOGGER.info("handle key press");
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
}
