/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resultbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
                inputLine = pipeOnlyIfChanged(filterTnmtLine3(inputLine), currentLine++);
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

    /** choose tnmt lines to print or to ignore */
    public String filterTnmtLine3(String inputLine) {
        this.currentLine++;

        //only print new lines
        if ((this.currentLine > this.lastPrintedLine) && (this.stop == false)) {
            StringTokenizer st = new StringTokenizer(inputLine, ":");

            //RESULT
            if (st.countTokens() >= 4) {
                this.lastPrintedLine = this.currentLine;
                this.resultsFound = true;

            } else {

                //intro text?
                if (this.isFirstRun) {
                    String token = st.nextToken();
                    if (token.equalsIgnoreCase("ROUNDS")) {
                        this.stop = true;
                        this.isFirstRun = false;
                    }
                } //end of round?
                else if (this.resultsFound) {
                    this.stop = true;
                } else { //too far ahead (someone pressed save)?
                    if (inputLine.matches("^ROUND:")) {
                        return inputLine;
                    } else {
                        if (!this.isFirstRun) {
                            return null;
                        }
                    }
                }
            }

            if (inputLine.matches("^END-OF")) {
                return null;
            } else {
                return inputLine;
            }
        }

        return null;
    }

    /*
    public String filterTnmtLine(String input) {
    return input;
    }
     */
    private String pipeOnlyIfChanged(String inputLine, int lineNumber) {
        /*if(this.tnmtMemory.size() > lineNumber &&
        this.tnmtMemory.get(lineNumber).equalsIgnoreCase(inputLine)) {
        //line has not changed
        inputLine = null;
        }
        else {
        try {
        this.tnmtMemory.remove(lineNumber);
        this.tnmtMemory.add(lineNumber, "Aa" + lineNumber + inputLine);
        }
        catch(IndexOutOfBoundsException ie) {
        //ok
        }
        }
         */
        return inputLine;
    }
}
