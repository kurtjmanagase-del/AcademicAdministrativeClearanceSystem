# PANELIST SETUP GUIDE — READ THIS FIRST

Thank you for reviewing our project:
**Automated Academic and Administrative Clearance Management System**

This package contains everything you need to RUN the system on your laptop.

## What's inside

| Item | Purpose |
|---|---|
| `ClearanceSystem.war` | The ready-to-deploy web application (the actual system) |
| `database/academic_clearance_db.sql` | The database with sample data + demo accounts |
| `src/` | Complete Java source code (Servlets, DAOs, JSPs) |
| `pom.xml` + `lib/` | Maven build file + MariaDB JDBC driver |
| `docs/` | Demo script, ERD, defense paper, run guide |

## Requirements

1. **XAMPP** (includes MariaDB + Apache Tomcat 8.5) — https://www.apachefriends.org
2. **Java JDK 17+** — https://adoptium.net
3. (Optional) **NetBeans** — only if you want to open/edit the source code

---

## HOW TO RUN — 5 steps

### Step 1 — Start MySQL
- Open XAMPP Control Panel → click **Start** beside **MySQL**.

### Step 2 — Import the database
- Browser → `http://localhost/phpmyadmin`
- Click **New** → database name exactly: `academic_clearance_db` (utf8mb4)
- Click **Import** tab → choose `database/academic_clearance_db.sql` → **Go**
- You should see 16 tables (roles, users, students, offices, clearance_cycles, ...)

### Step 3 — Start Tomcat
- XAMPP Control Panel → click **Start** beside **Tomcat**.
- (XAMPP's Tomcat is at `C:\xampp\tomcat`; if you use a standalone Tomcat 8.5+, that works too)

### Step 4 — Deploy the app
- Copy **`ClearanceSystem.war`** into Tomcat's `webapps` folder:
  - `C:\xampp\tomcat\webapps\`
- Tomcat auto-deploys. Wait ~10 seconds.

### Step 5 — Open the system
- Browser → **http://localhost:8080/ClearanceSystem**

---

## DEMO ACCOUNTS

| Role | Username | Password |
|---|---|---|
| Administrator | kurt | managase |
| Student | 2025-1001 | pass123 |
| Officer (Library) | officer.lib | pass123 |
| Officer (Cashier) | cashier | cashier |
| Registrar | Registrar | Registrar |

> If any officer/registrar password fails: login as admin (kurt/managase)
> → User Management → Reset Pass on that account.

---

## 6-MINUTE DEMO FLOW (full script in docs/DEMO_SCRIPT.md)

1. **Admin** (kurt/managase): show User Management, Offices, Terms, Clearance Cycles.
2. **Student** (2025-1001/pass123): My Clearance — auto-generated checklist.
3. **Officer** (officer.lib/pass123): Review Queue → approve an item (CLEARED).
4. **ACTION REQUIRED loop**: officer requests action → student responds.
5. **Registrar** (Registrar/Registrar): Final Verification → Approve.
6. **Admin**: Audit Logs — full tracing trail.

---

## TROUBLESHOOTING

| Problem | Fix |
|---|---|
| Page won't load | Tomcat not running → Step 3 |
| Login DB error | MySQL not running → Step 1 |
| phpMyAdmin shows no tables | Import failed → redo Step 2 |
| 404 on /ClearanceSystem | WAR not deployed → wait/restart Tomcat |
| Port 8080 busy | Change Tomcat port to 8081 in server.xml, use that port in URL |

Questions? Message us — we can walk you through it live.
