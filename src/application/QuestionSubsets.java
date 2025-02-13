package application;
import java.util.ArrayList;

/**
 * The QuestionSubsets class adds related questions into a subset/group.
 */
public class QuestionSubsets {
    private ArrayList<Questions> questionsubset = new ArrayList<>();

    public QuestionSubsets(ArrayList<Questions> questionsubset) {
        this.questionsubset = questionsubset;
    }

    // DEFAULT CONSTRUCTOR 
    public QuestionSubsets() {
        this.questionsubset = new ArrayList<>();
    }

    // Get the subset
    public ArrayList<Questions> getQuestionSubset() {
        return questionsubset;
    }

    // Add question
    public void addQuestion(Questions question) {
        if (Questions.validateQuestion(question.getQuestion())) {
            this.questionsubset.add(question);
        }
    }

    // Remove question
    public void removeQuestion(Questions question) {
         this.questionsubset.remove(question);
    }
}