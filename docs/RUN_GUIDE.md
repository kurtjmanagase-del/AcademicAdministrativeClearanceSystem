# HOW TO RUN THE SYSTEM — STEP BY STEP

## What you need (all already installed)
- XAMPP (has MariaDB + Tomcat 8.5)
- Java JDK 27 (already installed)
- The project folder:
  `C:\Users\trnal\OneDrive\Documents\NetBeansProjects\AcademicAdministrativeClearanceManagementSystem`

---

## PART 1 — Start the Database (MariaDB)

1. Open **XAMPP Control Panel** (search "XAMPP" in Windows).
2. Click **Start** beside **MySQL**. Wait until the green light.
   - (It should already be running — the green "MySQL" row.)
3. Check: open browser → `http://localhost/phpmyadmin`
   - You should see `academic_clearance_db` in the left list.

---

## PART 2 — Start the Web Server (Tomcat)

> ⚠️ On THIS computer, Tomcat can't be started with the XAMPP "Start" button
> (it fails with a Java error). Use the ready-made script below.

### Option A — One-click script (recommended, use this every time)

Create a file `start_tomcat.bat` on your Desktop or in the project folder:

```bat
@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-27
set CATALINA_HOME=C:\xampp\tomcat
set CATALINA_BASE=C:\xampp\tomcat
set CATALINA_OPTS=-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.io.tmpdir=C:/xampp/tomcat/temp
cd /d C:\xampp\tomcat
start "Tomcat" "C:\Program Files\Java\jdk-27\bin\java.exe" -cp "C:\xampp\tomcat\bin\bootstrap.jar;C:\xampp\tomcat\bin\tomcat-juli.jar" org.apache.catalina.startup.Bootstrap
timeout /t 3 >nul
echo Tomcat starting... open http://localhost:8080/ClearanceSystem after ~10 seconds
```

Double-click `start_tomcat.bat` → wait ~10 seconds.

### Option B — Manual (same as what I use)
```bash
export MSYS_NO_PATHCONV=1 JAVA_HOME="C:\Program Files\Java\jdk-27" \
  CATALINA_OPTS="-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.io.tmpdir=C:/xampp/tomcat/temp"
cd /c/xampp/tomcat
"/c/Program Files/Java/jdk-27/bin/java" -cp "C:\xampp\tomcat\bin\bootstrap.jar;C:\xampp\tomcat\bin\tomcat-juli.jar" org.apache.catalina.startup.Bootstrap
```

---

## PART 3 — Open the App

In your browser (Chrome) go to:

```
http://localhost:8080/ClearanceSystem
```

You should see the **LOGIN PAGE**.

---

## PART 4 — Demo Accounts (login)

| Role | Username | Password |
|---|---|---|
| Admin | `kurt` | `managase` |
| Student | `2025-1001` | `pass123` |
| Officer (Library) | `officer.lib` | `pass123` |
| Officer (Cashier/Dept) | `officer2` | `newpass9` |
| Registrar | `registrar1` | `reg123` |

(These are demo/test accounts — you can create more via Admin → User Management.)

---

## PART 5 — If a Page Changed / You Edited Code (re-deploy)

Every time code changes, the WAR must be re-copied:

```bash
# 1. Stop Tomcat: Ctrl+C in the Tomcat window (or Task Manager → kill java.exe on port 8080)

# 2. Build the WAR (from the project folder):
cd "C:\Users\trnal\OneDrive\Documents\NetBeansProjects\AcademicAdministrativeClearanceManagementSystem"
"/c/Program Files/Java/jdk-27/bin/javac" -cp "target/servlet-api.jar;lib/mariadb-java-client-3.5.3.jar" -d target/classes $(find src/main/java -name "*.java")
rm -rf target/webapp && mkdir -p target/webapp/WEB-INF/classes target/webapp/WEB-INF/lib
cp -r src/main/webapp/* target/webapp/
cp -r target/classes/* target/webapp/WEB-INF/classes/
cp lib/mariadb-java-client-3.5.3.jar target/webapp/WEB-INF/lib/
jar cf target/ClearanceSystem.war -C target/webapp .

# 3. Deploy:
rm -rf /c/xampp/tomcat/webapps/ClearanceSystem
cp target/ClearanceSystem.war /c/xampp/tomcat/webapps/

# 4. Start Tomcat again (Part 2), wait 10s, refresh browser.
```

---

## Common Problems

| Problem | Fix |
|---|---|
| "Can't connect" in browser | Tomcat not running → do Part 2 |
| Login error (DB) | MySQL not running → do Part 1 |
| Old version showing | Redeploy (Part 5) then **hard refresh**: Ctrl+Shift+R |
| Port 8080 already in use | There's a leftover java.exe → Task Manager → end it → restart Tomcat |
| 404 on a page | App is still loading → wait 5 more seconds and refresh |
