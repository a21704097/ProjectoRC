package Cliente;
import java.io.*;
import java.net.*;

public class MulticastReceiver extends Client implements Runnable {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];

    public void run() {
        try {
            socket = new MulticastSocket(7777);

        InetAddress group = InetAddress.getByName("224.2.7.6");
        socket.joinGroup(group);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf,0, buf.length);
            socket.receive(packet);
            String received = new String(buf);

            String[] dados = received.split(";");

            switch (dados[0]){
                case "Criar":
                    if(username.equals(dados[1])){
                        System.out.println("O seu leilão foi criado com sucesso com ID " + dados[2] + ".");
                    }else{
                        System.out.println("Há um novo leilão disponível,queira consultar os leilões disponíveis.");
                    }
                    break;

                case "Licitar":
                    if(!(username.equals(dados[2]))){
                        if(leiloes.contains(Integer.parseInt(dados[1]))){
                            System.out.println("Foi recebida uma nova licitação no leilão com ID " + dados[1] + ".");
                        }
                    }
                    break;

                case "Fechou":
                    if(!dados[2].equals("Ninguem")) {
                        if (username.equals(dados[2])) {
                            System.out.println("Parabéns! Foi o vencedor do leilão com o ID " + dados[1] + " no valor de " + dados[3] + " euros.");
                        } else if (leiloes.contains(Integer.parseInt(dados[1]))) {
                            System.out.println("O leilão com o ID " + dados[1] + " no qual realizou licitações já fechou, infelizmente você não foi o vencedor.");
                        } else if (leiloesProprios.contains(Integer.parseInt(dados[1]))) {
                            System.out.println("O bem presente no leilão com o ID "+ dados[1] +" foi vendido à pessoa "+ dados[2] + " com o valor " + dados[3] + " de euros.");
                        }
                    }else{
                        if(leiloesProprios.contains(Integer.parseInt(dados[1]))){
                            System.out.println("Lamentamos, mas o seu leilão com o ID "+ dados[1] +" fechou sem qualquer licitacão.");
                        }
                    }
                    break;

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