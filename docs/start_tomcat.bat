@echo off
REM ============================================================
REM  START TOMCAT - Clearance Management System
REM  Double-click this file to start the web server
REM ============================================================
set JAVA_HOME=C:\Program Files\Java\jdk-27
set CATALINA_HOME=C:\xampp\tomcat
set CATALINA_BASE=C:\xampp\tomcat
set CATALINA_OPTS=-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.io.tmpdir=C:/xampp/tomcat/temp
cd /d C:\xampp\tomcat
echo Starting Tomcat... waiting 12 seconds
start "Tomcat - ClearanceSystem" "C:\Program Files\Java\jdk-27\bin\java.exe" -cp "C:\xampp\tomcat\bin\bootstrap.jar;C:\xampp\tomcat\bin\tomcat-juli.jar" org.apache.catalina.startup.Bootstrap
timeout /t 12 >nul
echo.
echo DONE! Open this in Chrome:
echo   http://localhost:8080/ClearanceSystem
echo.
pause
