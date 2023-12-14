import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Coordinate the translation of MAL assembly code to text-based binary.
 *
 * @author Your name.
 * @version
 */

public class Assembler {
  // The lines of the input file.
  private List<String> input;
  // Where to write the output.
  private PrintWriter output;
  /**
   * Create an assembler.
   * @param inputfile The input file.
   * @param outputfile The output file.
   */
  public Assembler(String inputfile, String outputfile) throws IOException {
    input = Files.readAllLines(Paths.get(inputfile));
    output = new PrintWriter(new FileWriter(outputfile));
  }

  /**
   * Translate the input file, line by line.
   *
   */
  public void assemble() {
    for (String line : input) {
      translateOneInstruction(line);
    }
    output.close();
  }

  /**
   * Translate one line of MAL assembly code to text-based binary.
   * @param line The line to translate.
   */
  private void translateOneInstruction(String line) {
    line = line.replaceAll("\\s+", "");

    int length = 0;

    HashMap<String, String[]> intructions = new HashMap<String, String[]>();
    intructions.put("LOADN", new String[] {"0000", "5"});
    intructions.put("LOADA", new String[] {"0001", "5"});
    intructions.put("ADD", new String[] {"0010", "3"});
    intructions.put("SUB", new String[] {"0011", "3"});
    intructions.put("JMP", new String[] {"0100", "3"});
    intructions.put("JGT", new String[] {"0101", "3"});
    intructions.put("JLT", new String[] {"0110", "3"});
    intructions.put("JEQ", new String[] {"0111", "3"});
    intructions.put("COPY", new String[] {"1000", "4"});
    intructions.put("STORE", new String[] {"1001", "5"});

    HashMap<String, String> reg = new HashMap<>();
    reg.put("A", "01");
    reg.put("D", "10");

    String foundInstruction = "";

    for (String key : intructions.keySet()) {
      if (line.contains(key)) {
        foundInstruction = key;
      }
    }

    String[] instructArr = intructions.get(foundInstruction);
    length = Integer.parseInt(instructArr[1]);

    String[] lineSegments = new String[3];
    lineSegments[0] = line.substring(0, length);

    if (foundInstruction.equals("LOADN") || foundInstruction.equals("LOADA")
            || foundInstruction.equals("JMP") || foundInstruction.equals("STORE")) {
      if (foundInstruction.equals("LOADA")) {
        // speration:
        lineSegments[1] = line.substring(length);
        lineSegments[2] = "null";
        // output:
        output.println(instructArr[0] + "0000");
        int number = Integer.parseInt(lineSegments[1].replaceAll("[^0-9]", ""));
        output.println(String.format("%8s", Integer.toBinaryString(number))
                           .replaceAll(" ", "0"));
      } else if (foundInstruction.equals("LOADN")) {
        lineSegments[1] = line.substring(length, length + 3);
        lineSegments[2] = "null";
        // output:
        output.println(instructArr[0] + "0000");
        int number = Integer.parseInt(lineSegments[1].replaceAll("[^0-9]", ""));
        output.println(String.format("%8s", Integer.toBinaryString(number))
                           .replaceAll(" ", "0"));

      } else if (foundInstruction.equals("STORE")) {
        // Separation
        lineSegments[1] = line.substring(length, length + 1);
        lineSegments[2] = "null";

        // Output
        output.println(instructArr[0] + "0000");
        int number = Integer.parseInt(lineSegments[1].replaceAll("[^0-9]", ""));
        output.println(String.format("%8s", Integer.toBinaryString(number))
                           .replaceAll(" ", "0"));
      } else {
        lineSegments[1] = line.substring(length, length + 1);
        lineSegments[2] = line.substring(length + 1);
        // output:
        output.println(instructArr[0] + "0000");
        int number = Integer.parseInt(lineSegments[1].replaceAll("[^0-9]", ""));
        output.println(String.format("%8s", Integer.toBinaryString(number))
                           .replaceAll(" ", "0"));
      }
    } else if (foundInstruction.equals("ADD") || foundInstruction.equals("SUB")
        || foundInstruction.equals("COPY")) {
      // seperation
      lineSegments[1] = line.substring(length, length + 1);
      lineSegments[2] = line.substring(length + 2, length + 3);

      // output
      output.println(instructArr[0] + reg.get(lineSegments[1])
          + reg.get(lineSegments[2]));
    } else if (foundInstruction == "JGT" || foundInstruction == "JLT"
        || foundInstruction == "JEQ") {
      // seperation
      lineSegments[1] = line.substring(length, length + 1);
      lineSegments[2] = line.substring(length + 2);

      // output
      output.println(instructArr[0] + reg.get(lineSegments[1]) + "00");
      int number = Integer.parseInt(lineSegments[2].replaceAll("[^0-9]", ""));
      output.println(String.format("%8s", Integer.toBinaryString(number))
                         .replaceAll(" ", "0"));
    }
  }
}
