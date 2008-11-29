/*
 * ResultBot.java
 * 
 * Created on 16/10/2007, 23:53:14
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package resultbot;

/**
 *
 * @author aulaskar
 */
        
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import org.jibble.pircbot.*;

public class ResultBot extends PircBot {
    
    String version = "0.11";
    private boolean isFirstRun = true;
    private boolean stop = false;
    private boolean resultsFound = false;
    private String lastLine = null;
    private int lastRound = 0;
    private int currentRound = 1;
    private long lastPrintedLine = 0;
    private long currentLine = 0;
    private ArrayList playoffs = new ArrayList();
    
    public ResultBot() {
        this.setName("Tulospalvelu");
        this.setLogin("tnnne");
        this.setFinger("Tuloksia reaalisti!");
        this.setVersion(version);
    }
    
    public ResultBot(String name, String login, String finger) {
        this.setName(name);
        this.setLogin(login);
        this.setFinger(finger);
        this.setVersion(version);
    }
    
    
    /** this will loop until file is modified */
    public void pollFile(String filename) throws IOException, InterruptedException {
        File f = new File(filename);
        Thread.sleep(5000);
        if(f.exists() && f.canRead()) {
            Date d = new Date();
            Date d2 = new Date();
            d.setTime(f.lastModified());
            while(true) {
                d2.setTime(f.lastModified());
                if(d.compareTo(d2) != 0) {
                    this.currentLine = 0;
                    this.stop = false;
                    this.resultsFound = false;
                    return;
                }
                Thread.sleep(5000);    
            }
            
        } else {
            throw new IOException("Input file can't be read.");
        }
            
    }
    
    
    
    public BufferedReader readNamedFile(String filename) {
        BufferedReader is = null;
        try {
           is = new BufferedReader(
                    new FileReader(filename)
                    );
            String inputLine;
            
            
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        
        return is;
    }

    
    /** send unfiltered input stream */
    public void sendUnfilteredStream(String channel, BufferedReader is) {
        try {
            String inputLine;
            
            while ((inputLine = is.readLine()) != null) {
                this.sendMessage0(channel, inputLine);
            }
            is.close();
            
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        
        return;
    }

    
    /** send tnmt-file filtered by tnmt filter */
    public void sendTnmtFilteredStream(String channel, BufferedReader is) {
        try {
            String inputLine;
            
            while ((inputLine = is.readLine()) != null) {
                inputLine = filterTnmtLine2(inputLine);
                if(inputLine != null ) 
                    this.sendMessage0(channel, inputLine);                    
            }
            is.close();
            
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        
        return;
    }

    /** send txt-file filtered by playoff filter */
    public void sendPlayoffFilteredStream(String channel, BufferedReader is) {
        try {
            String inputLine;
            int i = 0;
            
            while ((inputLine = is.readLine()) != null) {
                if(isFirstRun) {
                    this.playoffs.add(i, inputLine);                    
                } 
                else {
                    //print only changed lines
                    if(this.playoffs.size() > i && this.playoffs.get(i).equals(inputLine)) {
                        inputLine = null;
                    }
                    else {
                        if(this.playoffs.size() > i)
                          this.playoffs.set(i, inputLine);
                        else
                          this.playoffs.add(i, inputLine);
                    }
                }
                i++;                
                inputLine = filterPlayoffLine(inputLine);
                
                if(inputLine != null ) 
                    this.sendMessage0(channel, "Playoff: " + inputLine);                    
            }
            is.close();
            this.isFirstRun = false;
            
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        
        return;
    }

    
    /** print lines relevant to playoff matches */
    public String filterPlayoffLine(String inputLine) {       
        String token = null;
                    
        if(inputLine == null ) 
            return null;
        
        StringTokenizer st = new StringTokenizer(inputLine, "-");
        //not a match
        if(st.countTokens() < 2) {
            return null;
        }
                
        return inputLine;
    }
    
    
    /** choose tnmt lines to print or to ignore */
    public String filterTnmtLine(String inputLine) {
        this.currentLine++;
        String token = null;
        
        
        if(this.currentLine > this.lastPrintedLine) {
            if(this.stop) {
                return null;
            }            
        
            StringTokenizer st = new StringTokenizer(inputLine, ":");            
            if(st.countTokens() >= 4) {
                this.resultsFound = true;
                //this.stop = true;
            }
            else {
                if(this.resultsFound) {
                    this.stop = true;  
                }
            }
            
            token = st.nextToken();
            if(this.isFirstRun) {
                if(token.equalsIgnoreCase("rounds")) {
                    this.stop = true;
                    this.isFirstRun = false;
                }
            }
            else {
                if(st.countTokens() < 3 && 
                        st.countTokens() > 0 && 
                        ! token.equalsIgnoreCase("ROUND") && 
                        ! token.equalsIgnoreCase("END-OF-ROUND")) {
                    //inputLine = "Odotamme tulosta " + inputLine;
                    //this.stop = true;
                    return null;
                }
            }
            
            this.lastPrintedLine++;                               
            return inputLine;
        }         
        
        return null;
    }

    /** choose tnmt lines to print or to ignore */
    public String filterTnmtLine2(String inputLine) {
        this.currentLine++;
        
        //only print new lines
        if((this.currentLine > this.lastPrintedLine) && (this.stop == false)) {
            StringTokenizer st = new StringTokenizer(inputLine, ":");       
                          
            //RESULT
            if(st.countTokens() >= 4) {            
                this.lastPrintedLine = this.currentLine;            
                this.resultsFound = true;
                                
            }           
            else { 
                
                //intro text?                
                if(this.isFirstRun) {
                    String token = st.nextToken();
                    if(token.equalsIgnoreCase("ROUNDS")) {
                        this.stop = true;
                        this.isFirstRun = false;
                    }
                }                                
                //end of round?
                else if(this.resultsFound) {            
                    this.stop = true;
                }            
                else { //too far ahead (someone pressed save)?
                    
                    if(! this.isFirstRun) return null;
                }
            }
            
            return inputLine;
        }
        
        return null;
    }

    /** print wrapper for testing purposes */
    public void sendMessage0(String channel, String message) {
        //System.out.println(channel+ " " +message);
        this.sendMessage(channel, message);
    }
}
