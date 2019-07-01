package Servidor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

// Server class
public class Regulador {
     static ArrayList<Licitador> licitadores = new ArrayList<>();
     static ArrayList<Leilao> leiloes = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
        Thread fecharLeiloes = new Thread(new FecharLeiloes());


        try {
            lerFicheiroLicitadores();
            lerFicheiroLeiloes();
            lerFicheiroLicitacoes();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (true) {
            Socket s = null;
            //fecharLeiloes.start();
            try
            {
                // socket object to receive incoming client requests
                s = ss.accept();
                System.out.println("Novo Licitador conectado: " + s);

                System.out.println("Assigning new thread for this client");

                DataInputStream dis = new DataInputStream(s.getInputStream());
                // create a new thread object
                Thread t = new Thread(new ClientHandler(s, dis));

                // Invoking the start() method
                t.start();


            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }

    public static void lerFicheiroLicitadores() throws IOException{
        Scanner sc;

        File ficheiro1 = new File("Licitadores.txt");

        if(!ficheiro1.createNewFile()) {
            sc = new Scanner(ficheiro1);

            while (sc.hasNextLine()) {
                String[] dados = sc.nextLine().split(";");
                licitadores.add(new Licitador(dados[0], dados[1], dados[2], Integer.parseInt(dados[3])));
            }
        }

    }

    public static void lerFicheiroLeiloes() throws IOException {
        Scanner sc;
        File ficheiro2 = new File("Leiloes.txt");
        if(!ficheiro2.createNewFile()) {
            sc = new Scanner(ficheiro2);

            while (sc.hasNextLine()) {
                String[] dados = sc.nextLine().split(";");
                leiloes.add(new Leilao(Integer.parseInt(dados[0]), dados[1], dados[2], Integer.parseInt(dados[3]), dados[4]));
            }
        }
    }

    public static void lerFicheiroLicitacoes() throws IOException {
        Scanner sc;
        File ficheiro3 = new File("Licitacoes.txt");
        if(!ficheiro3.createNewFile()) {
            sc = new Scanner(ficheiro3);

            while (sc.hasNextLine()) {
                String[] dados = sc.nextLine().split(";");
                int id = Integer.parseInt(dados[0]);
                for(Leilao l : leiloes){
                    if(l.getId() == id){
                        l.adicionarLicitacao(Integer.parseInt(dados[1]), dados[2]);
                    }
                }
            }
        }
    }
}
