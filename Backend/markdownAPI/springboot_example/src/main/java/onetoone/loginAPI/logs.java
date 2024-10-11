package onetoone.loginAPI;


public class logs{

    private String email;
    private String password;

    //for login usage
    public logs(String email, String password){

        this.email =email;
        this.password = password;
    }

    //for email usage
    public logs(String email){
        this.email = email;
    }

    public logs(){

    }

    public String getemail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

}