package Servidor;

public class FecharLeiloes extends ClientHandler implements Runnable {

    @Override
    public void run() {
        while (true) {
            for (Leilao l : leiloes) {
                if (l.verificaData() && !l.isFechado()) {
                    l.fechar();
                    enviarTodos.multicast("Fechou;" + l.getId() + ";" + l.getVencedor() + ";" + l.getLicitacaoMax());
                }
            }
        }
    }
}
