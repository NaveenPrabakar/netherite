package onetoone.signupAPI;

import java.util.List;
import onetoone.signupAPI.signRepository;//need to find the id of user
import onetoone.signupAPI.signEntity;
import onetoone.summarizeAPI.OpenAISummarizeAPIRepository;
import onetoone.summarizeAPI.AI;
import onetoone.summarizeAPI.summarizeAPIEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

//GET method to retrive the times the user use
//POST-create the user with times they use API
//PUT-Update the time they use
//Delete-delete the whole things as 24 hours

@RestController
@RequestMapping("/OpenAIAPIuse")
public class OpenAISummarizeAPI{

    //api key.....
    @Value("${openai.api.key}")
    private String openAIKey;

    @Autowired
    private OpenAISummarizeAPIRepository api;

    @Autowired
    private signRepository sign;

    //GET method to retrive the times the user use openai api
    //front end giving the username or usergmail
    //need to tell front end to use a condition for post and put entity because diff API
    @GetMapping("/getUsageAPICount/{userName}")
    public int getUsageAPICount(@PathVariable String userName) {

        //need to go to signentity to find username in order to find userid
        signEntity temp = sign.findByUsername(userName);
        Long userID = temp.getId();

        System.out.println(userID);

        //use the userid for the apientity table to find the count
        summarizeAPIEntity temp2 = api.findByAIUserId(userID);

        //if the user not exist in the table that means user never use it before
        if (temp2==null){
            return -1;
        }

        int count=temp2.getUsageAPICount();

        return count;
    }

    //Post method to create the users entity in AI table (never use before)
    //using the class created AI.java for requestBody
    //front end giving json for username,promt and content
    @PostMapping("/createAIUser")
    public String getFeedBack1(@RequestBody AI body){

        //need to go to signentity to find username in order to find userid
        signEntity temp= sign.findByUsername(body.getUserName());
        Long userID=temp.getId();

        //need to create the table entity
        summarizeAPIEntity sm = new summarizeAPIEntity(userID,LocalDateTime.now(),1);
        api.save(sm);

        //get the content and promt and pass in using open ai
        // Call OpenAI API for the first time
        String response = callOpenAI(body.getPrompt(), body.getContent());

        return response;

    }
    //Post method to create the users entity in AI table
    //using the class created AI.java for requestBody
    //front end giving json for username,promt and content
    @PutMapping("/updateAIUser")
    public String getFeedBack2(@RequestBody AI body){

        //need to go to signentity to find username in order to find userid
        signEntity temp= sign.findByUsername(body.getUserName());
        Long userID=temp.getId();

        //user the userid for the api table to find the count
        summarizeAPIEntity temp2=api.findByAIUserId(userID);

        int count=temp2.getUsageAPICount();

        //check if the count exceed 20 times
        if (count>20){
            return "You have exceed the daily limit of using Open AI api, please wait for 24 hours later\n";
        }

        //increment the count and set the usageAPICount
        temp2.setUsageAPICount(++count);

        api.save(temp2);


        //get the content and promt and pass in using open ai
        // Call OpenAI API for the first time
        String response = callOpenAI(body.getPrompt(), body.getContent());

        return response;
    }

    // DELETE method to reset usage count after 24 hours
    @DeleteMapping("/resetUsage/{userName}")
    public String resetUsage(@PathVariable String userName) {
        signEntity user = sign.findByUsername(userName);
        Long userID = user.getId();

        //find the userentity and information
        summarizeAPIEntity temp = api.findByAIUserId(userID);
        if (temp == null) {
            return "User has not used the API before.";
        }

        // Reset usage count to 0
        temp.setUsageAPICount(0);
        api.save(temp);
        return "API usage has been reset for the user.";
    }

    //helper method
    private String callOpenAI(String prompt, String content) {
        String openaiApiUrl = "https://api.openai.com/v1/chat/completions";  // Endpoint for chat-based models
        String apiKey = "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";  // Replace with your OpenAI API key

        // Initialize RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Create the request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo-0125");  // Using GPT-3.5 Turbo model
        requestBody.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                .put(new JSONObject().put("role", "user").put("content", prompt + "\n" + content))
        );
        requestBody.put("max_tokens", 50);  // Set max tokens for the response
        requestBody.put("temperature", 0.7);  // Adjust creativity level

        // Create an HTTP entity with headers and body
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        // Make the POST request
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    openaiApiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Extract the response body
            JSONObject responseBody = new JSONObject(response.getBody());
            String generatedText = responseBody.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return generatedText.trim();  // Return the trimmed response text

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling OpenAI API: " + e.getMessage();
        }
    }

}

