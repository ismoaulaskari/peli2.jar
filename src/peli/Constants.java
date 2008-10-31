package peli;

import java.io.FileInputStream;
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
 * and set it as Class-path in the MANIFEST.MF
 */
public class Constants {
	private static Constants constants = null;
	private static Locale locale = null;
	private static ResourceBundle messages = null;
	private static ResourceBundle keyCodes = null;
	private static ResourceBundle rules = null;
	
	static {
		constants = new Constants();
		locale = new Locale(new String("fi"), new String("FI"));
                try {
                    messages = ResourceBundle.getBundle("Messages", locale);
                    rules = ResourceBundle.getBundle("Rules");
                } 
                catch (MissingResourceException e) {
                    messages = ResourceBundle.getBundle("conf.Messages", locale, Constants.class.getClass().getClassLoader());
                    rules = ResourceBundle.getBundle("conf.Rules", locale, Constants.class.getClass().getClassLoader());
                }
              
                //this should always be found
                keyCodes = ResourceBundle.getBundle("peli.KeyCodeBundle", locale);                
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