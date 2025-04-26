import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FSMFileManager {

    // Sve as serializeble
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


}
