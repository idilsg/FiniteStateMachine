//main classı
import java.util.Scanner;

public class FSMApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FSM fsm = new FSM();
        FSMParser parser = new FSMParser(fsm);
        FSMRunner runner = new FSMRunner(fsm);
        FSMLogger logger = new FSMLogger();

        System.out.println("FSM DESIGNER 1.0 - Komutları giriniz ('EXIT;' ile çıkabilirsiniz)");

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
                parser.processCommand(input);
                logger.log(input);
            } catch (Exception e) {
                System.out.println("Hata: " + e.getMessage());
            }
        }

        scanner.close();
    }
}

