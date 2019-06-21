package Servidor;

import java.util.ArrayList;

public class Licitador {
    private String username;
    private String password;
    private int plafond;
    private String salt;
    private ArrayList<Integer> leiloes = new ArrayList<>();
    private ArrayList<Integer> leiloesProprios = new ArrayList<>();

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

    public void retiraPlafond(int valor){
        if((getPlafond() - valor) >= 0){
            plafond -= valor;
        }
    }

    public void devolvePlafond(int valor){
        plafond+= valor;
    }


    public void adicionaLeilao(int idLeilao){
        this.leiloes.add(idLeilao);
    }

    public void adicionaLeilaoProprio(int idLeilao){
        this.leiloesProprios.add(idLeilao);
    }

    public String toStringParaFicheiro(){
        return (username + ";" + password + ";" + salt + ";" + plafond);
    }

    public ArrayList<Integer> getLeiloes() {
        return this.leiloes;
    }
    public ArrayList<Integer> getLeiloesProprios() {
        return this.leiloesProprios;
    }

}
