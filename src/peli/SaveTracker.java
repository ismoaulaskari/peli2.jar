package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
import javax.swing.JButton;

// Decompiler options: packimports(3) 
// Source File Name:   SaveTracker.java
public class SaveTracker {

    public static boolean isSaved() {
        return SaveTracker.isSaved;
    }

    public static String getIsSavedLogo() {
        return SaveTracker.isSavedLogo;
    }

    public static void setIsSaved(boolean isSaved) {
        String text = getRegisteredSaveButton().getText();

        if (SaveTracker.isSaved) {
            if (!isSaved) {
                SaveTracker.isSavedLogo = "*";
                text = text + isSavedLogo;
            }
        } else {
            if (isSaved) {
                SaveTracker.isSavedLogo = "";
                text = text.replaceAll("\\*", isSavedLogo);
            }
        }

        SaveTracker.isSaved = isSaved;
        getRegisteredSaveButton().setText(text);
    }

    public static JButton getRegisteredSaveButton() {
        return registeredSaveButton;
    }

    public static void setRegisteredSaveButton(JButton aRegisteredSaveButton) {
        registeredSaveButton = aRegisteredSaveButton;
    }

    public SaveTracker() {
    }
    private static JButton registeredSaveButton;
    private static boolean isSaved = true;
    private static String isSavedLogo = "";
}
