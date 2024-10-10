package onetoone.summarizeAPI;

public class AI{

    private String userName;
    private String prompt;
    private String content;

    public AI(String userName, String prompt, String content){
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
