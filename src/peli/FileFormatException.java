package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FileFormatException.java


public class FileFormatException extends Exception
{

    public FileFormatException()
    {
        this.printStackTrace();
    }

    public FileFormatException(FileFormatException e, int lineNumber) {        
        e.printStackTrace();
        System.err.println("FileFormatException at line " + lineNumber);
    }
}
