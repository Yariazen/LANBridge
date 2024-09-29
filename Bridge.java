import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.io.File;

class Handler implements URLHandler {
    String currentDir = Paths.get("").toAbsolutePath().toString();
    Hashtable<String, Process> runningServers = new Hashtable<>();

    public String handleRequest(URI url) {
        if (url.getPath().equals("/start-minecraft")) {
            String query = url.getQuery(); 
            if (query != null && query.startsWith("name=")) {
                String serverName = query.split("=")[1]; 
                return startServerWithConfig(serverName);
            } else {
                return "400 Bad Request: Missing or invalid 'name' query parameter.";
            }
        } else if (url.getPath().equals("/stop-minecraft")) {
            String query = url.getQuery(); 
            if (query != null && query.startsWith("name=")) {
                String serverName = query.split("=")[1];
                return stopServer(serverName);
            } else {
                return "400 Bad Request: Missing or invalid 'name' query parameter.\n";
            }
              
        } else {
            return "404 Missing.\n";
        }
    }

    public String startServer(String serverName) {
        if (serverName.equalsIgnoreCase("vanilla")) {
            return runBatFile(
                "server.bat",
                "C:\\Users\\Ash\\Server\\Vanilla",
                serverName
            )
        } else if (serverName.equalsIgnoreCase("ftb inferno")) {
            return runBatFile(
                "run.bat",
                "C:\\Users\\Ash\\Server\\FTB Inferno\\.minecraft",
                serverName
            )
        } else {
            return "400 Bad Request: Unsupported server name.\n";
        }
    }

    public String runBatFile(String batFile, String batFileDir, String serverName) {
            String batFilePath = batFileDir + "\\" + batFile;
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", batFilePath);
            processBuilder.directory(new File(batFileDir));

            Process batProcess = processBuilder.start();
            runningServers.put(serverName, batProcess);

            return "Server started with PID: " + batProcess.pid() + "\n";
        } catch (IOException e) {
            return "Failed to run .bat file: " + e.getMessage() + "\n";
        }
    }

    public String stopServer(String serverName) {
        Process serverProcess = runningServers.get(serverName);
        if (serverProcess != null && serverProcess.isAlive()) {
            serverProcess.destroy();
            runningServers.remove(serverName);
            return "Server '" + serverName + "' stopped.\n";
        } else {
            return "Server '" + serverName + "' is not running.\n";
        }
    }
}

class Bridge {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        Server.start(port, new Handler());
    }
}