/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.extern;

import java.util.jar.Manifest;

public interface IDecompilatSaver {
    public void copyFile(String var1, String var2, String var3);

    public void saveFolder(String var1);

    public void saveClassFile(String var1, String var2, String var3, String var4);

    public void saveFile(String var1, String var2, String var3);

    public void createArchive(String var1, String var2, Manifest var3);

    public void saveClassEntry(String var1, String var2, String var3, String var4, String var5);

    public void saveEntry(String var1, String var2, String var3, String var4);

    public void copyEntry(String var1, String var2, String var3, String var4);

    public void closeArchive(String var1, String var2);
}

