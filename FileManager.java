import java.io.*;
import java.util.*;
public class FileManager {

    public static <T> void saveToFile(String filename, ArrayList<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(filename))) 
        {
            oos.writeObject(list);
        } 
        catch (IOException ex) {
            System.out.println("Error saving to " + filename+ " : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static <T> ArrayList<T> loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(filename))) 
        {
            ArrayList<T> list = (ArrayList<T>) ois.readObject();
            return list;
        }
        catch(FileNotFoundException ex) {
            System.out.println("File not found! Starting with empty list...");
        }
        catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error Reading from file...");
        }
        return new ArrayList<>();

    }
}
