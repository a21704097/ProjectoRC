import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client class
public class Client extends Regulador {
    public static void main(String[] args) {
        try {
            Scanner scn = new Scanner(System.in);
            boolean logged = false;

            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 6500
            Socket s = new Socket(ip, 6500);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            do {
                MulticastReceiver mr = new MulticastReceiver();
                mr.start();
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
                            boolean login = login(dis, dos);
                            if (!login) {
                                System.out.println("Username ou Password errados\n");
                                break;
                            }
                            logged = true;
                            break;

                        case "Registo":
                            registo(dis, dos);
                            break;
                    }
                }else {
                    switch (menu()) {
                        case "1":
                            //TODO
                        case "2":
                            //TODO
                        case "3":
                            //TODO
                    }
                }


            } while(true);

            // closing resources
            scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static boolean login(DataInputStream dis, DataOutputStream dos) throws IOException{
        Scanner scn = new Scanner(System.in);
        System.out.println("Digite Username e Password separados por \";\"");
        String userPass = scn.nextLine();
        dos.writeUTF("Login;" + userPass);

        String mensagemRecebida = dis.readUTF();
        System.out.println("Login com Sucesso\n");
        return mensagemRecebida.equals("Aceite");
    }

    private static void registo(DataInputStream dis, DataOutputStream dos) throws IOException{
        Scanner scn = new Scanner(System.in);

        System.out.println("Digite novo Username, Password e Plafond separados por \";\"");
        String userPassPlaf = scn.nextLine();

        dos.writeUTF("Registo;" + userPassPlaf);
        String mensagemRecebida = dis.readUTF();

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
                            "-----------------------------------------------------------------");

        return scn.nextLine();

    }
}
