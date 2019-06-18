public class Licitador {
    private String username;
    private String password;
    private int plafond;
    private String salt;

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

}
