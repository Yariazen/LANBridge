$pidFile = Join-Path $PSScriptRoot "pidfile.txt"
$serverDir = $PSScriptRoot
$mainClass = "Bridge"
$javaPath = "java"

function Run-Server {
	Write-Host "Starting server..."

	$process = Start-Process $javaPath -ArgumentList "-cp . $mainClass" -WorkingDirectory $serverDir -PassThru
	$pidValue = $process.Id

	Set-Content $pidFile $pidValue
	Write-Host "Server started with PID $pidValue."
}

Run-Server
