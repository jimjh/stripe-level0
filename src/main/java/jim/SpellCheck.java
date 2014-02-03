package jim;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SpellCheck {

  private static final String PATH = "/usr/share/dict/words";

  private static Set<String> readDict(String path) throws IOException {
    Set<String> words = new HashSet<>();

    FileChannel fch = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
    MappedByteBuffer mbb = fch.map(FileChannel.MapMode.READ_ONLY, 0L, fch.size());

    // while(mbb.hasRemaining()) {
      CharBuffer buffer = StandardCharsets.UTF_8.decode(mbb);
      try(Scanner scanner = new Scanner(buffer)) {
        while(scanner.hasNext()) words.add(scanner.next());
      }
    // }

    fch.close();
    return words;
  }

  private static void filterInput(Set<String> words, InputStream input, PrintStream output) {
    try (Scanner scanner = new Scanner(input, "UTF-8")) {
      scanner.useDelimiter("(?<=\\s)");
      while (scanner.hasNext()) {
        String match = scanner.next();
        String word = match.substring(0, match.length() - 1);
        if (words.contains(word.toLowerCase())) {
          output.print(word);
        } else {
          output.print("<" + word + ">");
        }
        output.print(match.charAt(match.length() - 1));
      }
    }
  }

  public static void main(String[] args) throws IOException {
    String path = (args.length >= 1) ? args[0] : PATH;
    Set<String> words = readDict(path);
    filterInput(words, System.in, System.out);
  }
}
