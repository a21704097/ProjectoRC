package Servidor;

import java.net.*;

public class MulticastSender {
    private MulticastSocket socket;
    private InetAddress group;
    private byte[] buf;

    public void multicast(String multicastMessage) {

        try {
            socket = new MulticastSocket(7777);
            group = InetAddress.getByName("224.2.7.6");
            buf = multicastMessage.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 7777);
            socket.send(packet);
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}