/**
 * The Message class is the transfer word definition class
 * @version	1.0
 * @author Yu-Han Jen, 1508398, YJEN@student.unimelb.edu.au
 */
package Chat;

import java.io.Serializable;

public class Message implements Serializable {
    public String sender;
    public String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }
}