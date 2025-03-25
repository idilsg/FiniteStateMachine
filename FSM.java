//FSM’nin tüm bileşenlerini (semboller, durumlar, geçişler) içeren sınıf.
import java.io.Serializable;
import java.util.*;

public class FSM implements Serializable {
    private Set<State> states;  // FSM içindeki durumlar
    private Set<Character> symbols;  // FSM'nin kabul ettiği semboller
    private Map<State, Map<Character, State>> transitions; // Geçişler (Durum -> (Sembol -> Sonraki Durum))
    private State initialState; // Başlangıç durumu
    private Set<State> finalStates; // Kabul durumları

    public FSM() {
        this.states = new HashSet<>();
        this.symbols = new HashSet<>();
        this.transitions = new HashMap<>();
        this.finalStates = new HashSet<>();
    }

    public void addState(State state) {
        states.add(state);
    }

    public void addSymbol(char symbol) {
        symbols.add(symbol);
    }

    public void setInitialState(State state) {
        this.initialState = state;
    }

    public void addFinalState(State state) {
        finalStates.add(state);
    }

    public void addTransition(State from, char symbol, State to) {
        transitions.putIfAbsent(from, new HashMap<>());
        transitions.get(from).put(symbol, to);
    }

    public String execute(String input) {
        State currentState = initialState;
        StringBuilder stateSequence = new StringBuilder(currentState.getName());

        for (char symbol : input.toCharArray()) {
            if (!symbols.contains(symbol)) {
                return "Hata: Geçersiz sembol " + symbol;
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
}
