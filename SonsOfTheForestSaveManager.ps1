# Define file paths
$installerPath = ".\installers\jdk-20_windows-x64_bin.exe"
$jarFilePath = ".\lib\SonsOfTheForestSaveManager.jar"

# Check if Java is installed
$javaInstalled = Test-Path "HKLM:\SOFTWARE\JavaSoft\JDK"

# Install Java if not installed
if (-not $javaInstalled) {
    Write-Host "Java not found. Installing JDK 20..."
    Start-Process -FilePath $installerPath -ArgumentList "/s" -Wait
    Write-Host "Java installed successfully."
} else {
    Write-Host "Java is already installed."
}

# Set JAVA_HOME if it's not set
if (-not (Get-Item Env:JAVA_HOME)) {
    $jdkPath = (Get-ItemProperty -Path 'HKLM:\SOFTWARE\JavaSoft\JDK' -Name CurrentVersion).CurrentVersion
    $javaHome = (Get-ItemProperty -Path "HKLM:\SOFTWARE\JavaSoft\JDK\$jdkPath").JavaHome
    [System.Environment]::SetEnvironmentVariable('JAVA_HOME', $javaHome, 'Machine')
    $env:Path += ";$javaHome\bin"
}

# Run the jar file
Write-Host "Running Sons Of The Forest Save Manager..."
Start-Process -FilePath "java" -ArgumentList "-jar", $jarFilePath
