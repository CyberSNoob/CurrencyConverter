import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler {

    private static final String resourcePath = Paths.get(System.getProperty("user.dir"), "src/main/resources").toString();

    public static void write(String file, String content){
        Path filePath = Paths.get(resourcePath, file);
        if(!Files.exists(filePath)) throw new IllegalArgumentException("File not found");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString()))){
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonObject readJsonFile(String jsonFile){
        Path path = Paths.get(resourcePath, jsonFile);
        if(Files.exists(path) && isJsonFile(path)){
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))){
                return new Gson().fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                throw new RuntimeException("Error reading file: %s".formatted(path),e);
            }
        }
        throw new IllegalArgumentException("File not found");
    }

    public static boolean isJsonFile(Path path){
        try(FileReader fr = new FileReader(path.toString())){
            JsonElement el = JsonParser.parseReader(fr);
            return el.isJsonObject() || el.isJsonArray();
        }catch (JsonSyntaxException | JsonIOException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readTextFileToString(String file){
        Path filePath = Paths.get(resourcePath, file);
        if(!Files.exists(filePath)) return "";
        StringBuilder sb = new StringBuilder();
        try(BufferedReader r = new BufferedReader(new FileReader(filePath.toString()))){
            String line;
            while((line = r.readLine()) != null) sb.append(line);
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String stringToJsonFormat(String commaDelimitedString){
        List<String> l = Arrays.stream(commaDelimitedString.split(",")).map(String::trim).toList();
        Map<String, List<String>> data = new HashMap<>();
        data.put("data", l);
        return new Gson().toJson(data);
    }

}
