package coms309.PickUpLines;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/pickup-lines")
public class PickUpLineController {

    private List<PickUpLine>pickupLines=new ArrayList<>();

    public PickUpLineController() {
        pickupLines.add(new PickUpLine(1, "You are so hot girl that when I first laid eyes on you, I reached a runtime error."));
        pickupLines.add(new PickUpLine(2, "You are my superclass: you define what I can do."));
        pickupLines.add(new PickUpLine(3, "You are my semicolon; always present in everything I do."));
        pickupLines.add(new PickUpLine(4, "You are my methods. I am nothing without you."));
        pickupLines.add(new PickUpLine(5, "You are my loop condition. I keep coming back to you."));
        pickupLines.add(new PickUpLine(6, "You are a field in my class. You will always be protected."));
        pickupLines.add(new PickUpLine(7, "I think we should increase our bandwidth."));
        pickupLines.add(new PickUpLine(8, "I am a Boolean method whose love will always return true."));
        pickupLines.add(new PickUpLine(9, "Hey Baby, Let me hack your kernel."));
        pickupLines.add(new PickUpLine(10, "Are you a computer keyboard? Because you're my type."));
    }

    @GetMapping ("/{number}")
    public PickUpLine getPickUpLine (@PathVariable int number){
        if (number >0 && number <=pickupLines.size()){
            return pickupLines.get(number-1);
        }
        else{
            return new PickUpLine(number, "invalid number. Please choose a number between 1 and 10.\"");
        }
    }

    @PostMapping("/create")
    public PickUpLine createPickUpLine (@RequestBody String newLine){
        int nextNumber=pickupLines.size()+1;
        PickUpLine line=new PickUpLine(nextNumber,newLine);
        pickupLines.add(line);
        return line;
    }

    @GetMapping("/all")
    public List<PickUpLine> listPickupLines() {
        return pickupLines;
    }

    @PutMapping("/{number}")
    public PickUpLine updatePickUpLine(@PathVariable int number, @RequestBody String newLine){
        if (number >0 && number <=pickupLines.size()){
           PickUpLine updateLine=pickupLines.get(number-1);
           updateLine.setLine(newLine);
           return updateLine;
        }
        else{
            return new PickUpLine(number, "invalid number. Please choose a number between 1 and 10.\"");
        }
    }

    @DeleteMapping("/{number}")
    public String deletePickUpLine (@PathVariable int number){
        if (number >0 && number <=pickupLines.size()){
            pickupLines.remove(number-1);
            //reindex
            for (int i = number - 1; i < pickupLines.size(); i++) {
                pickupLines.get(i).setNumber(i + 1);
            }
            return "Pick Up Line deleted";
        }
        else{
            return "Invalid number. Please choose a number between 1 and 10.";
        }
    }

}

