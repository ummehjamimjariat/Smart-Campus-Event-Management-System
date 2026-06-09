@echo off
echo =============================================
echo  Smart Campus Event Management System
echo  Build Script - Windows
echo =============================================
echo.

javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: javac not found. Install JDK from https://adoptium.net/
    pause & exit /b 1
)

if exist out rmdir /s /q out
mkdir out

echo Compiling...
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt

if %errorlevel% neq 0 (
    echo COMPILATION FAILED. See errors above.
    pause & exit /b 1
)

echo.
echo SUCCESS! Launching app...
echo Default logins:
echo   admin@campus.edu / admin123 (ADMIN)
echo   events@campus.edu / org123  (ORGANIZER)
echo.
java -cp out com.smartcampus.Main
pause
