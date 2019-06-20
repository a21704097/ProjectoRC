import java.io.*;
import java.net.*;

class ClientHandler extends Regulador implements Runnable {
    final DataInputStream dis;
    final Socket s;
    DatagramPacket dp = null;

    static Licitador l;
    MulticastSender enviarTodos = new MulticastSender();

    // Constructor
    ClientHandler(Socket s, DataInputStream dis) {
        this.s = s;
        this.dis = dis;
    }

    public ClientHandler(Socket s) {
        this.s = s;
        this.dis = null;
    }

    public ClientHandler() {
        this.s = null;
        this.dis = null;
    }

    @Override
    public void run() {
        String received;

        while (true) {
            try {
                String[] input = dis.readUTF().split(";");
                received = input[0];
                String msg;

                DatagramSocket ds = new DatagramSocket();
                InetAddress ip = InetAddress.getLocalHost();

                if(received.equals("Exit")) {
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                switch (received) {
                    case "Login" :
                        msg = login(input);
                        ds.send(new DatagramPacket(msg.getBytes(),msg.length(), ip, 6000));
                        break;

                    case "Registo":
                        msg = registo(input);
                        ds.send(new DatagramPacket(msg.getBytes(),msg.length(), ip, 6000));
                        break;

                    case "Criar":
                        criaLeilao(input);
                        break;

                    case "Lista":
                        ListarLeiloes(ds);
                        break;

                    case "Licitar":
                        msg = licitar(input);
                        ds.send(dp = new DatagramPacket(msg.getBytes(),msg.length(), ip, 6000));
                        break;

                    case "Plafond":
                        msg = plafond();
                        ds.send(dp = new DatagramPacket(msg.getBytes(),msg.length(), ip, 6000));
                        break;

                    default:
                        msg = "Invalid Input";
                        ds.send(dp = new DatagramPacket(msg.getBytes(),msg.length(), ip, 6000));
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // closing resources
            dis.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void escreveNovoLicitador(String linha) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("Licitadores.txt", true));
        writer.write(linha+ "\n");
        writer.close();
    }

    private String login(String[] credenciais) {
        String username = credenciais[1];
        String password = credenciais[2];
        Hash pass = new Hash();

            for (Licitador l : licitadores) {
                if (username.equals(l.getUsername()) && pass.hashPassword(password, l.getSalt()).get().equals(l.getPassword())) {
                    ClientHandler.l = l;
                    System.out.println("sucesso");
                    return "Login com Sucesso";
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
        l = new Licitador(username, passHashed, salt, plafond);
        licitadores.add(l);
        return "Aceite";
    }

    private void criaLeilao(String[] dados) throws IOException{
        String data = dados[1];
        String descricao = dados[2];
        int id = 1;

        for(Leilao l : leiloes){
            if(l.getId() > id){
                id = l.getId() + 1;
            }
        }
        l.adicionaLeilaoProprio(id);

        BufferedWriter writer = new BufferedWriter(new FileWriter("Leiloes.txt", true));
        Leilao leilao = new Leilao(id,descricao, data, l.getUsername());
        writer.write( leilao.toStringParaFicheiro() + "\n");
        writer.close();
        leiloes.add(leilao);
        enviarTodos.multicast("Criar;"+ l.getUsername());
    }

    private void atualizarFicheiroLeiloes() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("Leiloes.txt", false));

        for(Leilao l : leiloes){
            writer.write(l.toStringParaFicheiro());
        }
        writer.close();
    }

    private void atualizarFicheiroLicitadores() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Licitadores.txt", false));
        for(Licitador l : licitadores){
            writer.write(l.toStringParaFicheiro());
        }
        writer.close();
    }

    private void ListarLeiloes(DatagramSocket ds) throws IOException {
        StringBuilder lista = new StringBuilder();

        for(Leilao l : leiloes){
            lista.append(l.toString());
        }

        String msg = ("Plafond disponível: " + l.getPlafond() + "\n" +
                "---------------------------------------------------------------\n" +
                lista);

        ds.send(new DatagramPacket(msg.getBytes(),msg.length()));
    }

    private String licitar(String [] dados) throws IOException {
        int idLeilao = Integer.parseInt(dados[1]);
        int valor = Integer.parseInt(dados[2]);

        for(Leilao leilao : leiloes){
            if(leilao.getId() == idLeilao){
                if(leilao.getLicitacaoMax() < valor){
                    if(l.getPlafond() > valor){
                        leilao.guardarLicitadorAnterior();
                        leilao.adicionarLicitacao(valor, l.getUsername());
                        l.adicionaLeilao(idLeilao);
                        l.retiraPlafond(valor);
                        for(Licitador licitador: licitadores){
                            if(licitador.getUsername().equals(leilao.getUltimoUser())){
                                licitador.devolvePlafond(leilao.getPlafond(leilao.getUltimoUser()));
                            }
                        }
                        enviarTodos.multicast("Licitar;" + leilao.getId() + ";" + l.getUsername());
                        atualizarFicheiroLeiloes();
                        atualizarFicheiroLicitadores();
                        leilao.guardarLicitacoes();
                        return "A sua licitação foi aceite.";
                    }else{
                        return "A sua solicitação não foi aceite,o valor da sua proposta é superior ao seu plafond.";
                    }
                }else{
                   return"A sua licitação não foi aceite, o valor proposto não é superior ao máximo atual.";
                }
            }else{
                return "O leilão com ID " + idLeilao + " não existe ou já não está disponível.";
            }
        }
        return "";
    }

    private String plafond(){
        return "O seu plafond​​ atual é de " + l.getPlafond() +" euros.";
    }

}