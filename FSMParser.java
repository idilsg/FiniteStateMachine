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
        if (command.isEmpty()) return;

        String[] parts = command.split(" ");
        switch (parts[0].toUpperCase()) {

            case "SYMBOLS":
                if (parts.length == 1) {
                    System.out.println("Current Symbols: " + fsm.describeSymbols());
                    break;
                }
                for (int i = 1; i < parts.length; i++) {
                    char symbol = parts[i].charAt(0);
                    if (!Character.isLetterOrDigit(symbol)) {
                        System.out.println("Warning: Invalid symbol '" + symbol + "' ignored");
                    } else if (!fsm.addSymbol(symbol)) {
                        System.out.println("Warning: Duplicate symbol '" + symbol + "'");
                    }
                }
                break;

            case "STATES":
                if (parts.length == 1) {
                    System.out.println("Current States:");
                    List<State> sortedStates = new ArrayList<>(fsm.getStates());
                    sortedStates.sort(Comparator.comparing(State::getName));
                    for (State s : sortedStates) {
                        String tag = "";
                        if (s.equals(fsm.getInitialState())) tag += " [initial]";
                        if (fsm.getFinalStates().contains(s)) tag += " [final]";
                        System.out.println("  " + s.getName() + tag);
                    }
                    break;
                }
                for (int i = 1; i < parts.length; i++) {
                    String name = parts[i];
                    if (!name.matches("[a-zA-Z0-9]+")) {
                        System.out.println("Warning: Invalid state name '" + name + "'");
                        continue;
                    }
                    State newState = new State(name.toLowerCase());
                    if (!fsm.getStates().add(newState)) {
                        System.out.println("Warning: Duplicate state '" + name + "'");
                    } else if (fsm.getInitialState() == null) {
                        fsm.setInitialState(newState);
                    }
                }
                break;

            case "INITIAL-STATE":
                if (parts.length != 2) {
                    System.out.println("Warning: INITIAL-STATE command must be followed by one state name.");
                    break;
                }
                String initName = parts[1];
                if (!initName.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Warning: Invalid state name '" + initName + "'");
                    break;
                }
                State initState = new State(initName.toLowerCase());
                if (!fsm.getStates().contains(initState)) {
                    fsm.addState(initState);
                    System.out.println("Warning: State '" + initName + "' was not declared before. It has been added.");
                }
                fsm.setInitialState(initState);
                break;

            case "FINAL-STATES":
                if (parts.length == 1) {
                    System.out.println("Warning: FINAL-STATES command requires at least one state.");
                    break;
                }
                for (int i = 1; i < parts.length; i++) {
                    String name = parts[i];
                    if (!name.matches("[a-zA-Z0-9]+")) {
                        System.out.println("Warning: Invalid state name '" + name + "'");
                        continue;
                    }
                    State s = new State(name.toLowerCase());
                    if (!fsm.getStates().contains(s)) {
                        fsm.addState(s);
                        System.out.println("Warning: State '" + name + "' was not declared before. It has been added.");
                    }
                    if (!fsm.getFinalStates().add(s)) {
                        System.out.println("Warning: State '" + name + "' is already a final state.");
                    }
                }
                break;

            case "TRANSITIONS":
                if (parts.length == 1) {
                    System.out.println("Warning: No transitions specified.");
                    break;
                }
                String full = command.substring("TRANSITIONS".length()).trim();
                String[] transitions = full.split(",");
                for (String t : transitions) {
                    String[] tParts = t.trim().split(" ");
                    if (tParts.length != 3) {
                        System.out.println("Warning: Invalid transition format '" + t + "'");
                        continue;
                    }
                    char symbol = tParts[0].charAt(0);
                    String from = tParts[1].toLowerCase();
                    String to = tParts[2].toLowerCase();
                    if (!Character.isLetterOrDigit(symbol)) {
                        System.out.println("Warning: Invalid symbol '" + symbol + "'");
                        continue;
                    }
                    State fromState = new State(from);
                    State toState = new State(to);
                    if (!fsm.getStates().contains(fromState)) {
                        fsm.addState(fromState);
                        System.out.println("Warning: State '" + from + "' was not declared before. It has been added.");
                    }
                    if (!fsm.getStates().contains(toState)) {
                        fsm.addState(toState);
                        System.out.println("Warning: State '" + to + "' was not declared before. It has been added.");
                    }
                    fsm.addSymbol(symbol);
                    fsm.addTransition(fromState, symbol, toState);
                }
                break;

            case "PRINT":
                System.out.println(fsm.describe());
                break;

            case "CLEAR":
                fsm = new FSM();
                System.out.println("FSM cleared.");
                break;

            case "LOAD":
                if (parts.length != 2) {
                    System.out.println("Error: LOAD command requires a filename.");
                    break;
                }
                String filename = parts[1];
                if (filename.endsWith(".bin")) {
                    FSM loadedFSM = FSMFileManager.loadFSM(filename);
                    if (loadedFSM != null) {
                        this.fsm = loadedFSM;
                        System.out.println("Binary FSM loaded from: " + filename);
                    }
                } else {
                    loadFromFile(filename);
                }
                break;

            case "EXECUTE":
                if (parts.length != 2) {
                    System.out.println("Error: EXECUTE requires an input string.");
                    break;
                }
                System.out.println(fsm.execute(parts[1]));
                break;

            default:
                System.out.println("Error: Unknown command");
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
