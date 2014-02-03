package jim;

import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;

public class SpellCheck {

  private static final String PATH = "/usr/share/dict/words";

  private static Set<String> readDict(String path) throws IOException {
    Set<String> words = new HashSet<>();
    Path p = Paths.get(path);
    try(BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
      String line;
      while((line = reader.readLine()) != null) words.add(line.trim());
    }
    return words;
  }

  private static void filterInput(Set<String> words, InputStream input, PrintStream output) {
    try(Scanner scanner = new Scanner(input, "UTF-8")) {
      scanner.useDelimiter("(?<=\\s)");
      while(scanner.hasNext()) {
        String match = scanner.next();
        String word  = match.substring(0, match.length() - 1);
        if (words.contains(word.toLowerCase())) {
          output.print(word);
        } else {
          output.print("<" + word + ">");
        }
        output.print(match.charAt(match.length()-1));
      }
    }
  }

  public static void main(String[] args) throws IOException {
    String path = (args.length >= 1) ? args[0] : PATH;
    Set<String> words = readDict(path);
    filterInput(words, System.in, System.out);
  }

}
