package me.devwckd.dodgeball.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class FileUtils {

    private FileUtils() {}

    public static void copyDir(File src, File dest) {
        try {
            final Path srcPath = Paths.get(src.getAbsolutePath());
            final Path destPath = Paths.get(dest.getAbsolutePath());
            Files.walk(srcPath)
              .forEach(sourcePath -> {
                  Path targetPath = destPath.resolve(srcPath.relativize(sourcePath));
                  try {
                      Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                  } catch (IOException e) {
                      throw new RuntimeException(e);
                  }
              });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
