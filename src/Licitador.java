import java.util.ArrayList;

public class Licitador {
    private String username;
    private String password;
    private int plafond;
    private String salt;
    private ArrayList<Integer> leiloes = new ArrayList<>(); //TODO
    private ArrayList<Integer> leiloesProprios = new ArrayList<>(); //TODO

    Licitador(String username, String password, String salt, int plafond){
        this.username = username;
        this.password = password;
        this.plafond = plafond;
        this.salt = salt;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public int getPlafond(){
        return plafond;
    }

    public String getSalt(){
        return salt;
    }

    public boolean retiraPlafond(int valor){
        if((getPlafond() - valor) >= 0){
            plafond -= valor;
            return true;
        }else{
            return false;
        }
    }

    public void devolvePlafond(int valor){
        plafond+= valor;
    }


    public void adicionaLeilao(int idLeilao){
        leiloes.add(idLeilao);
    }

    public String toStringParaFicheiro(){
        return (username + ";" + password + ";" + salt + ";" + plafond);
    }

    public void adicionaLeilaoProprio(int idLeilao){
        leiloesProprios.add(idLeilao);
    }
}
