//Kullanıcının girdiği komutları ayrıştıran ve yorumlayan sınıf.
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FSMParser {
    private FSM fsm;

    public FSMParser(FSM fsm) {
        this.fsm = fsm;
    }

    public void processCommand(String command) {
        String[] parts = command.split(" ");
        switch (parts[0]) {
            case "SYMBOLS":
                for (int i = 1; i < parts.length; i++) {
                    fsm.addSymbol(parts[i].charAt(0));
                }
                break;
            case "STATES":
                for (int i = 1; i < parts.length; i++) {
                    fsm.addState(new State(parts[i]));
                }
                break;
            case "INITIAL-STATE":
                fsm.setInitialState(new State(parts[1]));
                break;
            case "FINAL-STATES":
                for (int i = 1; i < parts.length; i++) {
                    fsm.addFinalState(new State(parts[i]));
                }
                break;
            case "EXECUTE":
                System.out.println(fsm.execute(parts[1]));
                break;
        }
    }

    public void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processCommand(line.trim());
            }
            System.out.println("File successfully loaded: " + filePath);
        } catch (IOException e) {
            System.out.println("Error while reading file: " + e.getMessage());
        }
    }
}
