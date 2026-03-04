/**
 * The JoinRejectException class used to let the user know what happen when the manager reject them
 * @version	1.0
 * @author Yu-Han Jen, 1508398, YJEN@student.unimelb.edu.au
 */
package Exception;
//user fo who want to join been rejected
public class JoinRejectException extends Exception {
    public JoinRejectException(String username)  {
        super( username + " has already been rejected by manager.");
    }
}
