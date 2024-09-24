$pidFile = Join-Path $PSScriptRoot "pidfile.txt"
$serverDir = $PSScriptRoot
$startScript = Join-Path $PSScriptRoot "start-server.ps1"
$javacPath = "javac"

$test = Join-Path $PSScriptRoot "test.txt"

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

function Update-Code {
	Write-Host "Pulling latest code from Github..."
	& git pull
}

function Compile-Server {
	& $javacPath *.java
	if ($LASTEXITCODE -eq 0) {
		Write-Host "Compilation successful."
	} else {
		Write-Host "Compilation failed."
		exit 1
	}
}

function Start-Server {
	Write-Host "Starting the server using start-server.ps1..."
	& $startScript
}

Kill-Server
Update-Code
Compile-Server
Start-Server
