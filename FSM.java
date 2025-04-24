//FSM’nin tüm bileşenlerini (semboller, durumlar, geçişler) içeren sınıf.
import java.io.Serializable;// FSMFileManager için gerekli
import java.util.*;

public class FSM implements Serializable {
    private Set<State> states;  // FSM içindeki tanımlı tüm durumlar q0,q1
    private Set<Character> symbols;  // FSM'nin kabul ettiği alfabetik semboller 0,1,a
    private Map<State, Map<Character, State>> transitions; // Geçişler (Durum -> (Sembol -> Sonraki Durum))
    private State initialState; // Başlangıç durumu
    private Set<State> finalStates; // Kabul durumları

    // implements Serializabledeki amaç :  Nesne dosyaya yazılabilir (save/load yapılabilir).

    public FSM() {
        this.states = new HashSet<>();
        this.symbols = new HashSet<>();
        this.transitions = new HashMap<>();
        this.finalStates = new HashSet<>();
    }

    public void addState(State state) {
        states.add(state);
    } // q0,q1

    public void addSymbol(char symbol) {
        symbols.add(symbol);
    } // a,b,0,1

    public void setInitialState(State state) {
        this.initialState = state;
    }


    public void addFinalState(State state) {
        finalStates.add(state);
    }

    public void addTransition(State from, char symbol, State to) {
        Map<Character, State> map = transitions.get(from);
        if (map == null) {
            map = new HashMap<>();
            transitions.put(from, map);
        }
        map.put(symbol, to);
    }

    public String execute(String input) {
        if (initialState == null) {
            return "Error: Initial state is not defined.";
        }

        State currentState = initialState;
        StringBuilder stateSequence = new StringBuilder(currentState.getName());

        for (char symbol : input.toCharArray()) {
            if (!symbols.contains(symbol)) {
                return "Error: Invalid symbol " + symbol;
            }
            Map<Character, State> stateTransitions = transitions.get(currentState);
            if (stateTransitions == null || !stateTransitions.containsKey(symbol)) {
                return stateSequence.toString() + " NO";
            }
            currentState = stateTransitions.get(symbol);
            stateSequence.append(" ").append(currentState.getName());
        }

        return stateSequence.toString() + (finalStates.contains(currentState) ? " YES" : " NO");
    }

    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append("SYMBOLS: ").append(symbols).append("\n");
        sb.append("STATES: ").append(states).append("\n");
        sb.append("INITIAL: ").append(initialState != null ? initialState : "None").append("\n");
        sb.append("FINAL STATES: ").append(finalStates).append("\n");
        sb.append("TRANSITIONS:\n");

        for (State from : transitions.keySet()) {
            for (Map.Entry<Character, State> entry : transitions.get(from).entrySet()) {
                sb.append("  ").append(from).append(" -- ").append(entry.getKey()).append(" --> ").append(entry.getValue()).append("\n");
            }
        }

        return sb.toString();
    }



}
