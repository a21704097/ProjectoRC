import java.io.*;
import java.net.*;

public class MulticastReceiver extends ClientHandler implements Runnable {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    String received;

    MulticastReceiver(Socket s) {
        super(s);
    }

    public void run() {
        try {
            socket = new MulticastSocket(7777);

        InetAddress group = InetAddress.getByName("224.2.7.6");
        socket.joinGroup(group);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            received = new String(buf);

            String[] dados = received.split(";");

            switch (dados[0]){
                case "Criar":

                case "Licitar":

                case "Fechou":

                default:
                    break;

            }


            if ("end".equals(received)) {
                break;
            }
        }
        socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}