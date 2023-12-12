import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandlerTests {

    private static final String resourcePath = Paths.get(System.getProperty("user.dir"), "src/main/resources").toString();

    @Test
    public void testPathExists() {
        String fileName = "currency_list.txt";
        Path path = Paths.get(resourcePath, fileName);
        Assertions.assertTrue(Files.exists(path));
    }

    @Test
    public void testPathNotExists() {
        String fileName = "currency2.txt";
        Path path = Paths.get(resourcePath, fileName);
        Assertions.assertFalse(Files.exists(path));
    }

    @Test
    public void testStringToJsonFormat(){
        String s = FileHandler.stringToJsonFormat("1,2,3");
        Assertions.assertEquals("{\"data\":[\"1\",\"2\",\"3\"]}",s);
    }
}
