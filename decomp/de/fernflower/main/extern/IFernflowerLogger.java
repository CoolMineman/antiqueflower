/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.extern;

import de.fernflower.main.extern.IFernflowerLogger$1;
import java.util.HashMap;

public interface IFernflowerLogger {
    public static final int TRACE = 1;
    public static final int INFO = 2;
    public static final int WARNING = 3;
    public static final int ERROR = 4;
    public static final int IMMEDIATE = 5;
    public static final HashMap mapLogLevel = new IFernflowerLogger$1();
    public static final String[] names = new String[]{"", "DEBUG", "INFO", "WARNING", "ERROR", ""};

    public void writeMessage(String var1, int var2);

    public void writeMessage(String var1, int var2, int var3);

    public void startClass(String var1);

    public void endClass();

    public void startWriteClass(String var1);

    public void endWriteClass();

    public void startMethod(String var1);

    public void endMethod();

    public int getSeverity();

    public void setSeverity(int var1);

    public boolean getShowStacktrace();
}

