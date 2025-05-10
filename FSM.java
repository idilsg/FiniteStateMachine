// FSM’nin tüm bileşenlerini (semboller, durumlar, geçişler) içeren sınıf

import java.io.Serializable; // FSMFileManager için gerekli
import java.util.*;


// implements Serializabledeki amaç :  Nesne dosyaya yazılabilir (save/load yapılabilir)
public class FSM implements Serializable {
    private Set<State> states;                      // Durumlar kümesi
    private Set<Character> symbols;                 // Semboller kümesi
    private Map<State, Map<Character, State>> transitions;  // Geçişler
    private State initialState;                     // Başlangıç durumu
    private Set<State> finalStates;                 // Kabul durumları

    public FSM() {
        this.states = new HashSet<>();
        this.symbols = new HashSet<>();
        this.transitions = new HashMap<>();
        this.finalStates = new HashSet<>();
    }

    // New helper methods for duplicate-checking in parser
    public boolean hasTransition(State from, char symbol) {
        Map<Character, State> map = transitions.get(from);
        return map != null && map.containsKey(symbol);
    }

    public State getTransition(State from, char symbol) {
        Map<Character, State> map = transitions.get(from);
        return (map != null) ? map.get(symbol) : null;
    }

    // ----- SYMBOL işlemleri -----
    public boolean addSymbol(char symbol) { // a,b,0,1
        if (!Character.isLetterOrDigit(symbol)) {
            return false;
        }
        return symbols.add(Character.toLowerCase(symbol));  // küçük harf olarak ekleniyor
    }
    // transitionda varlık kontrolü için gerekti
    public void addTransition(char symbol, State from, State to) {
        if (!symbols.contains(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is not defined in FSM.");
        }
        if (!states.contains(from) || !states.contains(to)) {
            throw new IllegalArgumentException("Both source and destination states must be defined.");
        }
        transitions.putIfAbsent(from, new HashMap<>());
        transitions.get(from).put(symbol, to);
    }


    public Set<Character> getSymbols() {
        return new HashSet<>(symbols); // dışarıya kopya ver
    }

    public String describeSymbols() {
        List<Character> sortedSymbols = new ArrayList<>(symbols);
        sortedSymbols.sort(Comparator.naturalOrder());
        StringBuilder sb = new StringBuilder();
        for (char c : sortedSymbols) {
            sb.append(c).append(" ");
        }
        return sb.toString().trim(); // remove trailing space
    }

    // ----- STATE işlemleri -----
    public void addState(State state) { // q0,q1
        states.add(state);
    }

    public Set<State> getStates() {
        return states;
    }

    public void setInitialState(State state) {
        this.initialState = state;
    }

    public State getInitialState() {
        return initialState;
    }

    public void addFinalState(State state) {
        finalStates.add(state);
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }


    // ----- FSM Çalıştırma -----
    public String execute(String input) {
        if (initialState == null) return "Error: Initial state is not defined.";
        State cur = initialState;
        StringBuilder seq = new StringBuilder(cur.getName());
        for (char c : input.toCharArray()) {
            if (!symbols.contains(c))         return "Error: Invalid symbol " + c;
            if (!hasTransition(cur, c))       return seq + " NO";
            cur = getTransition(cur, c);
            seq.append(" ").append(cur.getName());
        }
        return finalStates.contains(cur) ? seq + " YES" : seq + " NO";

    }

    public static String normalizeWhitespace(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }
    // ----- Yazdırılabilir Açıklama -----
    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append("SYMBOLS: ").append(symbols).append("\n");
        sb.append("STATES: ").append(states).append("\n");
        sb.append("INITIAL: ").append(initialState != null ? initialState : "None").append("\n");
        sb.append("FINAL STATES: ").append(finalStates).append("\n");
        sb.append("TRANSITIONS:\n");

        for (State from : transitions.keySet()) {
            for (Map.Entry<Character, State> entry : transitions.get(from).entrySet()) {
                sb.append("  ").append(from).append(" -- ").append(entry.getKey())
                        .append(" --> ").append(entry.getValue()).append("\n");
            }
        }



        return sb.toString();
    }


}
