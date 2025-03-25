//Kullanıcı girişlerini ve FSM çıktısını bir dosyaya kaydeden sınıf.
import java.io.FileWriter;
import java.io.IOException;

public class FSMLogger {
    private FileWriter logFile;
    private boolean loggingEnabled = false;

    public void startLogging(String filePath) {
        try {
            logFile = new FileWriter(filePath, false);
            loggingEnabled = true;
            System.out.println("LOGGING STARTED: " + filePath);
        } catch (IOException e) {
            System.out.println("Log dosyası açılamadı: " + e.getMessage());
        }
    }

    public void stopLogging() {
        if (loggingEnabled) {
            try {
                logFile.close();
                loggingEnabled = false;
                System.out.println("STOPPED LOGGING");
            } catch (IOException e) {
                System.out.println("Log dosyası kapatılamadı: " + e.getMessage());
            }
        } else {
            System.out.println("LOGGING was not enabled");
        }
    }

    public void log(String message) {
        if (loggingEnabled) {
            try {
                logFile.write(message + "\n");
                logFile.flush();
            } catch (IOException e) {
                System.out.println("Log dosyasına yazılamadı: " + e.getMessage());
            }
        }
    }
}
