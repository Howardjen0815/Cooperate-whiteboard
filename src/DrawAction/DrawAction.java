/**
 * The DrawAction class used to define the action which include color, type
 * , and their coordinate place, so let the json file can be read back to whiteboard
 * @version	1.0
 * @author Yu-Han Jen, 1508398, YJEN@student.unimelb.edu.au
 */
package DrawAction;

import java.awt.Color;
import java.io.Serializable;

public class DrawAction implements Serializable {
    public String type;      // will include the style of draw
    public int x1, y1, x2, y2;   // used for start and close coordinate
    public int r, g, b;      // color
    public int thickness;
    public String text;
    public boolean isEraser; // if the user press eraser it will use it to erase
    public int fontSize = 14;
    //defautl fontName
    public String fontName = "SansSerif";


    public DrawAction(String type, int x1, int y1, int x2, int y2, Color color, int thickness, boolean isEraser) {
        this.type = type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.thickness = thickness;
        this.isEraser = isEraser;
    }

    public Color getColor() {
        return new Color(r, g, b);
    }
}
