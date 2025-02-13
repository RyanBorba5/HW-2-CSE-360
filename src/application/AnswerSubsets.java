package application;
import java.util.ArrayList;

/**
 * The AnswerSubsets class defines answer groups.
 */
public class AnswerSubsets {
    private ArrayList<Answers> answersubset = new ArrayList<>();

    public AnswerSubsets(ArrayList<Answers> answersubset) {
        this.answersubset = answersubset;
    }
    
    // DEFAULT CONSTRUCTOR
    public AnswerSubsets() {
        this.answersubset = new ArrayList<>();
    }
    
    // Get the subset
    public ArrayList<Answers> getAnswerSubset() { 
        return answersubset; 
    }
   
    // Add an answer
    public void addAnswer(Answers answer) {
        if (Answers.validateAnswer(answer.getAnswer())) {
            this.answersubset.add(answer);
        }
    }
    
    // Remove answer
    public void removeAnswer(Answers answer) {
        this.answersubset.remove(answer);
    }
}