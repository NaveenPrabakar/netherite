package onetoone.loginAPI;


public class logs{

    private String username;
    private String password;

    //for login usage
    public logs(String username, String password){

        this.username = username;
        this.password = password;
    }

    //for email usage
    public logs(String username){
        this.username = username;
    }

    public logs(){

    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

}