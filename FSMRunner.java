// FSM’yi bir giriş dizesi üzerinde çalıştıran sınıf.

public class FSMRunner {
    private FSM fsm;

    public FSMRunner(FSM fsm) {
        this.fsm = fsm;
    }

    public String run(String input) {
        if (input == null || input.isEmpty()) {
            return "Error: Empty input";
        }

        return fsm.execute(input); // FSM içindeki execute metodunu çalıştır
    }

    public void printFSM() {
        System.out.println(fsm.describe());
    }
}
