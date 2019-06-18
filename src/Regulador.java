import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Server class
public class Regulador {
    static ArrayList<Licitador> licitadores = new ArrayList<>();
    static ArrayList<Leilao> leiloes = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // server is listening on port 6500
        ServerSocket ss = new ServerSocket(6500);

        try {
            Scanner sc;

            File ficheiro1 = new File("Licitadores.txt");

            if(!ficheiro1.createNewFile()) {
                sc = new Scanner(ficheiro1);

                while (sc.hasNextLine()) {
                    String[] dados = sc.nextLine().split(";");
                    licitadores.add(new Licitador(dados[0], dados[1], dados[2], Integer.parseInt(dados[3])));
                }
            }

            File ficheiro2 = new File("Leiloes.txt");
            if(!ficheiro2.createNewFile()) {
                sc = new Scanner(ficheiro2);

                while (sc.hasNextLine()) {
                    String[] dados = sc.nextLine().split(";");
                    leiloes.add(new Leilao(Integer.parseInt(dados[0]), LocalDateTime.parse(dados[1]), dados[2], (dados[3])));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        while (true) {
            Socket s = null;
            try
            {
                // socket object to receive incoming client requests
                s = ss.accept();

                System.out.println("Novo Licitador conectado: " + s);

                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                Thread t = new Thread(new ClientHandler(s, dis, dos));

                // Invoking the start() method
                t.start();

            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }
}
