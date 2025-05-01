import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FSMLogger {
    private BufferedWriter logFile;
    private boolean loggingEnabled = false;

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void startLogging(String filePath) {
        try {
            if (logFile != null) { //close the open ones
                logFile.close();
            }
            logFile = new BufferedWriter(new FileWriter(filePath, false));
            loggingEnabled = true;
            System.out.println("LOGGING STARTED: " + filePath);
        } catch (IOException e) {
            reportError("Log file could not be opened: " + e.getMessage());
            loggingEnabled = false;
        }
    }

    public void stopLogging() {
        if (loggingEnabled) {
            if (logFile != null) {
                try {
                    logFile.close();
                    System.out.println("STOPPED LOGGING");
                } catch (IOException e) {
                    reportError("Log file couldn't be closed: " + e.getMessage());
                } finally {
                    loggingEnabled = false;
                    logFile = null;
                }
            } else {
                System.out.println("No log file to close.");
                loggingEnabled = false;
            }
        } else {
            System.out.println("LOGGING was not enabled");
        }
    }

    public void log(String message) {
        if (loggingEnabled) {
            try {
                logFile.write(message + "\n");
                logFile.newLine();
                logFile.flush();
            } catch (IOException e) {
                reportError("Log file couldn't written: " + e.getMessage());
            }
        }
    }

    private void reportError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }
}

// read log yapılırsa eklemesi
//public void readLog() {
//        if (logFilePath == null) {
//            System.out.println("No log file available to read.");
//            return;
//        }
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
//            String line;
//            System.out.println("=== Log File Contents ===");
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch (IOException e) {
//            reportError("Could not read log file: " + e.getMessage());
//        }
//    }