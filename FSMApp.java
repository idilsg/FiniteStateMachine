//main classı
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FSMApp {
    public static void main(String[] args) {

        String versionNo = "1.0"; // You can change this to match GitHub version if needed
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (args.length == 0) {
            System.out.println("FSM DESIGNER " + versionNo + " - " + now.format(formatter));
        }

        Scanner scanner = new Scanner(System.in);
        FSM fsm = new FSM();
        FSMParser parser = new FSMParser(fsm);
        FSMRunner runner = new FSMRunner(fsm);
        FSMLogger logger = new FSMLogger();

        while (true) {
            System.out.print("? ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("EXIT;")) {
                System.out.println("TERMINATED BY USER");
                logger.stopLogging(); // Loglama kapat
                break;
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
                }
                continue;
            }

            try {
                // remove anything after the semicolon (command terminator or comment start)
                String cleaned = input.split(";")[0].trim();

                // only process if something is left after trimming
                if (!cleaned.isEmpty()) {
                    parser.processCommand(cleaned);
                }

                logger.log(input);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}

