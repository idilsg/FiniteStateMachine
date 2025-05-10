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
            if (inputFile.endsWith(".bin")|| inputFile.endsWith(".fs")) {
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
            String rawInput = scanner.nextLine().trim();

            // Normalize whitespace (convert tabs/multiple spaces to single space)
            rawInput = FSM.normalizeWhitespace(rawInput);

            // Only process up to first semicolon
            int semicolonIndex = rawInput.indexOf(';');
            if (semicolonIndex == -1) {
                System.out.println("Error: Command must end with ';'");
                continue;
            }

            String cleanCommand = rawInput.substring(0, semicolonIndex).trim();
            if (cleanCommand.isEmpty()) {
                continue;
            }
            //input = input.split(";")[0].trim();


            try {
                if (cleanCommand.equalsIgnoreCase("EXIT")) {
                    System.out.println("TERMINATED BY USER");
                    logger.stopLogging();
                    break;
                }

                if (cleanCommand.equalsIgnoreCase("HELP")) {
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

                if (cleanCommand.startsWith("LOG ")) {
                    String[] parts = cleanCommand.split(" ");
                    if (parts.length == 2) {
                        logger.startLogging(parts[1]);
                    } else {
                        logger.stopLogging();
                    }
                    continue;
                }

                if (cleanCommand.startsWith("EXECUTE ")) {
                    String[] parts = cleanCommand.split(" ");
                    if (parts.length == 2) {
                        String result = runner.run(parts[1]);
                        System.out.println(result);
                        logger.log(result);
                    } else {
                        System.out.println("Error: Invalid EXECUTE command. Usage: EXECUTE input_string");
                    }
                    continue;
                }

                if (cleanCommand.startsWith("COMPILE ")) {
                    String[] parts = cleanCommand.split(" ");
                    if (parts.length == 2) {
                        FSMFileManager.compileFSM(fsm, parts[1]);
                    } else {
                        System.out.println("Error: Invalid COMPILE command.");
                    }
                    continue;
                }

                // Diğer her şey FSMParser'a gönderiliyor
                parser.processCommand(cleanCommand);
                logger.log(cleanCommand);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
