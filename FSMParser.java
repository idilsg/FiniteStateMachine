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
        command = command.trim();
        if (command.isEmpty()) return;

        String[] parts = command.split("\\s+", 2);
        String keyword = parts[0].toUpperCase();
        String arg = (parts.length > 1 ? parts[1].trim() : "");

        switch (keyword) {
            case "SYMBOLS":
                if (arg.isEmpty()) {
                    System.out.println("Current Symbols: " + fsm.describeSymbols());
                    break;
                }

                String[] symbolParts = arg.split("\\s+");
                for (String symStr : symbolParts) {
                    if (symStr.length() != 1) {
                        System.out.println("Warning: Symbol '" + symStr + "' ignored (length must be 1)");
                        continue;
                    }

                    char symbol = symStr.charAt(0);
                    if (!Character.isLetterOrDigit(symbol)) {
                        System.out.println("Warning: Invalid symbol '" + symbol + "' ignored (non-alphanumeric)");
                    } else if (!fsm.addSymbol(Character.toLowerCase(symbol))) {
                        System.out.println("Warning: Duplicate symbol '" + symbol + "'");
                    }
                }
                break;

            case "STATES":
                if (arg.isEmpty()) {
                    System.out.println("Current States:");
                    List<State> list = new ArrayList<>(fsm.getStates());
                    list.sort(Comparator.comparing(State::getName));
                    for (State s : list) {
                        String tag = "";
                        if (s.equals(fsm.getInitialState())) tag += " [initial]";
                        if (fsm.getFinalStates().contains(s)) tag += " [final]";
                        System.out.println("  " + s.getName() + tag);
                    }
                } else {
                    for (String name : arg.split("\\s+")) {
                        if (!name.matches("[a-zA-Z0-9]+")) {
                            System.out.println("Warning: Invalid state name '" + name + "'");
                            continue;
                        }
                        State st = new State(name.toLowerCase());
                        if (!fsm.getStates().add(st)) {
                            System.out.println("Warning: Duplicate state '" + name + "'");
                        } else if (fsm.getInitialState() == null) {
                            fsm.setInitialState(st);
                        }
                    }
                }
                break;

            case "INITIAL-STATE":
                if (parts.length != 2) {
                    System.out.println("Warning: INITIAL-STATE command must be followed by a single valid state name.");
                    break;
                }

                String initName = parts[1];
                if (!initName.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Warning: Invalid state name '" + initName + "' (must be alphanumeric)");
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
                    System.out.println("Warning: FINAL-STATES command requires at least one valid state name.");
                    break;
                }

                for (String name : arg.split("\\s+")) {
                    if (!name.matches("[a-zA-Z0-9]+")) {
                        System.out.println("Warning: Invalid state name '" + name + "' (must be alphanumeric)");
                        continue;
                    }

                    State state = new State(name.toLowerCase());
                    if (!fsm.getStates().contains(state)) {
                        fsm.addState(state);
                        System.out.println("Warning: State '" + name + "' was not declared before. It has been added.");
                    }

                    if (!fsm.getFinalStates().add(state)) {
                        System.out.println("Warning: State '" + name + "' is already a final state.");
                    }
                }
                break;

            case "TRANSITIONS":
                String input = arg;
                if (input.endsWith(";")) {
                    input = input.substring(0, input.length() - 1).trim();
                }
                String[] groups = input.split("\\s*,\\s*");
                for (String grp : groups) {
                    String grpTrim = grp.trim();
                    String[] elems = grpTrim.split("\\s+", 3);
                    if (elems.length != 3) {
                        System.out.println("Error: unexpected comma or semicolon in transition list");
                        continue;
                    }
                    String symStr = elems[0].trim();
                    if (symStr.length() != 1 || !Character.isLetterOrDigit(symStr.charAt(0))) {
                        System.out.println("Error: invalid symbol " + symStr);
                        continue;
                    }
                    char symbol = symStr.charAt(0);
                    String fromName = elems[1].trim();
                    String toName = elems[2].trim();
                    if (!fromName.matches("[a-zA-Z0-9]+")) {
                        System.out.println("Error: invalid state " + fromName);
                        continue;
                    }
                    if (!toName.matches("[a-zA-Z0-9]+")) {
                        System.out.println("Error: invalid state " + toName);
                        continue;
                    }
                    State from = new State(fromName.toLowerCase());
                    State to = new State(toName.toLowerCase());
                    if (!fsm.getStates().contains(from)) {
                        System.out.println("Error: State '" + fromName + "' is not defined.");
                        continue;
                    }
                    if (!fsm.getStates().contains(to)) {
                        System.out.println("Error: State '" + toName + "' is not defined.");
                        continue;
                    }
                    if (!fsm.getSymbols().contains(symbol)) {
                        System.out.println("Error: Symbol '" + symbol + "' is not defined.");
                        continue;
                    }
                    if (fsm.hasTransition(from, symbol)) {
                        State old = fsm.getTransition(from, symbol);
                        if (!old.equals(to)) {
                            System.out.println("Warning: multiple transitions for symbol '" + symbol +
                                    "' and state '" + fromName + "', overriding previous.");
                        }
                    }
                    fsm.addTransition(symbol, from, to);
                }
                break;

            case "EXECUTE":
                System.out.println(fsm.execute(parts[1]));
                break;

            default:
                if (command.equals("PRINT") || command.equals("EXECUTE") || command.equals("LOG") ||
                        command.equals("LOAD") || command.equals("COMPILE") ||
                        command.equals("CLEAR") || command.equals("EXIT")) {
                    break;
                }
                System.out.println("Error: Unknown command -> " + parts[0]);
                System.out.println("Valid commands are: SYMBOLS, STATES, INITIAL-STATE, FINAL-STATES, EXECUTE");
                break;
        }
    }

    public void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                buffer.append(line.trim());
                if (line.trim().endsWith(";")) {
                    processCommand(buffer.toString());
                    buffer.setLength(0);
                }
            }
            System.out.println("File successfully loaded: " + filePath);
        } catch (IOException e) {
            System.out.println("Error while reading file: " + e.getMessage());
        }
    }
}
