package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    
    
    public static String userNameRecognizerErrorMessage = "";	// The error message text
	public static String userNameRecognizerInput = "";			// The input being processed
	public static int userNameRecognizerIndexofError = -1;		// The index of error location
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is
														// running
    
    private static int userNameSize = 0;			// A numeric value may not exceed 16 characters
	// Private method to display debugging data
	private static void displayDebuggingInfo() {
		// Display the current state of the FSM as part of an execution trace
		if (currentCharNdx >= inputLine.length())
			// display the line with the current state numbers aligned
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state +
					((finalState) ? "       F   " : "           ") + "None");
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state +
				((finalState) ? "       F   " : "           ") + "  " + currentChar + " " +
				((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") +
				nextState + "     " + userNameSize);
	}
    
    private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}
    
    
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            boolean noerrors = false;
            
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            			if(databaseHelper.validateInvitationCode(code)) {
            			
            			// Create a new user and register them in the database
		            	User user=new User(userName, password, "user");
		                databaseHelper.register(user);
		                
		             // Navigate to the Welcome Login Page
		                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            		}else {
            			errorLabel.setText("Please enter a valid invitation code");
            		}
            		// Grant errors based on UserName
            		// Check to ensure that there is input to process
            		if(userName.length() <= 0) {
            			userNameRecognizerIndexofError = 0;	// Error at first character;
            			errorLabel.setText(userNameRecognizerErrorMessage);
            		}
            		
            		// The local variables used to perform the Finite State Machine simulation
            		state = 0;							// This is the FSM state number
            		inputLine = userName;					// Save the reference to the input line as a global
            		currentCharNdx = 0;					// The index of the current character
            		currentChar = userName.charAt(0);		// The current character from above indexed position
            		// The Finite State Machines continues until the end of the input is reached or at some
            		// state the current character does not match any valid transition to a next state
            		userNameRecognizerInput = userName;	// Save a copy of the input
            		running = true;						// Start the loop
            		nextState = -1;						// There is no next state
            		System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");
            		
            		// This is the place where semantic actions for a transition to the initial state occur
            		
            		userNameSize = 0;	
            		// Initialize the UserName size
            		// The Finite State Machines continues until the end of the input is reached or at some
            		// state the current character does not match any valid transition to a next state
            		while (running) {
            			// The switch statement takes the execution to the code for the current state, where
            			// that code sees whether or not the current character is valid to transition to a
            			// next state
            			switch (state) {
            			case 0:
            				// State 0 has 1 valid transition that is addressed by an if statement.
            				
            				// The current character is checked against A-Z, a-z, 0-9. If any are matched
            				// the FSM goes to state 1
            				
            				// A-Z, a-z, 0-9 -> State 1
            				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
            						(currentChar >= 'a' && currentChar <= 'z' )) {	// Check for a-z (We want the first to be alphabetical)
            					nextState = 1;
            					
            					// Count the character
            					userNameSize++;
            					
            					// This only occurs once, so there is no need to check for the size getting
            					// too large.
            				}
            				// If it is none of those characters, the FSM halts
            				else
            					running = false;
            				
            				// The execution of this state is finished
            				break;
            			
            			case 1:
            			    // State 1 has two valid transitions,
            			    //	1: a A-Z, a-z, 0-9 that transitions back to state 1
            			    //  2: a period that transitions to state 2
            			    // A-Z, a-z, 0-9 -> State 1
            			    if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
            			            (currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
            			            (currentChar == '_' ) ||	// Check for "_".
            			            (currentChar == '-' ) ||	// Check for "-".
            			            (currentChar == '.' ) ||	// Check for ".".
            			            (currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
            			        nextState = 1;
            			       
            			        // Count the character
            			        userNameSize++;
            			    }
            			    // . -> State 2
            			    else if (currentChar == '.') {							// Check for .
            			        nextState = 2;
            			       
            			        // Count the .
            			        userNameSize++;
            			    }
            			    // If it is none of those characters, the FSM halts
            			    else {
            			        running = false;
            			    }
            			    // The execution of this state is finished
            			    // If the size is larger than 16, the loop must stop
            			    if (userNameSize > 16)
            			        running = false;
            			    // Check if we are at the end of the input
            			    if (currentCharNdx == inputLine.length() - 1) {
            			        char lastChar = inputLine.charAt(inputLine.length() - 1);
            			        if (lastChar == '.' || lastChar == '-' || lastChar == '_') {
            			            userNameRecognizerErrorMessage +=
            			                "A UserName cannot end with a period, minus sign, or underscore.\n";
            			            errorLabel.setText(userNameRecognizerErrorMessage);
            			            noerrors = false;
            			        }
            			    }
            			   
            			    break;		
            				
            			case 2:
            				// State 2 deals with a character after a period in the name.
            				
            				// A-Z, a-z, 0-9 -> State 1
            				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
            						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
            						(currentChar == '_' ) ||	// Check for _
            						(currentChar == '-' ) ||	// Check for -
            						(currentChar == '.' ) ||	// Check for .
            						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
            					nextState = 1;
            					
            					// Count the odd digit
            					userNameSize++;
            					
            				}
            				// If it is none of those characters, the FSM halts
            				else
            					running = false;
            				// The execution of this state is finished
            				// If the size is larger than 16, the loop must stop
            				if (userNameSize > 16)
            					running = false;
            				break;			
            			}
            			
            			if (running) {
            				displayDebuggingInfo();
            				// When the processing of a state has finished, the FSM proceeds to the next
            				// character in the input and if there is one, it fetches that character and
            				// updates the currentChar.  If there is no next character the currentChar is
            				// set to a blank.
            				moveToNextCharacter();
            				// Move to the next state
            				state = nextState;
            				
            				// Is the new state a final state?  If so, signal this fact.
            				if (state == 1) finalState = true;
            				// Ensure that one of the cases sets this to a valid value
            				nextState = -1;
            			}
            			// Should the FSM get here, the loop starts again
            	
            		}
            		displayDebuggingInfo();
            		
            		System.out.println("The loop has ended.");
            		
            		// When the FSM halts, we must determine if the situation is an error or not.  That depends
            		// of the current state of the FSM and whether or not the whole string has been consumed.
            		// This switch directs the execution to separate code for each of the FSM states and that
            		// makes it possible for this code to display a very specific error message to improve the
            		// user experience.
            		userNameRecognizerIndexofError = currentCharNdx;	// Set index of a possible error;
            		userNameRecognizerErrorMessage = "\n*** ERROR *** ";
            		
            		// The following code is a slight variation to support just console output.
            		switch (state) {
            		case 0:
            			// State 0 is not a final state, so we can return a very specific error message
            			userNameRecognizerErrorMessage += "A UserName must start with A-Z, or a-z.\n";
            			errorLabel.setText(userNameRecognizerErrorMessage);
            			noerrors = false;
            		case 1:
            			// State 1 is a final state.  Check to see if the UserName length is valid.  If so we
            			// we must ensure the whole string has been consumed.
            			if (userNameSize < 4) {
            				// UserName is too small
            				userNameRecognizerErrorMessage += "A UserName must have at least 4 characters.\n";
            				errorLabel.setText(userNameRecognizerErrorMessage);
            				noerrors = false;
            			}
            			else if (userNameSize > 16) {
            				// UserName is too long
            				userNameRecognizerErrorMessage +=
            					"A UserName must have no more than 16 character.\n";
            				errorLabel.setText(userNameRecognizerErrorMessage);
            				noerrors = false;
            			}
            			else if (currentCharNdx < userName.length()) {
            				// There are characters remaining in the input, so the input is not valid
            				userNameRecognizerErrorMessage +=
            					"A UserName character may only contain the characters A-Z, a-z, 0-9, underscores, minus signs, and periods.\n";
            				errorLabel.setText(userNameRecognizerErrorMessage);
            				noerrors = false;
            			}
            			else {
            					// UserName is valid
            					userNameRecognizerIndexofError = -1;
            					userNameRecognizerErrorMessage = "";
            					errorLabel.setText(userNameRecognizerErrorMessage);
            					noerrors = true;
            			}
            		case 2:
            			// State 2 is not a final state, so we can return a very specific error message
        			        char lastChar = inputLine.charAt(inputLine.length() - 1);
        			        if (lastChar == '.' || lastChar == '-' || lastChar == '_') {
        			            userNameRecognizerErrorMessage +=
        			                "A UserName cannot end with a period, minus sign, or underscore.\n";
        			            errorLabel.setText(userNameRecognizerErrorMessage);
        			            noerrors = false;
        			        }
            			
            		default:
            			// This is for the case where we have a state that is outside of the valid range.
            			// This should not happen
            			userName = "";
            		}
            		
            		// Validate the invitation code
            	}
            	else {
            		errorLabel.setText("This userName is taken!!.. Please use another to setup an account");
            	}
            	
            	//THIS COMMENT IS A GAP THINGY
           		// The following are the local variable used to perform the Directed Graph simulation
            	if (noerrors == true) {
            		String passwordErrorMessage = "";
            		int passwordIndexofError = 0;			// Initialize the IndexofError
            		inputLine = password;					// Save the reference to the input line as a global
            		currentCharNdx = 0;					// The index of the current character
           		
            		if(password.length() <= 0) {
           			errorLabel.setText("*** Password Error *** Please make a password!");
            		}
           		
            		// The input is not empty, so we can access the first character
            		currentChar = password.charAt(0);		// The current character from the above indexed position
            		// The Directed Graph simulation continues until the end of the input is reached or at some 
            		// state the current character does not match any valid transition to a next state
            		String passwordInput = password;				// Save a copy of the input
            		boolean foundUpperCase = false;				// Reset the Boolean flag
            		boolean foundLowerCase = false;				// Reset the Boolean flag
            		boolean foundNumericDigit = false;			// Reset the Boolean flag
            		boolean foundSpecialChar = false;			// Reset the Boolean flag		// Reset the Boolean flag
            		boolean foundLongEnough = false;
            		boolean otherChar = false;
            		// Reset the Boolean flag
            		running = true;						// Start the loop
            		// The Directed Graph simulation continues until the end of the input is reached or at some 
            		// state the current character does not match any valid transition
            		while (running) {
            			// The cascading if statement sequentially tries the current character against all of the
            			// valid transitions
            			if (currentChar >= 'A' && currentChar <= 'Z') {
            				foundUpperCase = true;
            			} else if (currentChar >= 'a' && currentChar <= 'z') {
            				foundLowerCase = true;
            			} else if (currentChar >= '0' && currentChar <= '9') {
            				foundNumericDigit = true;
            			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
            				foundSpecialChar = true;
            			} else {
            				otherChar = true;                    // RYAN BORBAS CODE
            			}
            			if (currentCharNdx >= 7) {
            				foundLongEnough = true;
            			}
            		
            			// Go to the next character if there is one
            			currentCharNdx++;
            			if (currentCharNdx >= inputLine.length())
            				running = false;
            			else
            				currentChar = password.charAt(currentCharNdx);
            		}
            		
            		if (!foundUpperCase)
            			errorLabel.setText("*** Password Error *** Please include a uppercase letter!");
            		
            		if (!foundLowerCase)
            			errorLabel.setText("*** Password Error *** Please include a lowercase letter!");
            		
            		if (!foundNumericDigit)
            			errorLabel.setText("*** Password Error *** Please include a number!");
            			
            		if (!foundSpecialChar)
            			errorLabel.setText("*** Password Error *** Please include a special character!");
            			
            		if (!foundLongEnough)
            			errorLabel.setText("*** Password Error *** Password not long enough!");
            		}
           		         	            		
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField,inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
