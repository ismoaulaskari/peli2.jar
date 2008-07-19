package resultbot;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;
import org.jibble.pircbot.*;

public class Main extends Thread {

    ResultBot bot;
    String channel;
    String headline;
    String file;
    String mode;
    
    public Main(ResultBot bot, String channel, String headline, String file, String mode) {
        this.bot = bot;
        this.channel = channel;
        this.headline = headline;
        this.mode = mode;
        
        this.bot.joinChannel(this.channel);
        
        this.bot.sendMessage0(this.channel, "*" + this.headline + "*");
        if(file != null) {
                this.file = file;
                this.bot.sendMessage0(this.channel, "*" + this.file + "*");
        }
    }
    
    
    public void run() {
        try {
          if(this.file != null) {                
                while(true) {
                  this.bot.pollFile(this.file);
                  
                  if(this.mode.equalsIgnoreCase("tnmt")) {
                    this.bot.sendTnmtFilteredStream(this.channel, this.bot.readNamedFile(this.file));
                  }
                  else {
                      if(this.mode.equalsIgnoreCase("playoff")) {                  
                        this.bot.sendPlayoffFilteredStream(this.channel, this.bot.readNamedFile(this.file));
                      }
                  }
                  
                  this.sleep(10);
                }
                
          }
          else
                bot.sendUnfilteredStream(   channel, 
                        new BufferedReader(new 
                            InputStreamReader(System.in)));
        } catch (InterruptedException ie) {
          return;
        } catch (Exception e) {
                System.err.println("Give correct input filename as parameter instead of " + this.file + " .\n" + e);
        }
        
    }

    
    public static void main(String[] args) throws Exception {
        
        String conffile = "irc.properties";
        
        Properties props = new Properties();
        try {
            FileInputStream in = new FileInputStream( conffile );
            props.load(in);
        } catch (FileNotFoundException fe) {
            System.err.println("Missing " + conffile + " -file!");
            System.exit(-1);
        } catch (IOException ie) {
            System.err.println("Reading " + conffile + " failed!");
            System.exit(-1);
        }

        String name = props.getProperty("name");
        String login = props.getProperty("login");
        String finger = props.getProperty("finger");
        String server = props.getProperty("server");
        String channel = props.getProperty("channel");
        String headline = props.getProperty("headline");
        String delay = props.getProperty("delay");
        
        // Now start our bot up.
        ResultBot bot = new ResultBot(name, login, finger);

        // Enable debugging output.
        bot.setVerbose(true);
        
        // try to talk slow and avoid being kicked
        bot.setMessageDelay(Integer.parseInt(delay));
        
        // Connect to the IRC server.
        bot.connect(server);
        
        if(args.length > 0) {            
            StringTokenizer st = new StringTokenizer(args[0], ".");
            
            if(st.countTokens() == 2) {
              String filename = st.nextToken();
              String extension = st.nextToken();
              if(extension.equalsIgnoreCase("tnmt")) { 
                //expecting both a tnmt and a plaoff-txt-file                  
                  new Main(bot, channel, headline, filename + ".txt", "playoff").start();    
              }                                               
            }
            //general tnmt-file reader
            new Main(bot, channel, headline, args[0], "tnmt").start();
        }
        //std input -reader
        new Main(bot, channel, headline, null, "none").start();
    }
}
