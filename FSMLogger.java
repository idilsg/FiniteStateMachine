//
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FSMLogger {
    private BufferedWriter logFile;
    private boolean loggingEnabled = false;

    public void startLogging(String filePath) {
        try {
            if (logFile != null) { //close the open ones
                logFile.close();
            }
            logFile = new BufferedWriter(new FileWriter(filePath, false));
            loggingEnabled = true;
            System.out.println("LOGGING STARTED: " + filePath);
        } catch (IOException e) {
            reportError("log file could not be opened: " + e.getMessage());
            loggingEnabled = false;
        }
    }

    public void stopLogging() {
        if (loggingEnabled) {
            try {
                logFile.close();
                loggingEnabled = false;
                System.out.println("STOPPED LOGGING");
            } catch (IOException e) {
                reportError("Log file couln't closed: " + e.getMessage());
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
                reportError("Log file coudn't writen: " + e.getMessage());
            }
        }
    }private void reportError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }
}
