package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TournamentFileFilter.java

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.filechooser.FileFilter;

public class TournamentFileFilter extends FileFilter
{

    public TournamentFileFilter()
    {
        /*locale = new Locale(new String("fi"), new String("FI"));
        messages = ResourceBundle.getBundle("Messages", locale);*/
    	locale = Constants.getInstance().getLocale();
    	messages = Constants.getInstance().getMessages();
    }

    public boolean accept(File file)
    {
        if(file.isDirectory())
            return true;
        else
            return file.getName().endsWith(".tnmt");
    }

    public String getDescription()
    {
        return messages.getString("tnmtFileDescription");
    }

    private Locale locale;
    private ResourceBundle messages;
}
