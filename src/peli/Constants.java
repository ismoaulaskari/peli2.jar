package peli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Constants.java

/**
 * @author aulaskar
 * v1.1 lets make this a global class with settings 
 * as a Singleton
 * To find properties outside jar-file I put them in folder conf
 * @TODO choose language on startup
 * and set it as Class-path in the MANIFEST.MF
 */
public class Constants {
	private static Constants constants = null;
	private static Locale locale = null;
	private static ResourceBundle messages = null;
	private static ResourceBundle keyCodes = null;
	private static ResourceBundle rules = null;
	private static StringBuilder header = new StringBuilder(500);
        private static StringBuilder template = new StringBuilder(500);
        private static StringBuilder footer = new StringBuilder(100);
        private static int MAXMATCHES;
        
	static {
		constants = new Constants();
		locale = new Locale(new String("fi"), new String("FI"));
                //locale = new Locale(new String("en"), new String("US"));
                System.setProperty("Peli.jarVersion", "v. 1.15.0");
                try {
                    messages = ResourceBundle.getBundle("Messages", locale);
                    rules = ResourceBundle.getBundle("Rules");
                    //store html-template in memory:
                    BufferedReader bufferedreader = 
                            new BufferedReader(
                                new FileReader("conf" + File.separatorChar + "header.txt"));
                    String line = null;
                    while ((line = bufferedreader.readLine()) != null) {
                        header.append(line).append(System.getProperty("line.separator"));
                    }

                    bufferedreader = 
                            new BufferedReader(
                                new FileReader("conf" + File.separatorChar + "template.txt"));
                    line = null;
                    while ((line = bufferedreader.readLine()) != null) {
                        template.append(line).append(System.getProperty("line.separator"));
                    }

                    bufferedreader = 
                            new BufferedReader(
                                new FileReader("conf" + File.separatorChar + "footer.txt"));
                    line = null;
                    while ((line = bufferedreader.readLine()) != null) {
                        footer.append(line).append(System.getProperty("line.separator"));
                    }

                } 
                catch (MissingResourceException e) {
                    messages = ResourceBundle.getBundle("conf.Messages", locale, Constants.class.getClass().getClassLoader());
                    rules = ResourceBundle.getBundle("conf.Rules", locale, Constants.class.getClass().getClassLoader());
                }
                catch (FileNotFoundException fe) {
                    System.err.println(fe);
                    System.setProperty("TournamentUseVersion1HtmlOutput", "true");
                }
                catch (IOException ie) {
                    System.err.println(ie);
                    System.setProperty("TournamentUseVersion1HtmlOutput", "true");
                }
                
                //this should always be found
                keyCodes = ResourceBundle.getBundle("peli.KeyCodeBundle", locale);                
                  
                //too big gui dependencies
                //if(rules.containsKey("maxPlayoffMatches")) {
                 //   MAXMATCHES = Integer.parseInt(rules.getString("maxPlayoffMatches"));
                //}
                //else {
                    MAXMATCHES = 7;
                //}
	}

    public static StringBuilder getHeader() {
        return header;
    }
            
    public static StringBuilder getTemplate() {
        return template;
    }

    public static StringBuilder getFooter() {
        return footer;
    }

    public static int getMAXMATCHES() {
        return MAXMATCHES;
    }

    
	private Constants() {
        
	}

	public static Constants getInstance() {
		return constants;
	}

	public static Locale getLocale() {
		return locale;
	}

	public static ResourceBundle getMessages() {
		return messages;
	}

	public static ResourceBundle getKeyCodes() {
		return keyCodes;
	}

	public static ResourceBundle getRules() {
		return rules;
	}

}


/**
 * undocumented evil constants
 * It would strangely appear that this class is never used in the application
 * so I will comment it out.
 */
/*
public class Constants
{

    public Constants()
    {
    }

    public static final int divisionMaxSize = 24;
    public static final int TABLE = 0;
    public static final int MATCHES = 1;
    public static final int DATA = 2;
    public static final int HTML = 3;
    public static final boolean NO_BORDER = false;
    public static final boolean BORDER = true;
    public static final int UP = 0;
    public static final int DOWN = 1;
}
*/