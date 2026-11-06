# PANELIST SETUP GUIDE — Read this FIRST

Thank you for reviewing our project:
**Automated Academic and Administrative Clearance Management System**

This package contains everything needed to run the system on YOUR laptop.

## What's inside

| File/Folder | Purpose |
|---|---|
| `ClearanceSystem.war` | The ready-to-deploy web application |
| `database/academic_clearance_db.sql` | The database with sample data + accounts |
| `src/` | The complete Java source code (NetBeans project) |
| `docs/` | Documentation: demo script, ERD, defense paper |
| `pom.xml` | Maven build file |
| `lib/` | MariaDB JDBC driver |

## Requirements (already common on student laptops)

1. **XAMPP** (includes MariaDB + Tomcat) — https://www.apachefriends.org
2. **Java JDK 17 or newer** — https://adoptium.net
3. **NetBeans** (optional — only needed to open/edit the source)

## How to run (5 steps, ~5 minutes)

### Step 1 — Start MySQL (database)
- Open XAMPP Control Panel → click **Start** beside **MySQL**.

### Step 2 — Import the database
- Open browser → `http://localhost/phpmyadmin`
- Click **New** → database name: `academic_clearance_db`
- Go to **Import** tab → choose `database/academic_clearance_db.sql` → **Go**.
- You should see 16 tables.

### Step 3 — Start Tomcat (web server)
- In XAMPP Control Panel, click **Start** beside **Tomcat**.
- If Tomcat is NOT listed in XAMPP, use the bundled Tomcat:
  - Open the `Tomcat` folder from XAMPP and run `startup.bat`
  - (On this project we used XAMPP's Tomcat 8.5 at `C:\xampp\tomcat`)

### Step 4 — Deploy the app
- Copy `ClearanceSystem.war` into the Tomcat **webapps** folder:
  - XAMPP default: `C:\xampp\tomcat\webapps\`
- Tomcat auto-deploys it. Wait ~10 seconds.

### Step 5 — Open the system
- Browser → **http://localhost:8080/ClearanceSystem**

## Demo accounts (log in with these)

| Role | Username | Password |
|---|---|---|
| Administrator | kurt | managase |
| Student | 2025-1001 | pass123 |
| Officer (Library) | officer.lib | pass123 |
| Officer (Cashier) | cashier | cashier |
| Registrar | Registrar | Registrar |

> Tip: if any officer/registrar password fails, log in as admin
> (kurt/managase) → User Management → Reset Pass on that account.

## The 6-minute demo flow (see docs/DEMO_SCRIPT.md for the full script)
1. Login as admin → show User Management, Offices, Terms, Cycles.
2. Login as student → My Clearance (auto-generated checklist).
3. Login as officer → Review Queue → approve an item (CLEARED).
4. Show ACTION REQUIRED loop (officer requests action → student responds).
5. Login as registrar → Final Verification → Approve.
6. Show Audit Logs (admin) for the full tracing trail.

## Troubleshooting

| Problem | Fix |
|---|---|
| Page won't load | Tomcat not running — do Step 3 |
| Login says DB error | MySQL not running — do Step 1 |
| phpMyAdmin shows no tables | Import failed — redo Step 2 |
| 404 on /ClearanceSystem | WAR didn't deploy — wait longer or restart Tomcat |
| Port 8080 busy | Another app uses it — change Tomcat port in server.xml to 8081 and use that in the URL |

## Support
If you hit any problem, message us — we can walk you through it live.
