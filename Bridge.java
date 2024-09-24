import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class Handler implements URLHandler {
    String currentDir;
    String logFilePath;
    
    public Handler(String currentDir, String logFilePath) {
        this.currentDir = currentDir;
        this.logFilePath = logFilePath;
    }

    public String handleRequest(URI url) {
        if (url.getPath().equals("/start-server")) {
            String scriptPath = currentDir + "\\start-server.ps1";

            String response = handleScript(scriptPath);
            logToFile(response);
            return response;
        } else if (url.getPath().equals("/update-server")) {
            String scriptPath = currentDir + "\\update-server.ps1";
            
            String response = handleScript(scriptPath);
            logToFile(response);
            return response;
        } else {
            return "404 Missing";
        }
    }

    public String handleScript(String scriptPath) {
        try {
            String command = "powershell.exe \"" + scriptPath + "\"";
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();

            StringBuilder str = new StringBuilder();
            String line;

            str.append("Standard Output:\n");
            BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
            while ((line = stdout.readLine()) != null) {
                str.append(line).append("\n");
            }
            stdout.close();

            str.append("Standard Error:\n");
            BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
            while ((line = stderr.readLine()) != null) {
                str.append(line).append("\n");
            }
            stderr.close();

            str.append("Done\n");
            return str.toString();
        } catch (IOException e) {
            return "Encountered unexpected error.";
        }
    }

    private void logToFile(String message) {
        try (FileWriter writer = new FileWriter(logFilePath, true)) { // Append mode
            writer.write(message + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}

class Bridge {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        String currentDir = Paths.get("").toAbsolutePath().toString();
        String logFilePath = currentDir + "\\log.log";
        String previousLogFilePath = currentDir + "\\previous.log";

        File currentLogFile = new File(logFilePath);
        if (currentLogFile.exists()) {
            currentLogFile.renameTo(new File(previousLogFilePath));
        }

        currentLogFile.createNewFile();

        Server.start(port, new Handler(currentDir, logFilePath));
    }
}