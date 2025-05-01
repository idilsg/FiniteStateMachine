import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FSMApp {
    public static final String VERSION = "1.0";

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // başlangıç mesajı
        System.out.println("FSM DESIGNER " + VERSION + " - " + now.format(formatter));

        Scanner scanner = new Scanner(System.in);
        FSM fsm = new FSM();
        FSMParser parser = new FSMParser(fsm);
        FSMRunner runner = new FSMRunner(fsm);
        FSMLogger logger = new FSMLogger();

        // if started with an argument load the file
        if (args.length > 0) {
            String inputFile = args[0];
            if (inputFile.endsWith(".bin")) {
                FSM loaded = FSMFileManager.loadFSM(inputFile);
                if (loaded != null) {
                    fsm = loaded;
                    parser = new FSMParser(fsm);
                    runner = new FSMRunner(fsm);
                    System.out.println("FSM loaded from binary file: " + inputFile);
                }
            } else {
                parser.loadFromFile(inputFile);
            }
        }

        while (true) {
            System.out.print("? ");
            String input = scanner.nextLine().trim();

            // Boş input kontrolü
            if (input.isEmpty()) {
                continue;
            }

            // Noktalı virgül sonrası temizle
            input = input.split(";")[0].trim();

            try {
                if (input.equalsIgnoreCase("EXIT")) {
                    System.out.println("TERMINATED BY USER");
                    logger.stopLogging();
                    break;
                }

                if (input.equalsIgnoreCase("HELP")) {
                    System.out.println("Available commands:");
                    System.out.println("  SYMBOLS [symbol1 symbol2 ...]");
                    System.out.println("  STATES [state1 state2 ...]");
                    System.out.println("  INITIAL-STATE [state]");
                    System.out.println("  FINAL-STATES [state1 state2 ...]");
                    System.out.println("  TRANSITIONS [symbol current_state next_state, ...]");
                    System.out.println("  EXECUTE [input_string]");
                    System.out.println("  PRINT");
                    System.out.println("  LOG [filename]");
                    System.out.println("  LOAD [filename]");
                    System.out.println("  COMPILE [filename]");
                    System.out.println("  CLEAR");
                    System.out.println("  EXIT");
                    continue;
                }

                if (input.startsWith("LOG ")) {
                    String[] parts = input.split(" ");
                    if (parts.length == 2) {
                        logger.startLogging(parts[1]);
                    } else {
                        logger.stopLogging();
                    }
                    continue;
                }

                if (input.startsWith("EXECUTE ")) {
                    String[] parts = input.split(" ");
                    if (parts.length == 2) {
                        String result = runner.run(parts[1]);
                        System.out.println(result);
                        logger.log(result);
                    } else {
                        System.out.println("Error: Invalid EXECUTE command. Usage: EXECUTE input_string");
                    }
                    continue;
                }

                if (input.startsWith("COMPILE ")) {
                    String[] parts = input.split(" ");
                    if (parts.length == 2) {
                        FSMFileManager.compileFSM(fsm, parts[1]);
                    } else {
                        System.out.println("Error: Invalid COMPILE command. Usage: COMPILE filename.bin");
                    }
                    continue;
                }

                // Diğer her şey FSMParser'a gönderiliyor
                parser.processCommand(input);
                logger.log(input);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
