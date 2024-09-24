import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class Handler implements URLHandler {
    String currentDir = Paths.get("").toAbsolutePath().toString();
    
    public String handleRequest(URI url) {
        if (url.getPath().equals("/start-server")) {
            String scriptPath = currentDir + "\\start-server.ps1";
            return handleScript(scriptPath);
        } if else (url.getPath().equals("/update-server")) {
            String scriptPath = currentDir + "\\update-server.ps1";
            return handleScript(scriptPath);
        } else {
            return "404 Missing";
        }
    }

    public String handleScript(String scriptPath) {
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
    }
}

class Bridge {
    public static void main(String[] args) throws IOException {
        int port = 8080;

        Server.start(port, new Handler());
    }
}