import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Leilao {
    private int id;
    private LocalDateTime dataDeFecho;
    private String autor;
    private String descricao;
    private HashMap<Licitador, Integer> licitacoes = new HashMap<>();
    private int licitacaoMax = 0;

    Leilao(int id, LocalDateTime dataDeFecho, String autor, String descricao){
        this.id = id;
        this.dataDeFecho = dataDeFecho;
        this.autor = autor;
        this.descricao = descricao;
    }

    public void adicionarLicitacao(Licitador l, int valor){
        licitacoes.put(l, valor);
    }

    public boolean verificaData(){
        LocalDateTime dataAtual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.out.println(dataAtual.format(formatter));

        if(dataDeFecho.isAfter(dataAtual)){
            return false;
        }

        return true;
    }

    public String toString(){
        return (id + " " + descricao + " " + dataDeFecho + " " + getLicitacaoMax() + " " + autor + " ");
    }

    public int getLicitacaoMax(){
        for( Integer i : licitacoes.values()){
                if(i > licitacaoMax){
                    licitacaoMax = i;
                }
        }
        return licitacaoMax;
    }
}
