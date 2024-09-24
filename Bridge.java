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
        } else if (url.getPath().equals("/update-server")) {
            String scriptPath = currentDir + "\\update-server.ps1";
            return handleScript(scriptPath);
        } else {
            return "404 Missing";
        }
    }

    public String handleScript(String scriptPath) {
        try {
            String command = "powershell.exe \"" + scriptPath + "\"";
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();
            return "";
        } catch (IOException e) {
            return "Encountered unexpected error.";
        }
    }
}

class Bridge {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        Server.start(port, new Handler());
    }
}