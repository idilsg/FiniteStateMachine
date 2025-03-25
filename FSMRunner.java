// FSM’yi bir giriş dizesi üzerinde çalıştıran sınıf.
public class FSMRunner {
    private FSM fsm;

    public FSMRunner(FSM fsm) {
        this.fsm = fsm;
    }

    public String run(String input) {
        return fsm.execute(input); // FSM içindeki execute metodunu çalıştır
    }
}

