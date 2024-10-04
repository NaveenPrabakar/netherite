package onetoone.loginAPI;


public class logs{

    private String email;
    private String password;

    public logs(String email, String password){

        this.email = email;
        this.password = password;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }
}