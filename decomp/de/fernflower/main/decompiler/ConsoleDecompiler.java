/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.decompiler;

import de.fernflower.main.DecompilerContext;
import de.fernflower.main.Fernflower;
import de.fernflower.main.decompiler.helper.PrintStreamLogger;
import de.fernflower.main.extern.IBytecodeProvider;
import de.fernflower.main.extern.IDecompilatSaver;
import de.fernflower.main.extern.IFernflowerLogger;
import de.fernflower.util.InterpreterUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ConsoleDecompiler
implements IBytecodeProvider,
IDecompilatSaver {
    private File root;
    private Fernflower fernflower;
    private HashMap mapArchiveStreams = new HashMap();
    private HashMap mapArchiveEntries = new HashMap();

    public ConsoleDecompiler() {
        this(null);
    }

    public ConsoleDecompiler(HashMap hashMap) {
        this(new PrintStreamLogger(3, System.out), hashMap);
    }

    protected ConsoleDecompiler(IFernflowerLogger iFernflowerLogger, HashMap hashMap) {
        this.fernflower = new Fernflower(this, this, hashMap);
        DecompilerContext.setLogger(iFernflowerLogger);
    }

    public static void main(String[] stringArray) {
        try {
            if (stringArray != null && stringArray.length > 1) {
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                ArrayList<String> arrayList = new ArrayList<String>();
                ArrayList<String> arrayList2 = new ArrayList<String>();
                boolean bl = true;
                int n = 0;
                while (n < stringArray.length - 1) {
                    String string = stringArray[n];
                    if (bl && string.startsWith("-") && string.length() > 5 && string.charAt(4) == '=') {
                        Object object = string.substring(5);
                        if ("TRUE".equalsIgnoreCase((String)object)) {
                            object = "1";
                        } else if ("FALSE".equalsIgnoreCase((String)object)) {
                            object = "0";
                        }
                        hashMap.put(string.substring(1, 4), object);
                    } else {
                        bl = false;
                        if (string.startsWith("-e=")) {
                            arrayList2.add(string.substring(3));
                        } else {
                            arrayList.add(string);
                        }
                    }
                    ++n;
                }
                if (arrayList.isEmpty()) {
                    ConsoleDecompiler.printHelp();
                    return;
                }
                ConsoleDecompiler consoleDecompiler = new ConsoleDecompiler(new PrintStreamLogger(2, System.out), hashMap);
                for (String string : arrayList) {
                    consoleDecompiler.addSpace(new File(string), true);
                }
                for (String string : arrayList2) {
                    consoleDecompiler.addSpace(new File(string), false);
                }
                consoleDecompiler.decompileContext(new File(stringArray[stringArray.length - 1]));
                return;
            }
            ConsoleDecompiler.printHelp();
            return;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    private static void printHelp() {
        System.out.println("Usage: java ConsoleDecompiler ( -<option>=<value>)* (<source>)+ <destination>");
        System.out.println("Example: java ConsoleDecompiler -dgs=true c:\\mysource\\ c:\\my.jar d:\\decompiled\\");
    }

    public void addSpace(File file, boolean bl) {
        this.fernflower.getStructcontext().addSpace(file, bl);
    }

    public void decompileContext(File file) {
        this.root = file;
        this.fernflower.decompileContext();
    }

    public InputStream getBytecodeStream(String object, String string) {
        try {
            Object object2 = new File((String)object);
            if (string == null) {
                return new FileInputStream((File)object2);
            }
            object2 = new ZipFile((File)object2);
            object = ((ZipFile)object2).entries();
            while (object.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry)object.nextElement();
                if (!zipEntry.getName().equals(string)) continue;
                return ((ZipFile)object2).getInputStream(zipEntry);
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return null;
    }

    private String getAbsolutePath(String string) {
        return new File(this.root, string).getAbsolutePath();
    }

    private boolean addEntryName(String string, String string2) {
        HashSet<String> hashSet = (HashSet<String>)this.mapArchiveEntries.get(string);
        if (hashSet == null) {
            hashSet = new HashSet<String>();
            this.mapArchiveEntries.put(string, hashSet);
        }
        return hashSet.add(string2);
    }

    public void copyEntry(String object, String object2, String object3, String string) {
        try {
            String string2 = new File(this.getAbsolutePath((String)object2), (String)object3).getAbsolutePath();
            if (!this.addEntryName(string2, string)) {
                DecompilerContext.getLogger().writeMessage("Zip entry already exists: " + (String)object2 + "," + (String)object3 + "," + string, 3);
                return;
            }
            object = new ZipFile(new File((String)object));
            object2 = ((ZipFile)object).entries();
            while (object2.hasMoreElements()) {
                object3 = (ZipEntry)object2.nextElement();
                if (!((ZipEntry)object3).getName().equals(string)) continue;
                object3 = ((ZipFile)object).getInputStream((ZipEntry)object3);
                ZipOutputStream zipOutputStream = (ZipOutputStream)this.mapArchiveStreams.get(string2);
                zipOutputStream.putNextEntry(new ZipEntry(string));
                InterpreterUtil.copyInputStream((InputStream)object3, zipOutputStream);
                ((InputStream)object3).close();
            }
            return;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return;
        }
    }

    public void copyFile(String string, String string2, String string3) {
        try {
            InterpreterUtil.copyFile(new File(string), new File(this.getAbsolutePath(string2), string3));
            return;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return;
        }
    }

    public void saveFile(String string, String string2, String string3) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(new File(((ConsoleDecompiler)((Object)bufferedWriter)).getAbsolutePath(string), string2)), "UTF8"));
            bufferedWriter.write(string3);
            bufferedWriter.flush();
            bufferedWriter.close();
            return;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return;
        }
    }

    public void createArchive(String object, String object2, Manifest manifest) {
        try {
            object = new File(this.getAbsolutePath((String)object), (String)object2);
            ((File)object).createNewFile();
            object2 = manifest != null ? new JarOutputStream((OutputStream)new FileOutputStream((File)object), manifest) : new ZipOutputStream(new FileOutputStream((File)object));
            this.mapArchiveStreams.put(((File)object).getAbsolutePath(), object2);
            return;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return;
        }
    }

    public void saveClassEntry(String string, String string2, String string3, String string4, String string5) {
        this.saveEntry(string, string2, string4, string5);
    }

    public void saveClassFile(String string, String string2, String string3, String string4) {
        this.saveFile(string, string3, string4);
    }

    public void saveEntry(String string, String string2, String string3, String string4) {
        try {
            String string5 = new File(((ConsoleDecompiler)object).getAbsolutePath(string), string2).getAbsolutePath();
            if (!((ConsoleDecompiler)object).addEntryName(string5, string3)) {
                DecompilerContext.getLogger().writeMessage("Zip entry already exists: " + string + "," + string2 + "," + string3, 3);
                return;
            }
            Object object = (ZipOutputStream)((ConsoleDecompiler)object).mapArchiveStreams.get(string5);
            ((ZipOutputStream)object).putNextEntry(new ZipEntry(string3));
            if (string4 != null) {
                object = new BufferedWriter(new OutputStreamWriter((OutputStream)object, "UTF8"));
                ((Writer)object).write(string4);
                ((BufferedWriter)object).flush();
                return;
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public void saveFolder(String string) {
        new File(this.getAbsolutePath(string)).mkdirs();
    }

    public void closeArchive(String string, String string2) {
        try {
            string = new File(((ConsoleDecompiler)((Object)zipOutputStream)).getAbsolutePath(string), string2).getAbsolutePath();
            ((ConsoleDecompiler)zipOutputStream).mapArchiveEntries.remove(string);
            ZipOutputStream zipOutputStream = (ZipOutputStream)((ConsoleDecompiler)zipOutputStream).mapArchiveStreams.remove(string);
            zipOutputStream.flush();
            zipOutputStream.close();
            return;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return;
        }
    }
}

