/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct;

import de.fernflower.main.DecompilerContext;
import de.fernflower.main.Fernflower;
import de.fernflower.main.extern.IDecompilatSaver;
import de.fernflower.struct.ContextUnit;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.lazy.LazyLoader;
import de.fernflower.struct.lazy.LazyLoader$Link;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class StructContext {
    private LazyLoader loader;
    private HashMap classes = new HashMap();
    private HashMap units = new HashMap();
    private ContextUnit defaultUnit;
    private IDecompilatSaver saver;
    private Fernflower decdata;

    public StructContext(IDecompilatSaver iDecompilatSaver, Fernflower fernflower, LazyLoader lazyLoader) {
        this.saver = iDecompilatSaver;
        this.decdata = fernflower;
        this.loader = lazyLoader;
        this.defaultUnit = new ContextUnit(0, null, "", true, iDecompilatSaver, fernflower);
        this.units.put("", this.defaultUnit);
    }

    public final StructClass getClass(String string) {
        return (StructClass)this.classes.get(string);
    }

    public final void reloadContext() {
        for (ContextUnit contextUnit : this.units.values()) {
            for (StructClass structClass : contextUnit.getClasses()) {
                this.classes.remove(structClass.qualifiedName);
            }
            contextUnit.reload(this.loader);
            for (StructClass structClass : contextUnit.getClasses()) {
                this.classes.put(structClass.qualifiedName, structClass);
            }
        }
    }

    public final void saveContext() {
        for (Object object : ((StructContext)object).units.values()) {
            if (!((ContextUnit)object).isOwn()) continue;
            ((ContextUnit)object).save();
        }
    }

    public final void addSpace(File file, boolean bl) {
        this.addSpace("", file, bl);
    }

    private void addSpace(String string, File file, boolean bl) {
        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            string = String.valueOf(string) + "/" + (string.length() == 0 ? "" : file.getName());
            int n = fileArray.length - 1;
            while (n >= 0) {
                this.addSpace(string, fileArray[n], bl);
                --n;
            }
            return;
        }
        String string2 = file.getName();
        boolean bl2 = false;
        try {
            if (string2.endsWith(".jar")) {
                this.addArchive(string, file, 1, bl);
                bl2 = true;
            } else if (string2.endsWith(".zip")) {
                this.addArchive(string, file, 2, bl);
                bl2 = true;
            }
        }
        catch (IOException iOException) {
            DecompilerContext.getLogger().writeMessage("Archive file or some parts of its content invalid: " + (string.length() > 0 ? String.valueOf(string) + "/" : "") + string2, 4);
        }
        if (!bl2) {
            ContextUnit contextUnit = (ContextUnit)this.units.get(string);
            if (contextUnit == null) {
                contextUnit = new ContextUnit(0, null, string, bl, this.saver, this.decdata);
                this.units.put(string, contextUnit);
            }
            boolean bl3 = false;
            if (string2.endsWith(".class")) {
                try {
                    StructClass structClass = new StructClass(this.loader.getClassStream(file.getAbsolutePath(), null), bl, this.loader);
                    this.classes.put(structClass.qualifiedName, structClass);
                    contextUnit.addClass(structClass, string2);
                    this.loader.addClassLink(structClass.qualifiedName, new LazyLoader$Link(file.getAbsolutePath(), null));
                    bl3 = true;
                }
                catch (IOException iOException) {
                    DecompilerContext.getLogger().writeMessage("Invalid class file: " + (string.length() > 0 ? String.valueOf(string) + "/" : "") + string2, 4);
                }
            }
            if (!bl3) {
                contextUnit.addOtherEntry(file.getAbsolutePath(), string2);
            }
        }
    }

    private void addArchive(String string, File file, int n, boolean bl) {
        ZipFile zipFile = n == 1 ? new JarFile(file) : new ZipFile(file);
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            Object object = enumeration.nextElement();
            ContextUnit contextUnit = (ContextUnit)this.units.get(String.valueOf(string) + "/" + file.getName());
            if (contextUnit == null) {
                contextUnit = new ContextUnit(n, string, file.getName(), bl, this.saver, this.decdata);
                if (n == 1) {
                    contextUnit.setManifest(((JarFile)zipFile).getManifest());
                }
                this.units.put(String.valueOf(string) + "/" + file.getName(), contextUnit);
            }
            String string2 = ((ZipEntry)object).getName();
            if (!((ZipEntry)object).isDirectory()) {
                if (string2.endsWith(".class")) {
                    object = new StructClass(zipFile.getInputStream((ZipEntry)object), bl, this.loader);
                    this.classes.put(((StructClass)object).qualifiedName, object);
                    contextUnit.addClass((StructClass)object, string2);
                    if (this.loader == null) continue;
                    this.loader.addClassLink(((StructClass)object).qualifiedName, new LazyLoader$Link(file.getAbsolutePath(), string2));
                    continue;
                }
                contextUnit.addOtherEntry(file.getAbsolutePath(), string2);
                continue;
            }
            if (!((ZipEntry)object).isDirectory()) continue;
            contextUnit.addDirEntry(string2);
        }
    }

    public final HashMap getClasses() {
        return this.classes;
    }
}

