@echo off
REM Starts backend and frontend in separate terminal windows for local development.
echo Starting Game Catalog Application...
echo.

REM Set JAVA_HOME
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot

echo Starting Backend Server...
start "Backend Server" cmd /k "cd /d %~dp0backend && mvnw.cmd spring-boot:run"

REM Simple startup delay so backend has time to initialize before frontend starts.
echo Waiting for backend to start...
timeout /t 10 /nobreak

echo Starting Frontend Server...
start "Frontend Server" cmd /k "cd /d %~dp0frontend && npm run dev"

echo.
echo ========================================
echo Application is starting!
echo Backend: http://localhost:8080
echo Frontend: http://localhost:3001
echo ========================================
echo.
echo Press any key to stop all servers...
pause
REM Terminates terminals created by this script.
taskkill /FI "WindowTitle eq Backend Server*" /T /F
taskkill /FI "WindowTitle eq Frontend Server*" /T /F
