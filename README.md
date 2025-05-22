# FSM Designer – SE116 Java Project (Spring 2025)

This project is a command-line Finite State Machine (FSM) designer written in Java as part of the SE116 course at Izmir University of Economics. It enables users to design and simulate FSMs by providing commands to define symbols, states, transitions, and execute input strings to verify behavior.


## Team Members
- İdil Sanem Gürsoy
- İrem Özkan
- Elif Karslı  
- Doruk Yaşar

**Advisor:** Dr. Öğr. Üyesi Erdem Okur


## Project Summary

The application supports:
- Defining symbols and states
- Declaring initial and final states
- Adding transitions with validation
- Saving and loading FSMs from text and binary files
- Executing input strings and returning YES/NO based on FSM logic
- Logging user commands and outputs

The application fully satisfies all 15 functional requirements as outlined in the official project description and has been thoroughly tested.


## Project Structure

| Class            | Description |
|------------------|-------------|
| `FSM`            | Core logic for FSM, including states, symbols, transitions, and execution |
| `State`          | Serializable class representing an FSM state |
| `FSMApp`         | Entry point for command-line interaction |
| `FSMParser`      | Parses user commands and updates FSM accordingly |
| `FSMRunner`      | Executes input strings using FSM logic |
| `FSMLogger`      | Manages logging of commands and system output |
| `FSMFileManager` | Handles saving/loading FSMs as text or binary files |


## Sample Session

```
? SYMBOLS a b;  
? STATES q0 q1;  
? INITIAL-STATE q0;  
? FINAL-STATES q1;  
? TRANSITIONS a q0 q1, b q1 q0;  
? EXECUTE ab;  
q0 q1 q0 YES
```

## Testing

Tested manually using both direct command input and external text files. Execution output matches expected FSM behavior. Full command examples and results are included in the project report.


## Git Usage

This project was collaboratively developed using a private GitHub repository with weekly commits by all team members as required.


## Conclusion

Overall, this project enhanced our skills in object-oriented programming, team collaboration, and applying theoretical concepts of finite state machines in practice. It served as a valuable hands-on experience in software development workflows.
