@echo off
REM ==========================================================
REM MEDICARE - Windows Launch Script
REM ==========================================================

echo =========================================
echo    MEDICARE HOSPITAL MANAGEMENT SYSTEM
echo =========================================

REM Compile source files
echo [*] Compiling Java classes...
javac -cp ".;lib/*" *.java
if %errorlevel% neq 0 (
    echo [X] Compilation failed. Please ensure JDK 17+ is installed and on PATH.
    pause
    exit /b %errorlevel%
)

REM Run application
echo [*] Starting application...
java -cp ".;lib/*" Main
pause
