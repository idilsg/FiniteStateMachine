//Kullanıcının girdiği komutları ayrıştıran ve yorumlayan sınıf.
import java.io.*;
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
                        System.out.println("Warning: Invalid symbol '" + symbol + "' ignored");
                    }
                    // try to add the symbol in lowercase (case-insensitive check)
                    else if (!fsm.addSymbol(symbol)) {
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

                    // loop through each state already stored in FSM
                    for (State s : sortedStates) {
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
                    }
                    // First added state becomes initial state if none is set
                    else if (fsm.getInitialState() == null) {
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

                // check if name is alphanumeric
                if (!initName.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Warning: Invalid state name '" + initName + "' (must be alphanumeric)");
                    break;
                }
                State initState = new State(initName.toLowerCase());

                // if the state does not exist yet, add it with a warning
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

                    // check if name is alphanumeric
                    if (!name.matches("[a-zA-Z0-9]+")) {
                        System.out.println("Warning: Invalid state name '" + name + "' (must be alphanumeric)");
                        continue;
                    }
                    State s = new State(name.toLowerCase());

                    // add to FSM if not declared
                    if (!fsm.getStates().contains(s)) {
                        fsm.addState(s);
                        System.out.println("Warning: State '" + name + "' was not declared before. It has been added.");
                    }

                    // warn if already a final state
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

                // parse transitions list from the rest of the line
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

                    // validate symbol
                    if (!Character.isLetterOrDigit(symbol)) {
                        System.out.println("Warning: Invalid symbol '" + symbol + "'");
                        continue;
                    }
                    State fromState = new State(from);
                    State toState = new State(to);

                    // add undeclared from-state
                    if (!fsm.getStates().contains(fromState)) {
                        fsm.addState(fromState);
                        System.out.println("Warning: State '" + from + "' was not declared before. It has been added.");
                    }

                    // add undeclared to-state
                    if (!fsm.getStates().contains(toState)) {
                        fsm.addState(toState);
                        System.out.println("Warning: State '" + to + "' was not declared before. It has been added.");
                    }

                    // add transition components
                    fsm.addSymbol(symbol);
                    fsm.addTransition(fromState, symbol, toState);
                }
                break;

            case "PRINT":
                if (parts.length == 1) {
                    System.out.println(fsm.describe());
                } else if (parts.length == 2) {
                    String outFile = parts[1];
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
                        writer.write(fsm.describe());
                        writer.flush();
                        System.out.println("FSM written to " + outFile);
                    } catch (IOException e) {
                        System.out.println("Error: Could not write to file " + outFile + ": " + e.getMessage());
                    }
                } else {
                    System.out.println("Error: Invalid PRINT command. Usage: PRINT [optional_filename]");
                }
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

                // load based on file type
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

                // run FSM with given input string
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
