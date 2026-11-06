@echo off
setlocal
cd /d "%~dp0"
if exist "%ProgramFiles%\Android\Android Studio\jbr\bin\java.exe" (
    set "JAVA_HOME=%ProgramFiles%\Android\Android Studio\jbr"
)
echo Starting Dices 1.1.1...
call gradlew.bat :desktopApp:run --console=plain
if errorlevel 1 (
    echo.
    echo Build or startup failed. See the message above and README.md.
    pause
)
endlocal
