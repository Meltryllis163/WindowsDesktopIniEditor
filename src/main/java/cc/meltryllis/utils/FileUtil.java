package cc.meltryllis.utils;

import java.io.File;
import java.nio.file.NoSuchFileException;

public final class FileUtil {
    public static File readFile(String path) throws NoSuchFileException {
        File file = new File(path);
        if (!file.exists()) {
            throw new NoSuchFileException("");
        }
        return file;
    }
}
