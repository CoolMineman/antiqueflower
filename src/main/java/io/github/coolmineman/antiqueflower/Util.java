package io.github.coolmineman.antiqueflower;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class Util {
    private Util() { }

    @SuppressWarnings("all")
    public static <T extends Throwable> RuntimeException sneak(Throwable t) throws T {
        throw (T)t;
    }

    public static <T extends Throwable> void unsneak() throws T {
        //noop
    }

    private static final Map<String, String> createArgs = Collections.singletonMap("create", "true");
    private static final FileSystemProvider jarFileSystemProvider;

    static {
        FileSystemProvider jarFileSystemProvider2 = null;
        for (FileSystemProvider fileSystemProvider : FileSystemProvider.installedProviders()) {
            if (fileSystemProvider.getScheme().equals("jar")) {
                jarFileSystemProvider2 = fileSystemProvider;
            }
        }
        Objects.requireNonNull(jarFileSystemProvider2);
        jarFileSystemProvider = jarFileSystemProvider2;
    }

    public static FileSystem newJarFileSystem(Path path) {
        try {
            return jarFileSystemProvider.newFileSystem(path, createArgs);
        } catch (Exception e) {
            throw Util.sneak(e);
        }
    }
}
