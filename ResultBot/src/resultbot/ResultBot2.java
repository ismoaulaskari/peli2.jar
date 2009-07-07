/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package resultbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author aulaskar
 */
public class ResultBot2 extends ResultBot {
    String version = "0.20";
    ArrayList<String> tnmtMemory = new ArrayList<String>();
    
    public ResultBot2(String name, String login, String finger) {
        super(name, login, finger);
        this.setVersion(version);
    }

    /**
     * updated to support peli2.jar tnmt-format,
     * and collect entire tnmt in memory
     * @param channel
     * @param is
     */
    public void sendTnmtFilteredStream(String channel, BufferedReader is) {
        try {
            String inputLine;
            int currentLine = 0;
            while ((inputLine = is.readLine()) != null) {
                inputLine = pipeOnlyIfChanged(filterTnmtLine(inputLine), currentLine++);
                if (inputLine != null) {
                    this.sendMessage0(channel, inputLine);
                }
            }
            is.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        return;
    }

    public String filterTnmtLine(String input) {
        return input;
    }

    private String pipeOnlyIfChanged(String inputLine, int lineNumber) {
        if(this.tnmtMemory.get(lineNumber).equalsIgnoreCase(inputLine)) {
            //line has not changed
            inputLine = null;
        }
        else {
            this.tnmtMemory.remove(lineNumber);
            this.tnmtMemory.add(lineNumber, inputLine);
        }

        return inputLine;
    }

}
