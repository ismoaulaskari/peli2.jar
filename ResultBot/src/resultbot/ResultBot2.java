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

    String version = "0.21";
    ArrayList<String> tnmtMemory = new ArrayList<String>();
    private boolean isPlayoff = false;

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
            int lineNumber = 0;
            while ((inputLine = is.readLine()) != null) {
                inputLine = pipeOnlyIfChanged(smartFilterTnmtLine(inputLine), lineNumber++);
                if (inputLine != null) {
                    this.sendMessage0(channel, inputLine);
                }
            }
            this.isPlayoff = false;
            is.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        return;
    }

    /** choose tnmt lines to print or to ignore */
    public String filterDivisionTnmtLine(String inputLine) {
        this.currentLine++;

        if (inputLine.matches("^END-OF.*")) {
            return null;
        }

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

                    if (inputLine.matches("^PLAYOFFS-SIZE:.*")) {
                        this.isPlayoff = true;
                    }

                    /*if (inputLine.matches("^ROUND:.*")) {

                    return inputLine;
                    }*/

                    if (!this.isFirstRun) {
                        return null;
                    }

                }
            }

            return inputLine;
        }

        return null;
    }

    /**
     * print larger picture
     * @param inputLine
     * @return
     */
    private String filterPlayoffTnmtLine(String inputLine) {
        if (inputLine.matches("^END-OF.*")) {
            return null;
        }

        if (inputLine.matches("^PLAYOFF-SIZE:.*")) {
            return inputLine;
        }

        //empty result
        if (inputLine.matches("^[^:]+:[^:]+$")) {
            return null;
        }


        if (this.isPlayoff) {
            if (inputLine.matches(".*PLAYOFFPAIR.*")) {
                return null;
            }
            else {
                return inputLine;
            }
        } else {
            return null;
        }

    }

    public String smartFilterTnmtLine(String input) {
        if (this.isPlayoff) {
            input = filterPlayoffTnmtLine(input);
        } else {
            input = filterDivisionTnmtLine(input);
        }

        return input;
    }

    private String pipeOnlyIfChanged(String inputLine, int lineNumber) {
        return inputLine;
    /*
    if (this.tnmtMemory.size() > lineNumber &&
    this.tnmtMemory.get(lineNumber).equalsIgnoreCase(inputLine)) {
    //line has not changed
    System.out.println(this.tnmtMemory.get(lineNumber) + "=" + inputLine);
    inputLine = null;
    } else {
    try {
    System.out.print("REMOVE" + this.tnmtMemory.remove(lineNumber));
    this.tnmtMemory.add(lineNumber, inputLine);
    } catch (IndexOutOfBoundsException ie) {
    //ok
    }
    }

    return inputLine;*/
    }
}
