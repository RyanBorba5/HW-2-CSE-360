package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import application.User;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(20))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    // THIS IS FOR QUESTIONS IN THE DATABASE
	    String questionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "question TEXT NOT NULL, "
	            + "studentName VARCHAR(255), "
	            + "questionTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "resolved BOOLEAN DEFAULT FALSE, "
	            + "recency INT DEFAULT 0)";
	    statement.execute(questionsTable);

	 // THIS IS FOR ANSWERS IN THE DATABASE
	    String answersTable = "CREATE TABLE IF NOT EXISTS Answers ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "questionId INT, "
	            + "answer TEXT NOT NULL, "
	            + "answerTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "hasBeenRead BOOLEAN DEFAULT FALSE, "
	            + "isSolution BOOLEAN DEFAULT FALSE, "
	            + "studentName VARCHAR(255), "
	            + "FOREIGN KEY (questionId) REFERENCES Questions(id))";
	    statement.execute(answersTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Add a new question to the database
	public int addQuestion(application.Questions question) throws SQLException {
		String insertQuestion = "INSERT INTO Questions (question, studentName, resolved, recency) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, question.getQuestion());
	        pstmt.setString(2, question.getQuestionStudentName());
	        pstmt.setBoolean(3, question.getIsQuestionResolved());
	        pstmt.setInt(4, question.getQuestionRecency());
	        pstmt.executeUpdate();
	        
	        // Get the auto-generated key for the new question
	        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getInt(1);
	            } else {
	                throw new SQLException("Creating question failed, no ID obtained.");
	            }
	        }
	    }
	}


	public void addAnswer(int questionId, application.Answers answer) throws SQLException {
	    String insertAnswer = "INSERT INTO Answers (questionId, answer, hasBeenRead, isSolution, studentName) VALUES (?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer)) {
	        pstmt.setInt(1, questionId);
	        pstmt.setString(2, answer.getAnswer());
	        pstmt.setBoolean(3, answer.getHasAnswerBeenRead());
	        pstmt.setBoolean(4, answer.getIsAnswerSolved());
	        pstmt.setString(5, answer.getAnswerStudentName());
	        pstmt.executeUpdate();
	    }
	}
	
	public void updateAnswerStatus(int answerID, boolean isSolution, boolean hasBeenRead) throws SQLException {
        String updateQuery = "UPDATE Answers SET isSolution = ?, hasBeenRead = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setBoolean(1, isSolution);
            pstmt.setBoolean(2, hasBeenRead);
            pstmt.setInt(3, answerID);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No answer was updated. AnswerID might be incorrect or the answer does not exist.");
            }
        }
    }


	public void updateQuestionStatus(int questionID, boolean resolved) throws SQLException {
	    String updateQuery = "UPDATE Questions SET resolved = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setBoolean(1, resolved);
	        pstmt.setInt(2, questionID);
	        pstmt.executeUpdate();
	    }
	}

	
	public ResultSet getQuestions(boolean onlyUnresolved) throws SQLException {
	    String query = "SELECT * FROM Questions" + (onlyUnresolved ? " WHERE resolved = FALSE" : "");
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        return pstmt.executeQuery();
	    }
	}

	
	public ResultSet getAnswersForQuestion(int questionID) throws SQLException {
	    String query = "SELECT * FROM Answers WHERE questionId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, questionID);
	        return pstmt.executeQuery();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
