package application;

public class Answers {
    private String answer;
    private String time;
    private boolean hasbeenread;
    private boolean solution;
    private String astudentname;
    private String AID;

    // DEFAULT CONSTRUCTOR
    public Answers(String answer, String studentName, boolean hasBeenRead, boolean isSolution, String time, String AID) {
        this.answer = answer;
        this.time = time;
        this.hasbeenread = hasBeenRead;
        this.solution = isSolution;
        this.astudentname = studentName;
        this.AID = "0"; // IDK WHAT TO DO WITH THIS
    }

    // GET STUFF
    public String getAnswer() { return answer; }
    public String getAnswerTime() { return time; }
    public boolean getHasAnswerBeenRead() { return hasbeenread; }
    public boolean getIsAnswerSolved() { return solution; }
    public String getAnswerStudentName() { return astudentname; }
    public String getAnswerID() {return AID;}

    // SET STUFF
    public void setAnswer(String answer) { this.answer = answer; }
    public void setAnswerTime(String time) { this.time = time; }
    public void setHasAnswerBeenRead(boolean hasBeenRead) { this.hasbeenread = hasBeenRead; }
    public void setIsAnswerSolved(boolean isSolution) { this.solution = isSolution; }
    public void setAnswerStudentName(String studentName) { this.astudentname = studentName; }

    public static void main(String[] args) {
        System.out.println(Answers.validateAnswer(""));
    }
    
    public static boolean validateAnswer(String answer) {
        if(answer == null || answer.length() == 0) {
            System.out.println("*** ERROR *** An answer must be inputted.");
            return false;
        }

        if (!Character.isLetter(answer.charAt(0))) {
            System.out.println("*** ERROR *** An answer must start with A-Z, or a-z.\n");
            return false;
        }

        if (answer.length() > 150) {
            System.out.println("*** ERROR *** Answer is longer than 150 characters!");
            return false;
        }
        return true;
    }
}