package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HtmlTools.java

import java.io.PrintWriter;

/**
 * most of the html printing, this is where I'll put some CSS classes
 * @author aulaskar
 *
 */
public class HtmlTools
{

    public HtmlTools()
    {
    }

    static final void intro(PrintWriter printwriter, String s)
    {
        printwriter.println("<HTML>");
        printwriter.println("<HEAD>");
        printwriter.println("<STYLE TYPE=\"text/css\">");
        printwriter.println("TD,TH {");
        printwriter.println("font-family : verdana, arial, helvetica, sans-serif;");
        printwriter.println("font-size : 10pt;");
        printwriter.println("}");
        printwriter.println("H1 {");
        printwriter.println("font-family : verdana, arial, helvetica, sans-serif;");
        printwriter.println("}");
        printwriter.println("PRE {");
        printwriter.println("font-family : courier;");
        printwriter.println("}");
        printwriter.println("</STYLE>");
        printwriter.println("<TITLE>" + s + "</TITLE>");
        printwriter.println("</HEAD>");
        printwriter.print("<BODY  vlink=\"#551a8b\" bgcolor=\"#ffffff\" ");
        printwriter.println(" alink=\"#ff0000\" link=\"#0000ee\" text=\"#000000\">");
    }

    static final void insertDate(PrintWriter printwriter, String s)
    {
        printwriter.println("<P>\n<CENTER>\n<H1>" + s + "</H1>\n</CENTER>");
    }

    static final void outro(PrintWriter printwriter)
    {
        printwriter.println("</BODY>");
        printwriter.println("</HTML>");
    }

    static final void h1(PrintWriter printwriter, String s)
    {
        printwriter.println("<CENTER><H1>" + s + "</H1></CENTER>");
    }

    static final void hr(PrintWriter printwriter)
    {
        printwriter.println("<HR>");
    }

    static final void br(PrintWriter printwriter)
    {
        printwriter.println("<BR>");
    }

    static final void pageBreak(PrintWriter printwriter) {
        printwriter.println("<p style=\"page-break-before: always\"/>");
    }

    static final void tableIntro(PrintWriter printwriter, boolean flag)
    {
        printwriter.print("<TABLE WIDTH=\"100%\"");
        if(flag)
            printwriter.println(" BORDER>");
        else
            printwriter.println(">");
    }

    static final void tableIntro(PrintWriter printwriter, boolean flag, String s)
    {
        printwriter.print("<TABLE WIDTH=\"" + s + "\"");
        if(flag)
            printwriter.println(" BORDER>");
        else
            printwriter.println(">");
    }

    static final void tableOutro(PrintWriter printwriter)
    {
        printwriter.println("</TABLE>");
    }

    static final void td(PrintWriter printwriter, String s)
    {
        printwriter.println("<TD>" + s + "</TD>");
    }

    static final String td(String s)
    {
        return "<TD>" + s + "</TD>";
    }

    static final String td(int i)
    {
        return "<TD>" + i + "</TD>";
    }

    static final String tr(String s)
    {
        return "<TR>" + s + "</TR>";
    }

    static final void td(PrintWriter printwriter, String s, int i)
    {
        printwriter.println("<TD COLSPAN=" + i + ">" + s + "</TD>");
    }
}
