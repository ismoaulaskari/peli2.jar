package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Match.java

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Understands the inputted match result
 * @author aulaskar
 *
 */
public class Match
{

	//for England..and mutual comparison
	Match(String home, String visitor) 
	{
		this.home = home;
		this.visitor = visitor;
		isOver = false;
	}
	
    Match(String s)
        throws FileFormatException
    {
        isOver = false;
        int i = s.indexOf(':');
        if(i > 0)
        {
            home = s.substring(0, i);
            s = s.substring(i + 1);
        } else
        {
            throw new FileFormatException();
        }
        i = s.indexOf(':');
        if(i > 0)
        {
            visitor = s.substring(0, i);
            s = s.substring(i + 1);
        } else
        {
            visitor = s;
            return;
        }
        i = s.indexOf(':');
        if(i > 0)
        {
            homeGoals = Integer.parseInt(s.substring(0, i));
            visitorGoals = Integer.parseInt(s.substring(i + 1));
            isOver = true;
        }
    }

    public String getHtmlTableRow(int i)
    {
        String s = isOver ? "" + homeGoals : "&nbsp;&nbsp;&nbsp;";
        String s1 = isOver ? "" + visitorGoals : "&nbsp;&nbsp;&nbsp;";
        return "<TR><TD>" + i + "</TD>" + "<TD>" + ":" + "</TD>" + "<TD>" + home + "</TD>" + "<TD ALIGN=CENTER>" + "-" + "</TD>" + "<TD>" + visitor + "</TD>" + "<TD ALIGN=CENTER>" + s + "</TD>" + "<TD ALIGN=CENTER>" + "-" + "</TD>" + "<TD ALIGN=CENTER>" + s1 + "</TD>" + "</TR>";
    }

    public void print()
    {
        System.out.println(home + " - " + visitor);
    }

    public void save(PrintWriter printwriter)
    {
        printwriter.print(home + ":" + visitor);
        printwriter.println(isOver ? ":" + homeGoals + ":" + visitorGoals : "");
    }

    public String getResult()
    {
        if(!isOver)
            return "";
        else
            return homeGoals + "-" + visitorGoals;
    }

    public String getResultInverted()
    {
        if(!isOver)
            return "";
        else
            return visitorGoals + "-" + homeGoals;
    }

    public void setResult(String s)
    {
        s = s.trim();
        if(s.equals(""))
        {
            isOver = false;
            return;
        }
        int i = s.indexOf('-');
        if(i > 0)
            try
            {
                homeGoals = Integer.parseInt(s.substring(0, i));
                visitorGoals = Integer.parseInt(s.substring(i + 1));
                isOver = true;
            }
            catch(NumberFormatException numberformatexception)
            {
                isOver = false;
            }
        else
            isOver = false;
    }

    public String home()
    {
        return home;
    }

    public String visitor()
    {
        return visitor;
    }

    public int homeGoals()
    {
        return homeGoals;
    }

    public int visitorGoals()
    {
        return visitorGoals;
    }

    public boolean isOver()
    {
        return isOver;
    }

    public boolean isDummy()
    {
        return home.equals("X") || visitor.equals("X") || home.equals("") || visitor.equals("");
    }

    private String home;
    private String visitor;
    private int homeGoals;
    private int visitorGoals;
    private boolean isOver;
}
