$serverDir = $PSScriptRoot
$startScript = Join-Path $PSScriptRoot "start-server.ps1"
$stopScript = Join-Path $PSScriptRoot "stop-server.ps1"
$javacPath = "javac"

function Stop-Server {
	Write-Host "Stopping the server using stop-server.ps1..."
	& $stopScript
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

Stop-Server
Update-Code
Compile-Server
Start-Server
