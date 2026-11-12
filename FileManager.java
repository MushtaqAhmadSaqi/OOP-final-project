import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles secure and atomic serialization and deserialization of application data.
 * Includes defense-in-depth measures:
 * - Insecure deserialization protection via ObjectInputFilter (CWE-502)
 * - Path traversal protection (CWE-22)
 * - Atomic file writes to prevent data corruption on sudden interruption (CWE-362)
 */
public class FileManager {

    /**
     * Whitelist of allowed class names for deserialization.
     */
    private static final Set<String> ALLOWED_CLASSES = new HashSet<>(Arrays.asList(
            "java.util.ArrayList",
            "Room",
            "SingleRoom",
            "DoubleRoom",
            "FamilyRoom",
            "Staff",
            "Staff$Shift",
            "Employee",
            "Person",
            "Booking",
            "Guest",
            "Manager",
            "Receptionist",
            "java.time.LocalDate"
    ));

    /**
     * Validates that the filename is a simple, safe file name without path traversal characters.
     */
    private static void validateFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty.");
        }
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\") || filename.contains(":") || filename.contains("\0")) {
            throw new SecurityException("Potential directory traversal or invalid path detected: " + filename);
        }
    }

    /**
     * Atomically saves a list to file using a temporary file and atomic replacement.
     * Prevents file corruption if serialization or the process is interrupted.
     */
    public static <T> boolean saveToFile(String filename, ArrayList<T> list) {
        validateFilename(filename);

        Path targetPath = Paths.get(filename);
        Path tempPath = Paths.get(filename + ".tmp");

        ArrayList<T> safeList = (list != null) ? list : new ArrayList<>();

        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(tempPath, 
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)))) {
                oos.writeObject(safeList);
                oos.flush();
            }

            // Atomic move/replace target file
            try {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException ex) {
            System.err.println("Error saving to " + filename + ": " + ex.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ignored) {}
            return false;
        }
    }

    /**
     * Securely loads a list from file using ObjectInputFilter to prevent deserialization vulnerabilities.
     */
    public static <T> ArrayList<T> loadFromFile(String filename) {
        validateFilename(filename);

        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(filePath, StandardOpenOption.READ)))) {

            // Configure programmatic ObjectInputFilter to reject any unauthorized classes
            ois.setObjectInputFilter(filterInfo -> {
                if (filterInfo.depth() > 20) {
                    return ObjectInputFilter.Status.REJECTED;
                }
                if (filterInfo.arrayLength() > 100000) {
                    return ObjectInputFilter.Status.REJECTED;
                }

                Class<?> clazz = filterInfo.serialClass();
                if (clazz == null) {
                    return ObjectInputFilter.Status.UNDECIDED;
                }

                // Unwrap arrays
                while (clazz.isArray()) {
                    clazz = clazz.getComponentType();
                }

                if (clazz.isPrimitive()) {
                    return ObjectInputFilter.Status.ALLOWED;
                }

                String name = clazz.getName();
                if (ALLOWED_CLASSES.contains(name) || name.startsWith("java.lang.")) {
                    return ObjectInputFilter.Status.ALLOWED;
                }

                return ObjectInputFilter.Status.REJECTED;
            });

            Object data = ois.readObject();
            if (data instanceof ArrayList<?>) {
                @SuppressWarnings("unchecked")
                ArrayList<T> list = (ArrayList<T>) data;
                return list;
            }
            System.err.println("Invalid data format in " + filename + ". Starting with empty list.");
        } catch (FileNotFoundException ex) {
            // Expected on first run
            return new ArrayList<>();
        } catch (InvalidClassException | ClassNotFoundException ex) {
            System.err.println("Data format incompatibility or unauthorized class in " + filename + ": " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error reading from " + filename + ": " + ex.getMessage());
        }
        return new ArrayList<>();
    }
}
