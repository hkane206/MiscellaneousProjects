import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Test {
    public static void main(String[] args) throws Exception {
        GraphProcessor g = new GraphProcessor();
        FileInputStream f = new FileInputStream(new File("data/simple.graph"));
        g.initialize(f);
        g.getAdjList();
    }  
}
