package io.javablex.nativex;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class NativeUtils {
    private static final int MIN_PREFIX_LENGTH = 3;
    public static final String NATIVE_FOLDER_PATH_PREFIX = "bci-native-libs";
    private static File temporaryDir;

    private NativeUtils() {
    }

    public static <T extends Library> T loadLibraryFromJar(String path, Class<T> tClass) throws IOException {
        if (null == path || !path.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
        if (filename == null || filename.length() < MIN_PREFIX_LENGTH) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }
        if (temporaryDir == null) {
            temporaryDir = createTempDirectory(NATIVE_FOLDER_PATH_PREFIX);
            temporaryDir.deleteOnExit();
        }
        File temp = new File(temporaryDir, filename);
        try (InputStream is = NativeUtils.class.getResourceAsStream(path)) {
            Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            temp.delete();
            throw e;
        } catch (NullPointerException e) {
            temp.delete();
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }
        try {
//            return temp.getAbsolutePath();
            return Native.load(temp.getAbsolutePath(), tClass);
//            System.load(temp.getAbsolutePath());
        } finally {
            if (isPosixCompliant()) {
                temp.delete();
            } else {
                temp.deleteOnExit();
            }
        }
    }

    private static boolean isPosixCompliant() {
        try {
            return FileSystems.getDefault()
                    .supportedFileAttributeViews()
                    .contains("posix");
        } catch (FileSystemNotFoundException
                 | ProviderNotFoundException
                 | SecurityException e) {
            return false;
        }
    }

    private static File createTempDirectory(String prefix) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File generatedDir = new File(tempDir, prefix + System.nanoTime());

        if (!generatedDir.mkdir())
            throw new IOException("Failed to create temp directory " + generatedDir.getName());

        return generatedDir;
    }
}
