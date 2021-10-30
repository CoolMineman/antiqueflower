/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct;

import de.fernflower.main.Fernflower;
import de.fernflower.main.extern.IDecompilatSaver;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.lazy.LazyLoader;
import de.fernflower.struct.lazy.LazyLoader$Link;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public final class ContextUnit {
    private int type;
    private String archivepath;
    private String filename;
    private List classes = new ArrayList();
    private List classentries = new ArrayList();
    private List direntries = new ArrayList();
    private List otherentries = new ArrayList();
    private Manifest manifest;
    private IDecompilatSaver decompilatSaver;
    private Fernflower decompiledData;
    private boolean own = true;

    public ContextUnit(int n, String string, String string2, boolean bl, IDecompilatSaver iDecompilatSaver, Fernflower fernflower) {
        this.type = n;
        this.own = bl;
        this.archivepath = string;
        this.filename = string2;
        this.decompilatSaver = iDecompilatSaver;
        this.decompiledData = fernflower;
    }

    public final void addClass(StructClass structClass, String string) {
        this.classes.add(structClass);
        this.classentries.add(string);
    }

    public final void addDirEntry(String string) {
        this.direntries.add(string);
    }

    public final void addOtherEntry(String string, String string2) {
        this.otherentries.add(new String[]{string, string2});
    }

    public final void reload(LazyLoader lazyLoader) {
        ArrayList<StructClass> arrayList = new ArrayList<StructClass>();
        for (StructClass structClass : this.classes) {
            String string = structClass.qualifiedName;
            structClass = new StructClass(lazyLoader.getClassStream(string), structClass.isOwn(), lazyLoader);
            arrayList.add(structClass);
            LazyLoader$Link lazyLoader$Link = lazyLoader.getClassLink(string);
            lazyLoader.removeClassLink(string);
            lazyLoader.addClassLink(structClass.qualifiedName, lazyLoader$Link);
        }
        this.classes = arrayList;
    }

    public final void save() {
        switch (this.type) {
            case 0: {
                this.decompilatSaver.saveFolder(this.filename);
                for (String[] stringArray : this.otherentries) {
                    this.decompilatSaver.copyFile(stringArray[0], this.filename, stringArray[1]);
                }
                int n = 0;
                while (n < this.classes.size()) {
                    String string;
                    StructClass structClass = (StructClass)this.classes.get(n);
                    String string2 = (String)this.classentries.get(n);
                    if ((string2 = this.decompiledData.getClassEntryName(structClass, string2)) != null && (string = this.decompiledData.getClassContent(structClass)) != null) {
                        this.decompilatSaver.saveClassFile(this.filename, structClass.qualifiedName, string2, string);
                    }
                    ++n;
                }
                return;
            }
            case 1: 
            case 2: {
                this.decompilatSaver.saveFolder(this.archivepath);
                this.decompilatSaver.createArchive(this.archivepath, this.filename, this.manifest);
                for (String[] stringArray : this.direntries) {
                    this.decompilatSaver.saveEntry(this.archivepath, this.filename, (String)stringArray, null);
                }
                for (String[] stringArray : this.otherentries) {
                    if (this.type == 1 && "META-INF/MANIFEST.MF".equalsIgnoreCase(stringArray[1])) continue;
                    this.decompilatSaver.copyEntry(stringArray[0], this.archivepath, this.filename, stringArray[1]);
                }
                int n = 0;
                while (n < this.classes.size()) {
                    StructClass structClass = (StructClass)this.classes.get(n);
                    String string = (String)this.classentries.get(n);
                    if ((string = this.decompiledData.getClassEntryName(structClass, string)) != null) {
                        String string3 = this.decompiledData.getClassContent(structClass);
                        this.decompilatSaver.saveClassEntry(this.archivepath, this.filename, structClass.qualifiedName, string, string3);
                    }
                    ++n;
                }
                this.decompilatSaver.closeArchive(this.archivepath, this.filename);
            }
        }
    }

    public final void setManifest(Manifest manifest) {
        this.manifest = manifest;
    }

    public final boolean isOwn() {
        return this.own;
    }

    public final List getClasses() {
        return this.classes;
    }
}

