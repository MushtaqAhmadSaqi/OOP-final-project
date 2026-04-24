import java.io.*;
import java.util.*;

public class FileManager {

    public static <T> void saveToFile(String filename, ArrayList<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(list);
        } catch (IOException ex) {
            System.out.println("Error saving to " + filename + " : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static <T> ArrayList<T> loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object data = ois.readObject();
            if (data instanceof ArrayList<?>) {
                @SuppressWarnings("unchecked")
                ArrayList<T> list = (ArrayList<T>) data;
                return list;
            }
            System.out.println("Invalid data format in " + filename + ". Starting with empty list...");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found! Starting with empty list...");
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error reading from " + filename + ": " + ex.getMessage());
        }
        return new ArrayList<>();
    }
}
