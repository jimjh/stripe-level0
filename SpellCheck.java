import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;

@SuppressWarnings("unchecked")
public class SpellCheck {

  private static final String PATH = "/usr/share/dict/words";

  public static void main(String[] args) {

    // String path = (args.length >= 1) ? args[0] : PATH;

    //Set<String> words = new HashSet<>();
    //Path p = Paths.get(PATH);
    //try(BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
    //  String line;
    //  while((line = reader.readLine()) != null) words.add(line.trim());
    //} catch (IOException e) {
    //  System.err.println("caught exception: " + e.getMessage());
    //  e.printStackTrace(System.err);
    //}

    Path objFile = Paths.get("dict.bin");
    //try(OutputStream buffer = Files.newOutputStream(objFile, StandardOpenOption.CREATE_NEW)) {
    //  ObjectOutput output = new ObjectOutputStream(buffer);
    //  output.writeObject(words);
    //} catch(IOException e) {
    //  System.err.println("caught exception: " + e.getMessage());
    //  e.printStackTrace(System.err);
    //}

    Set<String> words;
    try(InputStream buffer = Files.newInputStream(objFile, StandardOpenOption.READ)) {
      ObjectInput input = new ObjectInputStream(buffer);
      words = (Set<String>) input.readObject();
    } catch(IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    try(Scanner scanner = new Scanner(System.in, "UTF-8")) {
      while(scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split("\\s");
        for (int i=0; i<parts.length; i++) {
          String word = parts[i];
          if (words.contains(word.toLowerCase())) {
            System.out.print(word);
          } else {
            System.out.print("<" + word + ">");
          }
          if (i != parts.length - 1) System.out.print(" ");
        }
        System.out.println();
      }
    }

  }

}
