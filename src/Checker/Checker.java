package Checker;
/**
 * The Checker class to check user and manager command line parameter correct or not
 * @version	1.0
 * @author Yu-Han Jen, 1508398, YJEN@student.unimelb.edu.au
 */

public class Checker {
    public static String checkArgsValid(String[] args){
        if (args.length < 3) {
            System.out.println("Incorrect parameter length, please enter three parameter, first is IP address , second is is port, third is your name, thanks Mate!");
            System.exit(1);
        }

        if(!checkInteger(args[1])){
            System.out.println("The port number is not valid, please enter the correct port format, it should be integer, and port range is 1025~65534");
            System.exit(1);
        }
        return args[1];

    }


    public static boolean checkInteger(String port){
        try{
            int portNumber = Integer.parseInt(port);

            return portNumber > 1024 && portNumber< 65535; //port should larger than 1024 to avoid use the <1024
        }catch (NumberFormatException e){
            return false;
        }
    }
}
