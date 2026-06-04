@echo off
REM Stops frontend and backend windows/processes started for local development.
title Stop Game Catalog
color 0C

echo ========================================
echo   STOPPING GAME CATALOG APPLICATION
echo ========================================
echo.

echo Closing Frontend...
taskkill /FI "WindowTitle eq Game Catalog Frontend*" /T /F >nul 2>&1

echo Closing Backend...
taskkill /FI "WindowTitle eq Game Catalog Backend*" /T /F >nul 2>&1

REM Also kill by process name as backup
taskkill /F /IM node.exe >nul 2>&1
for /f "tokens=5" %%a in ('netstat -aon ^| find ":8080" ^| find "LISTENING"') do taskkill /F /PID %%a >nul 2>&1

echo.
echo All services stopped!
echo.
pause
