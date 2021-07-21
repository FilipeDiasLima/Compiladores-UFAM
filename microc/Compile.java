import java.io.*;

public class Compile
{
    private static String objFileName(String s) {
        int i = s.lastIndexOf('.');
        if (i < 0) return s + ".obj";
        else return s.substring(0, i) + ".obj";
    }

    public static void main(String argv[])
    {
        if (argv.length > 0) {
            String source = argv[0];
            String output = objFileName(source);
            Scanner s = new Scanner(source);
            Parser p = new Parser(s);
            p.Parse();
            if (p.errors.count == 0) {
                    try {
                        Code.write(new FileOutputStream(output));
                    } catch (IOException e) {
                        System.out.println("-- impossivel abrir arquivo OBJ " + output);
                    }
            }
        } else System.out.println("-- uso: java Compiler <arquivo-fonte>");
    }
}

