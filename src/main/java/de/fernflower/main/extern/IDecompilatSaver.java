package de.fernflower.main.extern;

import java.util.jar.Manifest;

public interface IDecompilatSaver {

    public void copyFile(String source, String destpath, String destfilename);

    public void saveFolder(String path);

    public void saveClassFile(String path, String qualifiedName, String entryName, String content);

    public void saveFile(String path, String filename, String content);

    public void createArchive(String path, String archivename, Manifest manifest);

    public void saveClassEntry(String path, String archivename, String qualifiedName, String entryName, String content);

    public void saveEntry(String path, String archivename, String entryName, String content);

    public void copyEntry(String source, String destpath, String archivename, String entry);

    public void closeArchive(String path, String archivename);

}