/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.extern;

public interface IIdentifierRenamer {
    public static final int ELEMENT_CLASS = 1;
    public static final int ELEMENT_FIELD = 2;
    public static final int ELEMENT_METHOD = 3;

    public boolean toBeRenamed(int var1, String var2, String var3, String var4);

    public String getNextClassname(String var1, String var2);

    public String getNextFieldname(String var1, String var2, String var3);

    public String getNextMethodname(String var1, String var2, String var3);
}

