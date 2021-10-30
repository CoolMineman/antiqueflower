package io.github.coolmineman.antiqueflower;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Manifest;

import de.fernflower.main.DecompilerContext;
import de.fernflower.main.extern.IDecompilatSaver;
import de.fernflower.main.extern.IFernflowerLogger;

public class ThreadSafeDecompilationSaver implements IDecompilatSaver, Closeable {
    private File root;
    private ConcurrentHashMap<String, FileSystem> zipMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Set<String>> mapArchiveEntries = new ConcurrentHashMap<>();

    public ThreadSafeDecompilationSaver(File root) {
        this.root = root;
    }

    private String getAbsolutePath(String path) {
        return new File(root, path).getAbsolutePath();
    }

    private boolean addEntryName(String filename, String entry) {
        return mapArchiveEntries.computeIfAbsent(filename, f -> ConcurrentHashMap.newKeySet()).add(entry);
    }

    public void copyEntry(String source, String destpath, String archivename, String entryName) {
        try {
            String filename = new File(getAbsolutePath(destpath), archivename).getAbsolutePath();

            if (!addEntryName(filename, entryName)) {
                DecompilerContext.getLogger().writeMessage(
                        "Zip entry already exists: " + destpath + "," + archivename + "," + entryName,
                        IFernflowerLogger.WARNING);
                return;
            }

            FileSystem srcarchive = zipMap.computeIfAbsent(source, s -> Util.newJarFileSystem(Paths.get(s)));
            FileSystem dst = zipMap.get(filename);
            Files.copy(srcarchive.getPath(entryName), dst.getPath(entryName));
        } catch (IOException ex) {
            DecompilerContext.getLogger().writeMessage(
                    "Error copying zip file entry: " + source + "," + destpath + "," + archivename + "," + entryName,
                    IFernflowerLogger.WARNING);
            ex.printStackTrace();
        }
    }

    public void copyFile(String source, String destpath, String destfilename) {
        try {
            Files.copy(Paths.get(source), Paths.get(destfilename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveFile(String path, String filename, String content) {
        try {
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(getAbsolutePath(path), filename)), StandardCharsets.UTF_8))) {
                out.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void createArchive(String path, String archivename, Manifest manifest) {
        try {
            File file = new File(getAbsolutePath(path), archivename);
            Path file2 = file.toPath();
            Files.deleteIfExists(file2);
            FileSystem f = Util.newJarFileSystem(file2);
            zipMap.put(file.getAbsolutePath(), f);
            if (manifest != null) {
                Files.createDirectories(f.getPath("META-INF"));
                try (OutputStream s = Files.newOutputStream(f.getPath("META-INF", "MANIFEST.MF"))) {
                    manifest.write(s);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveClassEntry(String path, String archivename, String qualifiedName, String entryName, String content) {
        saveEntry(path, archivename, entryName, content);
    }

    public void saveClassFile(String path, String qualifiedName, String entryName, String content) {
        saveFile(path, entryName, content);
    }

    public void saveEntry(String path, String archivename, String entryName, String content) {
        try {
            String filename = new File(getAbsolutePath(path), archivename).getAbsolutePath();
            FileSystem out = zipMap.get(filename);
            Path p = out.getPath(entryName);
            Path parent = p.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(p), StandardCharsets.UTF_8))) {
                if (content != null) {
                    writer.write(content);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveFolder(String path) {
        File f = new File(getAbsolutePath(path));
        f.mkdirs();
    }

    public void closeArchive(String path, String archivename) {
        String filename = new File(getAbsolutePath(path), archivename).getAbsolutePath();
        zipMap.computeIfPresent(filename, (a, b) -> {
            try {
                b.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public void close() throws IOException {
        for (FileSystem fileSystem : zipMap.values()) {
            fileSystem.close();
        }
    }
}
