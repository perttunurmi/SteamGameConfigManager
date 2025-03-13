package configManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** LinkManager */
public class LinkManager {

  public static void createLink(final File src, final File target) {
    System.out.println("Creating symbolic directory link from " + src + " to " + target);

    if (target.exists()) {
      System.err.println("Link target already exists: " + target);
      return;
    }

    try {
      Path path = Files.createSymbolicLink(target.toPath(), src.toPath());
      System.out.println("path to symbolic link: " + path);
    } catch (IOException error) {
      error.printStackTrace();
    }
  }

  public static void removeLink(final File src) {
    System.out.println("Removing symbolic directory link from " + src);
    try {
      Files.delete(src.toPath());
    } catch (IOException error) {
      error.printStackTrace();
    }
  }

  public static boolean isSymbolicLink(final File file) {
    final Path path = file.toPath();
    return Files.isSymbolicLink(path);
  }

  /**
   * @param file
   * @return Target of the symbolic link or null
   */
  public static Path getLinkTarget(final File file) {
    final Path path = file.toPath();
    Path target = null;
    try {
      target = Files.readSymbolicLink(path);
    } catch (final IOException error) {
      System.out.println("Failed to read symbolic link: " + error.getMessage());
    }
    return target;
  }
}
