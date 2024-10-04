package onetoone.loginAPI;


public class logs{

    private String username;
    private String password;

    public logs(String username, String password){

        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}