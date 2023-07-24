package io.javablex;

import com.sun.jna.Library;

import java.io.IOException;

public class SimpleBle {
    public static Lib instance;

    public interface Lib extends Library {
        void main();
    }

    static {
        try {
            instance = NativeUtils.loadLibraryFromJar("/lib/libblex.dylib", Lib.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SimpleBle.instance.main();
    }
}
