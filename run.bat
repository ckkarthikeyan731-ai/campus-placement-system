@echo off
REM ============================================================
REM  Campus Placement System — Windows compile & run script
REM  Karthikeyan C K | 25IT347 | St. Joseph's College of Engg
REM ============================================================

setlocal

set "BASE=%~dp0"
set "OUT_DIR=%BASE%out"
set "LIB=%BASE%lib\mysql-connector-j-8.3.0.jar"
set "SRC=%BASE%src\CampusPlacementSystem.java"
set MAIN=CampusPlacementSystem

echo.
echo  =====================================================
echo   Campus Placement Management System
echo   Karthikeyan C K ^| 25IT347
echo   St. Joseph's College of Engineering
echo  =====================================================
echo.

REM ── Create out dir if missing
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

if not exist "%LIB%" (
    echo [ERROR] MySQL Connector/J was not found:
    echo         %LIB%
    echo Add mysql-connector-j-8.3.0.jar to the lib folder.
    pause
    exit /b 1
)

REM ── Compile
echo [1/2] Compiling...
javac -encoding UTF-8 -cp "%LIB%" -d "%OUT_DIR%" "%SRC%"
if errorlevel 1 (
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)
echo        Compilation OK.

REM ── Run
echo [2/2] Launching application...
echo.
java -cp "%OUT_DIR%;%LIB%" %MAIN%

endlocal
