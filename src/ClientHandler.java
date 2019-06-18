import java.io.*;
import java.net.Socket;

class ClientHandler extends Regulador implements Runnable {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String received;

        while (true) {
            try {
                String[] credenciais = dis.readUTF().split(";");
                received = credenciais[0];

                MulticastSender enviarTodos = new MulticastSender();

                enviarTodos.multicast("oi");

                if(received.equals("Exit")) {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                switch (received) {
                    case "Login" :
                        dos.writeUTF(login(credenciais));
                        break;

                    case "Registo":
                        dos.writeUTF(registo(credenciais));
                        break;

                    case "Criar":
                    //TODO

                    case "Lista":
                    //TODO

                    case "Licitar":
                    //TODO

                    default:
                        dos.writeUTF("Invalid input");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void escreveNovoLicitador(String linha) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("Licitadores.txt"));
        writer.write(linha);
        writer.close();
    }

    private String login(String[] credenciais) {
        String username = credenciais[1];
        String password = credenciais[2];
        Hash pass = new Hash();

            for (Licitador l : licitadores) {
                if (username.equals(l.getUsername()) && pass.hashPassword(password, l.getSalt()).get().equals(l.getPassword())) {
                    return "Aceite";
                }
            }

        return "Login Incorreto, verifique Username ou Password\n";
    }

    private String registo(String[] credenciais) throws IOException{
        if(credenciais.length != 4){
            return "Numero de Parametros errado\n";
        }
        String username = credenciais[1];
        String password = credenciais[2];
        int plafond = Integer.parseInt(credenciais[3]);
        Hash pass = new Hash();

        if(!licitadores.isEmpty()) {
            for (Licitador l : licitadores) {
                if (l.getUsername().equals(username)) {
                    return "Username Ja Existe\n";
                }
            }
        }

        String salt = pass.generateSalt(64).get();
        String passHashed = pass.hashPassword(password,salt).get();

        escreveNovoLicitador(username + ";" + passHashed + ";" + salt + ";" + plafond);
        licitadores.add(new Licitador(username, passHashed, salt, plafond));
        return "Aceite";
    }

    private String criaLeilao(String[] dados) throws IOException{
        return "";
        //TODO
    }



}