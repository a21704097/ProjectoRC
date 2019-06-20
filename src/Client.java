import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

// Client class
public class Client extends Regulador {
    public static void main(String[] args) {
        try {
            Scanner scn = new Scanner(System.in);
            boolean logged = false;

            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");
            DatagramSocket ds = new DatagramSocket(6000);
            byte[] receive = new byte[256];
            DatagramPacket dpReceive = null;

            Socket s = new Socket(ip, 6500);

            // obtaining input and out streams
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            Thread mr = new Thread(new MulticastReceiver(s));
            mr.start();

            do {
                dpReceive = new DatagramPacket(receive,0, receive.length);

                if(!logged) {
                    System.out.println("-----------------------------------------------------------------\n" +
                            "             Digite Login para entrar\n" +
                            "             Digite Registo para criar ficha de Licitador\n" +
                            "             Digite Exit para terminar conexão\n" +
                            "-----------------------------------------------------------------");

                    String opcao = scn.nextLine();

                    if (opcao.equals("Exit")) {
                        System.out.println("A fechar a ligação: " + s);
                        s.close();
                        System.out.println("Ligação Fechada");
                        break;
                    }


                    switch (opcao) {
                        case "Login":
                            boolean login = login(ds, dpReceive ,dos);
                            if (!login) {
                                System.out.println("Username ou Password errados\n");
                                break;
                            }
                            logged = true;
                            break;

                        case "Registo":
                            registo(ds, dpReceive, dos);
                            break;

                        default:
                            System.out.println("Invalid Input");
                            break;
                    }
                }else {
                    switch (menu()) {
                        case "1":
                            criarLeilao(dos);
                            break;
                        case "2":
                            dos.writeUTF("Lista");
                            ds.receive(dpReceive);
                            String mensagemRecebida = new String(dpReceive.getData(),dpReceive.getOffset(), dpReceive.getLength());
                            System.out.println(mensagemRecebida);
                            break;
                        case "3":
                            System.out.println(licitar(ds, dpReceive, dos));
                            break;
                        case "4":
                            System.out.println(pedirPlafond(ds, dpReceive, dos));
                            break;

                        default:
                            System.out.println("Invalid Input");
                            break;

                    }
                }
                receive = new byte[256];

            } while(true);

            // closing resources
            scn.close();
            ds.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static boolean login(DatagramSocket ds, DatagramPacket dpReceive, DataOutputStream dos) throws IOException{
        Scanner scn = new Scanner(System.in);
        System.out.println("Digite Username e Password separados por \";\"");
        String userPass = scn.nextLine();
        dos.writeUTF("Login;" + userPass);
        ds.receive(dpReceive);
        String mensagemRecebida = new String(dpReceive.getData(),dpReceive.getOffset(), dpReceive.getLength());
        System.out.println(mensagemRecebida);
        return mensagemRecebida.equals("Login com Sucesso");
    }

    private static void registo(DatagramSocket ds, DatagramPacket dpReceive, DataOutputStream dos) throws IOException{
        Scanner scn = new Scanner(System.in);

        System.out.println("Digite novo Username, Password e Plafond separados por \";\"");
        String userPassPlaf = scn.nextLine();

        dos.writeUTF("Registo;" + userPassPlaf);
        ds.receive(dpReceive);
        String mensagemRecebida = new String(dpReceive.getData(),dpReceive.getOffset(), dpReceive.getLength());
        if(mensagemRecebida.equals("Aceite")){
            System.out.println("Registo criado com sucesso\n");
        }else{
            System.out.println(mensagemRecebida);
        }
    }

    private static String menu() {
        Scanner scn = new Scanner(System.in);

        System.out.println( "-----------------------------------------------------------------\n" +
                            "               1 - Criar Leilão\n" +
                            "               2 - Ver listagem de Leilões\n" +
                            "               3 - Licitar num Leilão\n" +
                            "               4 - Ver Plafond\n" +
                            "-----------------------------------------------------------------");

        return scn.nextLine();

    }

    private static void criarLeilao(DataOutputStream dos) throws IOException {
        Scanner scn = new Scanner(System.in);
        System.out.println("Digite a Data de Fecho do Leilao no formato \"dd-MM-yyyy HH:mm:ss\" e a Descricao do Leilao separados por \";\"");
        String dataDescricao = scn.nextLine();
        dos.writeUTF("Criar;" + dataDescricao);
    }

    private static String licitar(DatagramSocket ds, DatagramPacket dpReceive, DataOutputStream dos) throws IOException {
        Scanner scn = new Scanner(System.in);

        System.out.println("Digite o ID do Leilão em que quer licitar e o valor a licitar, separados por \";\"");
        String idValor = scn.nextLine();

        dos.writeUTF("Licitar;" + idValor);
        ds.receive(dpReceive);
        return new String(dpReceive.getData(),dpReceive.getOffset(), dpReceive.getLength());
    }

    private static int pedirPlafond(DatagramSocket ds, DatagramPacket dpReceive, DataOutputStream dos) throws IOException {
        dos.writeUTF("Plafond");
        ds.receive(dpReceive);
        return Integer.parseInt(new String(dpReceive.getData(),dpReceive.getOffset(), dpReceive.getLength()));
    }
}

