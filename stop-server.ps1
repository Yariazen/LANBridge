$pidFile = Join-Path $PSScriptRoot "pidfile.txt"

function Kill-Server {
        if (Test-Path $pidFile) {
                $pidValue = Get-Content $pidFile
                $process = Get-Process -Id $pidValue -ErrorAction SilentlyContinue
                if ($process) {
                        Write-Host "Killing server process with PID $pidValue..."
                        Stop-Process -Id $pidValue -Force
                        Write-Host "Server stopped."
                }
                Remove-Item $pidFile
        }
}
