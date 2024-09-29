import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;

class Handler implements URLHandler {
    String currentDir = Paths.get("").toAbsolutePath().toString();
    Hashtable<String, Process> runningServers = new Hashtable<>();

    public String handleRequest(URI url) {
        if (url.getPath().equals("/start-server")) {
            String scriptPath = currentDir + "\\start-server.ps1";
            return handleScript(scriptPath);
        } else if (url.getPath().equals("/update-server")) {
            String scriptPath = currentDir + "\\update-server.ps1";
            return handleScript(scriptPath);
        } else if (url.getPath().equals("/stop-server")) {
            String scriptPath = currentDir + "\\stop-server.ps1";
            return handleScript(scriptPath);
        } else if (url.getPath().equals("/start-minecraft")) {
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

    public String handleScript(String scriptPath) {
        try {
            String command = "powershell.exe \"" + scriptPath + "\"";
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();
            return scriptPath + "\n";
        } catch (IOException e) {
            return "Encountered unexpected error.\n";
        }
    }

    public String startServerWithConfig(String serverName) {
        if (serverName.equalsIgnoreCase("vanilla")) {
            return runJarFile(
                serverName,
                "C:\\Users\\Ash\\Server\\Vanilla\\server.jar",
                "nogui", "-Xms1G", "-Xmx2G"
            );
        } else {
            return "400 Bad Request: Unsupported server name.\n";
        }
    }

    public String runJarFile(String serverName, String jarPath, String... args) {
        try {
            StringBuilder command = new StringBuilder("java -jar");
            for (String arg : args) {
                command.append(" ").append(arg); 
            }
            command.append("\"" + jarPath + "\" ");
            command.append("nogui");
            return command.toString() + "\n";
            //Process jarProcess = Runtime.getRuntime().exec(command.toString());
            //runningServers.put(serverName, jarProcess);
            //return "";
        } catch (Exception e) {
            return "Encountered unexpected error.\n";
        }
    }

    public String stopServer(String serverName) {
        Process serverProcess = runningServers.get(serverName);
        if (serverProcess != null && serverProcess.isAlive()) {
            serverProcess.destroy();
            runningServers.remove(serverName);
            return "Server '" + serverName + "' stopped.";
        } else {
            return "Server '" + serverName + "' is not running.";
        }
    }
}

class Bridge {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        Server.start(port, new Handler());
    }
}