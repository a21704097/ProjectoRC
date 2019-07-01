package Servidor;

import java.io.*;
import java.net.*;

class ClientHandler extends Regulador implements Runnable {
    final DataInputStream dis;
    final Socket s;
    DatagramPacket dp = null;
    int portUDP = 0;

    static Licitador l;
    MulticastSender enviarTodos = new MulticastSender();

    // Constructor
    ClientHandler(Socket s, DataInputStream dis) {
        this.s = s;
        this.dis = dis;
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
                InetAddress ip = s.getInetAddress();

                if(received.equals("Exit")) {
                    System.out.println("Cliente.Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

                switch (received) {
                    case "Login" :
                        msg = login(input);
                        if(portUDP == 0){
                            portUDP = Integer.parseInt(input[3]);
                        }
                        ds.send(dp = new DatagramPacket(msg.getBytes(),msg.getBytes().length, ip, portUDP ));
                        break;

                    case "Registo":
                        msg = registo(input);
                        if(portUDP == 0){
                           portUDP = Integer.parseInt(input[4]);
                        }
                        ds.send(dp = new DatagramPacket(msg.getBytes(),msg.getBytes().length, ip, portUDP));
                        break;

                    case "Criar":
                        msg = String.valueOf(criaLeilao(input));
                        ds.send(dp = new DatagramPacket(msg.getBytes(),msg.getBytes().length, ip, portUDP));
                        break;

                    case "Lista":
                        ListarLeiloes(ds, ip);
                        break;

                    case "Licitar":
                        msg = licitar(input);
                        ds.send(dp = new DatagramPacket(msg.getBytes(),msg.getBytes().length, ip, portUDP));
                        break;

                    case "Plafond":
                        msg = plafond();
                        ds.send(dp = new DatagramPacket(msg.getBytes(),msg.getBytes().length, ip, portUDP));
                        break;

                    default:
                        msg = "Invalid Input";
                        ds.send(dp = new DatagramPacket(msg.getBytes(),msg.getBytes().length, ip, portUDP));
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
        if (licitadores.size() == 0) {
            writer.write(linha);
        }else{
            writer.write("\n" + linha);
        }
        writer.close();
    }

    private String login(String[] credenciais) {
        String username = credenciais[1];
        String password = credenciais[2];
        Hash pass = new Hash();

            for (Licitador l : licitadores) {
                if (username.equals(l.getUsername()) && pass.hashPassword(password, l.getSalt()).get().equals(l.getPassword())) {
                    ClientHandler.l = l;
                    return "Login com Sucesso";
                }
            }

        return "Login Incorreto, verifique Username ou Password\n";
    }

    private String registo(String[] credenciais) throws IOException{
        if(credenciais.length != 5){
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

    private int criaLeilao(String[] dados) throws IOException{
        String data = dados[1];
        String descricao = dados[2];
        int id = 1;

        for(Leilao l : leiloes){
            if(l.getId() >= id){
                id = l.getId() + 1;
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter("Leiloes.txt", true));
        Leilao leilao = new Leilao(id,descricao, data, l.getUsername());
        if (id == 1) {
            writer.write(leilao.toStringParaFicheiro());
        }else {
            writer.write("\n" + leilao.toStringParaFicheiro());
        }
        writer.close();
        leiloes.add(leilao);
        l.adicionaLeilaoProprio(id);
        enviarTodos.multicast("Criar;"+ l.getUsername() + ";" + id);
        return id;
    }

    private void atualizarFicheiroLeiloes() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("Leiloes.txt", false));

        for(Leilao l : leiloes) {
            if (l.getId() == 1) {
                writer.write(l.toStringParaFicheiro());
            }else {
                writer.write( "\n" + l.toStringParaFicheiro());
            }

        }
        writer.close();
    }

    private void atualizarFicheiroLicitadores() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Licitadores.txt", false));
        for(Licitador l : licitadores){
            if (licitadores.get(0).getUsername().equals(l.getUsername())) {
                writer.write(l.toStringParaFicheiro());
            }else {
                writer.write( "\n" + l.toStringParaFicheiro());
            }
        }
        writer.close();
    }

    private void ListarLeiloes(DatagramSocket ds, InetAddress address) throws IOException {
        StringBuilder lista = new StringBuilder();

        for(Leilao l : leiloes){
            lista.append(l.toString());
        }

        String msg = ("Plafond disponível: " + l.getPlafond() + "\n" +
                "---------------------------------------------------------------\n" +
                lista);

        ds.send(new DatagramPacket(msg.getBytes(),msg.getBytes().length, address, 6000));
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