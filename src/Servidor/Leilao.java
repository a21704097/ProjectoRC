package Servidor;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Leilao {
    private int id;
    private LocalDateTime dataDeFecho;
    private String dataFicheiro;
    private String autor;
    private String descricao;
    private HashMap<Integer, String> licitacoes = new HashMap<>();
    private int licitacaoMax = 0;
    private String ultimoUser;
    private boolean fechado = false;

    Leilao(int id, String descricao, String dataDeFecho, String autor){
        this.id = id;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.dataDeFecho =LocalDateTime.parse(dataDeFecho, formatter);
        this.dataFicheiro = dataDeFecho;
        this.autor = autor;
        this.descricao = descricao;
        licitacoes.put(0, "Ninguem");
    }
    Leilao(int id,String descricao, String dataDeFecho,int licitacaoMax, String autor){
        this.id = id;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.dataDeFecho = LocalDateTime.parse(dataDeFecho, formatter);
        this.dataFicheiro = dataDeFecho;
        this.autor = autor;
        this.licitacaoMax = licitacaoMax;
        this.descricao = descricao;
    }

    public void adicionarLicitacao( int valor, String username){
        licitacoes.put(valor, username);
    }

    public boolean verificaData(){
        LocalDateTime dataAtual = LocalDateTime.now();
        return !dataDeFecho.isAfter(dataAtual);
    }

    public String toString(){
        return (id + " " + descricao + " " + dataFicheiro + " " + getLicitacaoMax() + " " + autor + "\n");
    }

    public String toStringParaFicheiro(){
        return (id + ";" + descricao + ";" + dataFicheiro + ";" + getLicitacaoMax() + ";" + autor );
    }

    public int getLicitacaoMax(){
        for( Integer i : licitacoes.keySet()){
                if(i > licitacaoMax){
                    licitacaoMax = i;
                }
        }
        return licitacaoMax;
    }

    public int getId(){
        return id;
    }

    public String getVencedor(){
        return licitacoes.get(getLicitacaoMax());
    }

    public void guardarLicitacoes() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Licitacoes.txt", false));
        int j = 0;
        for(int i : licitacoes.keySet()){
            if(j==0) {
                writer.write(getId() + ";" + i + ";" + licitacoes.get(i));
            }else{
                writer.write("\n" + getId() + ";" + i + ";" + licitacoes.get(i));
                j++;
            }

        }
        writer.close();
    }

    public int getPlafond(String username){
        int plafond = 0;
        for(int i : licitacoes.keySet()){
            if(licitacoes.get(i).equals(username)){
                plafond = i;
            }
        }
        return plafond;
    }

    public void guardarLicitadorAnterior(){
        ultimoUser = licitacoes.get(getLicitacaoMax());
    }

    public String getUltimoUser(){
        return ultimoUser;
    }
    public boolean isFechado(){
        return fechado;
    }

    public void fechar(){
        fechado = true;
    }
}


