package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FileTools.java

import java.awt.Container;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileTools
{
    
    private static ResourceBundle messages;
    
    
    public FileTools()
    {
        
    }

    public static File askFileName(String s, Container container, FileFilter filefilter)
    {
        messages = Constants.getInstance().getMessages();
        JFileChooser jfilechooser = new JFileChooser(new File("."));
        jfilechooser.setAcceptAllFileFilterUsed(false);
        jfilechooser.setDialogTitle(s);
        jfilechooser.addChoosableFileFilter(filefilter);
        int i = jfilechooser.showDialog(container, messages.getString("openFile"));
        switch(i)
        {
        case 0: // '\0'
            return jfilechooser.getSelectedFile();

        case -1: 
        case 1: // '\001'
        default:
            return new File("");
        }
    }

    public static File canonize(File file, String s)
    {
        if(file.getName().endsWith(s))
            return file;
        else
            return new File(file.getName() + s);
    }
}
