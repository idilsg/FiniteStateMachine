//Kullanıcının girdiği komutları ayrıştıran ve yorumlayan sınıf.
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class FSMParser {
    private FSM fsm;

    public FSMParser(FSM fsm) {
        this.fsm = fsm;
    }

    public void processCommand(String command) {
        String[] parts = command.split(" ");
        switch (parts[0]) {
            case "SYMBOLS":
                // if no symbols are provided, print the existing symbol list
                if (parts.length == 1) {
                    System.out.println("Current Symbols: " + fsm.describeSymbols());
                    break;
                }

                // loop each symbol argument
                for (int i = 1; i < parts.length; i++) {
                    char symbol = parts[i].charAt(0); // get the first character of each entry

                    // if symbol is alphanumeric (letters and digits only)
                    if (!Character.isLetterOrDigit(symbol)) {
                        System.out.println("Warning: Invalid symbol '" + symbol + "' ignored (non-alphanumeric)");
                    }
                    // try to add the symbol in lowercase (case-insensitive check)
                    else if (!fsm.getSymbols().add(Character.toLowerCase(symbol))) {
                        System.out.println("Warning: Duplicate symbol '" + symbol + "'");
                    }
                }
                break;

            case "STATES":
                // if no states provided, print all current states and mark initial/final
                if (parts.length == 1) {
                    // STATES q0 q1 olduğunda, parts[0] = STATES oluyor
                    // o yüzden parts[1] ile başlıyoruz

                    System.out.println("Current States:");

                    // convert the set to a list and sort alphabetically by state name
                    List<State> sortedStates = new ArrayList<>(fsm.getStates());
                    sortedStates.sort(Comparator.comparing(State::getName));

                    for (State s : sortedStates) { // loop through each state already stored in FSM
                        String tag = ""; // tag = initial veya final
                        if (s.equals(fsm.getInitialState())) tag += " [initial]";
                        if (fsm.getFinalStates().contains(s)) tag += " [final]";
                        System.out.println("  " + s.getName() + tag);
                    }
                    break;
                }

                for (int i = 1; i < parts.length; i++) {
                    String name = parts[i];
                    // check if alphanumeric
                    if (!name.matches("[a-zA-Z0-9]+")) {
                        System.out.println("Warning: Invalid state name '" + name + "' (must be alphanumeric)");
                        continue;
                    }

                    State newState = new State(name.toLowerCase());
                    // Check for duplicates
                    if (!fsm.getStates().add(newState)) {
                        System.out.println("Warning: Duplicate state '" + name + "' already declared");
                    } else {
                        // First added state becomes initial state if none is set
                        if (fsm.getInitialState() == null) {
                            fsm.setInitialState(newState);
                        }
                    }
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
