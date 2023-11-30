import java.io.IOException;

/**
 * Driver program for MAL ASM to text-based binary.
 * @author djb
 * @version 2023.11.21
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("Usage: java Main file.mal");
        }
        else {
            String inputfile = args[0];
            if(inputfile.endsWith(".mal")) {
                int suffix = inputfile.lastIndexOf(".");
                String outputfile = inputfile.substring(0, suffix) + ".bin";
                try {
                    Assembler assem = new Assembler(inputfile, outputfile);
                    assem.assemble();
                } catch (IOException ex) {
                    System.err.println("Exception parsing: " + inputfile);
                    System.err.println(ex);
                }
            }
            else {
                System.err.println("Unrecognised file type: " + inputfile);
            }
        }
    }
}
