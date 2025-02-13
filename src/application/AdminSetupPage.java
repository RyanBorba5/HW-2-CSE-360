package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
    
    private final DatabaseHelper databaseHelper;
    private Label errorLabel = new Label();

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
            // Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            
            // Reset error label
            errorLabel.setText("");
            
            if(validateUserName(userName) && validatePassword(password)) {
                try {
                    // Create a new User object with admin role and register in the database
                    User user = new User(userName, password, "admin");
                    databaseHelper.register(user);
                    System.out.println("Administrator setup completed.");
                    
                    // Navigate to the Welcome Login Page
                    new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                } catch (SQLException e) {
                    System.err.println("Database error: " + e.getMessage());
                    e.printStackTrace();
                    errorLabel.setText("Database error: " + e.getMessage());
                }
            }
        });

        VBox layout = new VBox(10, userNameField, passwordField, setupButton, errorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }

    private boolean validateUserName(String userName) {
        if(userName.length() <= 0) {
            errorLabel.setText("*** ERROR *** A UserName must start with A-Z, or a-z.\n");
            return false;
        }

        int userNameSize = 0;
        boolean noerrors = true;
        int state = 0;
        int currentCharNdx = 0;
        char currentChar = userName.charAt(0);

        while (currentCharNdx < userName.length()) {
            switch (state) {
                case 0:
                    if ((currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z')) {
                        state = 1;
                        userNameSize++;
                    } else {
                        errorLabel.setText("*** ERROR *** A UserName must start with A-Z, or a-z.\n");
                        return false;
                    }
                    break;
                case 1:
                    if ((currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z') || 
                        (currentChar == '_') || (currentChar == '-') || (currentChar == '.') || 
                        (currentChar >= '0' && currentChar <= '9')) {
                        state = 1;
                        userNameSize++;
                    } else {
                        errorLabel.setText("*** ERROR *** A UserName character may only contain the characters A-Z, a-z, 0-9, underscores, minus signs, and periods.\n");
                        return false;
                    }
                    if (userNameSize > 16) {
                        errorLabel.setText("*** ERROR *** A UserName must have no more than 16 characters.\n");
                        return false;
                    }
                    break;
            }

            currentCharNdx++;
            if (currentCharNdx < userName.length()) {
                currentChar = userName.charAt(currentCharNdx);
            } else {
                break;
            }
        }

        if (userNameSize < 4) {
            errorLabel.setText("*** ERROR *** A UserName must have at least 4 characters.\n");
            return false;
        }

        // Check if the username ends with a special character
        if (currentChar == '.' || currentChar == '-' || currentChar == '_') {
            errorLabel.setText("*** ERROR *** A UserName cannot end with a period, minus sign, or underscore.\n");
            return false;
        }

        return noerrors;
    }

    private boolean validatePassword(String password) {
        if(password.length() <= 0) {
            errorLabel.setText("*** Password Error *** Please make a password!");
            return false;
        }

        boolean foundUpperCase = false, foundLowerCase = false, foundNumericDigit = false, 
                foundSpecialChar = false, foundLongEnough = false;

        for (char c : password.toCharArray()) {
            if (c >= 'A' && c <= 'Z') foundUpperCase = true;
            else if (c >= 'a' && c <= 'z') foundLowerCase = true;
            else if (c >= '0' && c <= '9') foundNumericDigit = true;
            else if ("~`!@#$%^&*()_+-={}[]|\\:;\"'<>,.?/".indexOf(c) >= 0) foundSpecialChar = true;

            if (password.length() >= 8) foundLongEnough = true;
        }

        if (!foundUpperCase) {
            errorLabel.setText("*** Password Error *** Please include an uppercase letter!");
            return false;
        }
        if (!foundLowerCase) {
            errorLabel.setText("*** Password Error *** Please include a lowercase letter!");
            return false;
        }
        if (!foundNumericDigit) {
            errorLabel.setText("*** Password Error *** Please include a number!");
            return false;
        }
        if (!foundSpecialChar) {
            errorLabel.setText("*** Password Error *** Please include a special character!");
            return false;
        }
        if (!foundLongEnough) {
            errorLabel.setText("*** Password Error *** Password must be at least 8 characters long!");
            return false;
        }

        return true;
    }
}