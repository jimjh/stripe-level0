package jim;

import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;

public class SpellCheck {

  private static final String PATH = "/usr/share/dict/words";

  public static void main(String[] args) {

    String path = (args.length >= 1) ? args[0] : PATH;

    Set<String> words = new HashSet<>();
    Path p = Paths.get(PATH);
    try(BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
      String line;
      while((line = reader.readLine()) != null) words.add(line.trim());
    } catch (IOException e) {
      System.err.println("caught exception: " + e.getMessage());
      e.printStackTrace(System.err);
    }

    try(Scanner scanner = new Scanner(System.in, "UTF-8")) {
      scanner.useDelimiter("(?<=\\s)");
      while(scanner.hasNext()) {
        String match = scanner.next();
        String word  = match.substring(0, match.length() - 1);
        if (words.contains(word.toLowerCase())) {
          System.out.print(word);
        } else {
          System.out.print("<" + word + ">");
        }
        System.out.print(match.charAt(match.length()-1));
      }
    }

  }

}
