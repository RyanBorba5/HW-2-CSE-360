package application;

public class Questions {
    private String question;
    private boolean resolved;
    private int recency;
    private String qstudentname;
    private String time;
    private String QID;

    // DEFAULT CONSTRUCTOR
    public Questions(String question, String studentName, boolean resolved, int recency, String time) {
        this.question = question;
        this.resolved = resolved;
        this.recency = recency;
        this.qstudentname = studentName;
        this.time = time;
        this.QID = "0"; // NOT SURE WHAT TO DO WITH THIS YET
    }

    // GETTING STUFF
    public String getQuestion() { return question; }
    public String getQuestionTime() { return time; }
    public int getQuestionRecency() { return recency; }
    public boolean getIsQuestionResolved() { return resolved; }
    public String getQuestionStudentName() { return qstudentname; }
    public String getQuestionID() { return QID; }

    // SETTING STUFF
    public void setQuestion(String question) { this.question = question; }
    public void setQuestionTime(String time) { this.time = time; }
    public void setQuestionRecency(int recency) { this.recency = recency; }
    public void setIsQuestionResolved(boolean resolved) { this.resolved = resolved; }
    public void setQuestionStudentName(String studentName) { this.qstudentname = studentName; }

    public static void main(String[] args) {
        System.out.println(Questions.validateQuestion(""));
    }
    
    public static boolean validateQuestion(String question) {
        if(question == null || question.length() == 0) {
            System.out.println("*** ERROR *** A question must be inputted.");
            return false;
        }

        if (!Character.isLetter(question.charAt(0))) {
            System.out.println("*** ERROR *** A question must start with A-Z, or a-z.\n");
            return false;
        }
        
        if (question.charAt(question.length()-1) != '?') {
            System.out.println("*** ERROR *** A question must end with a question mark!");
            return false;
        }

        if (question.length() > 90) {
            System.out.println("*** ERROR *** Question is longer than 90 characters!");
            return false;
        }
        return true;
    }
}