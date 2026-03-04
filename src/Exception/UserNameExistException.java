/**
 * The UserNameExistException class used to let the user know the name they enter has been existed so they should change
 * @version	1.0
 * @author Yu-Han Jen, 1508398, YJEN@student.unimelb.edu.au
 */
package Exception;

public class UserNameExistException extends Exception {
    public UserNameExistException(String username) {
        super("Username '" + username + "' is already taken. Please choose a different one.");
    }
}