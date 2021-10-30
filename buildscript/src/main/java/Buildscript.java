import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import io.github.coolcrabs.brachyura.compiler.java.JavaCompilationUnitBuilder;
import io.github.coolcrabs.brachyura.dependency.JavaJarDependency;
import io.github.coolcrabs.brachyura.project.java.SimpleJavaProject;
import io.github.coolcrabs.brachyura.util.AtomicFile;
import io.github.coolcrabs.brachyura.util.FileSystemUtil;
import io.github.coolcrabs.brachyura.util.JvmUtil;
import io.github.coolcrabs.brachyura.util.PathUtil;
import io.github.coolcrabs.brachyura.util.Util;
import io.github.coolcrabs.javacompilelib.JavaCompilationUnit;

public class Buildscript extends SimpleJavaProject {

    @Override
    public String getJarBaseName() {
        return "antiqueflower";
    }

    @Override
    public List<JavaJarDependency> getDependencies() {
        return Arrays.asList(new JavaJarDependency(getProjectDir().resolve("utils").resolve("fernflower-fix-deobf.jar"), null, null));
    }

    @Override
    public int getJavaVersion() {
        return 8;
    }

    @Override
    public boolean build() {
        JavaCompilationUnit javaCompilationUnit = new JavaCompilationUnitBuilder()
            .sourceDir(getSrcDir())
            .outputDir(getBuildClassesDir())
            .classpath(getCompileDependencies())
            .options(JvmUtil.compileArgs(JvmUtil.CURRENT_JAVA_VERSION, getJavaVersion()))
            .build();
        if (!compile(javaCompilationUnit)) return false;
        try {
            Path outjar = getBuildLibsDir().resolve(getJarBaseName() + ".jar");
            Path outjarsources = getBuildLibsDir().resolve(getJarBaseName() + "-sources.jar");
            Files.deleteIfExists(outjar);
            Files.deleteIfExists(outjarsources);
            try (
                AtomicFile aoutjar = new AtomicFile(outjar);
                AtomicFile aoutjarsources = new AtomicFile(outjarsources);
            ) {
                Files.deleteIfExists(aoutjar.tempPath);
                Files.deleteIfExists(aoutjarsources.tempPath);
                try (
                    FileSystem foutjar = FileSystemUtil.newJarFileSystem(aoutjar.tempPath);
                    FileSystem foutjarsources = FileSystemUtil.newJarFileSystem(aoutjarsources.tempPath);
                ) {
                    PathUtil.copyDir(getBuildClassesDir(), foutjar.getPath("/"));
                    PathUtil.copyDir(getBuildResourcesDir(), foutjar.getPath("/"));
                    PathUtil.copyDir(bin(), foutjar.getPath("/"));
                    PathUtil.copyDir(getSrcDir(), foutjarsources.getPath("/"));
                }
                aoutjar.commit();
                aoutjarsources.commit();
            }
            
            return true;
        } catch (Exception e) {
            throw Util.sneak(e);
        }
    }

    Path bin() {
        return getProjectDir().resolve("src").resolve("main").resolve("bin");
    }

    @Override
    public List<Path> getCompileDependencies() {
        return Arrays.asList(bin());
    }

}
