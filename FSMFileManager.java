import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FSMFileManager {

    // Save as serializable
    public static void saveFSM(FSM fsm, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(fsm);
            System.out.println("FSM saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error: Cannot save FSM. " + e.getMessage());
        }
    }

    // load FSM from file
    public static FSM loadFSM(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            FSM fsm = (FSM) ois.readObject();
            System.out.println("FSM loaded from " + filename);
            return fsm;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: Cannot load FSM. " + e.getMessage());
            return null;
        }
    }

    // COMPILE command: serialize FSM into a binary file
    public static void compileFSM(FSM fsm, String filename) {
        if (filename == null || filename.isEmpty()) {
            System.out.println("Error: Filename is missing. Usage: COMPILE filename");
            return;
        }

        if (!filename.endsWith(".bin")) {
            System.out.println("Error: Invalid filename. The compiled file must have a .bin extension.");
            return;
        }

        File file = new File(filename);
        if (file.exists()) {
            System.out.println("Warning: File already exists and will be overwritten: " + filename);
        }

        saveFSM(fsm, filename);
    }
}
