import java.io.*;

public class FSMFileManager {
    public static void saveFSM(FSM fsm, String filePath) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(fsm);
            System.out.println("FSM successfully saved: " + filePath);
        } catch (IOException e) {
            System.out.println("Error while saving FSM: " + e.getMessage());
        }
    }

    public static FSM loadFSM(String filePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (FSM) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while loading FSM: " + e.getMessage());
            return null;
        }
    }
}
