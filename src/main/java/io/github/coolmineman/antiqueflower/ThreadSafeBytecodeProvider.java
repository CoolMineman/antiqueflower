package io.github.coolmineman.antiqueflower;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.fernflower.main.extern.IBytecodeProvider;

public class ThreadSafeBytecodeProvider implements Closeable, IBytecodeProvider {
    private final Map<String, FileSystem> fileSystems = new ConcurrentHashMap<>();

    @Override
    public void close() throws IOException {
        for (FileSystem fileSystem : fileSystems.values()) {
            fileSystem.close();
        }
    }

    @Override
    public InputStream getBytecodeStream(String externPath, String internPath) {
        try {
            if (internPath == null) {
                return new FileInputStream(externPath);
            } else {
                return Files.newInputStream(fileSystems.computeIfAbsent(externPath, p -> Util.newJarFileSystem(Paths.get(p))).getPath(internPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
