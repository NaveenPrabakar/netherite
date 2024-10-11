package onetoone.summarizeAPI;

public class AI{

    private String email;
    private String prompt;
    private String content;

    public AI(String email, String prompt, String content){
        this.email = email;
        this.prompt = prompt;
        this.content = content;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String userEmail) {
        this.email = email;
    }
}
