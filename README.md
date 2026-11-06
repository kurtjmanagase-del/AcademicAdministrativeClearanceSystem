# Core system files only – Academic Administrative Clearance System

This repository contains the **complete working web application** for managing student clearances, built with Java Servlets/JSP, MariaDB, and Apache Tomcat 8.5.

## What you will find inside this repo:

### Core Application Files
- **`.gitignore`** – Ignore build artifacts and IDE files
- **`pom.xml`** – Maven build configuration
- **`lib/mariadb-java-client-3.5.3.jar`** – MariaDB JDBC driver
- **`database/academic_clearance_db.sql`** – Complete database schema + seed data
- **`src/`** – Entire Java source tree:
  - `src/main/java/com/clearancesystem/` – All servlets, DAOs, models, utils
  - `src/main/webapp/` – JSP pages, static assets, `web.xml`
- **`target/ClearanceSystem.war`** – Pre-built WAR file (ready to deploy to Tomcat)

### How to Run

1. **Set up XAMPP**
   - Install XAMPP (includes Apache Tomcat 8.5, MariaDB 10.5)
2. **Import Database**
   - Import `database/academic_clearance_db.sql` into MariaDB (use `academic_clearance_db` as DB name)
3. **Deploy WAR**
   - Copy `target/ClearanceSystem.war` to `C:/xampp/tomcat/webapps/`
4. **Start Tomcat**
   - Use the provided `docs/start_tomcat.bat` or the XAMPP "Start Tomcat" button
5. **Access**
   - Open `http://localhost:8080/ClearanceSystem` in your browser

### Demo Accounts (Default Passwords – Change on first login!)
| Role          | Username         | Password |
|---------------|------------------|----------|
| Administrator | `kurt`           | `admin123` |
| Registrar     | `registrar1`     | `registrar123` |
| Clearance Officer | `officer.lib` | `officer123` |
| Student       | `2025-1001`      | `student123` |

### Technologies Used
- Java (Servlet API 3.0, JSP 2.3)
- Maven (POM)
- MariaDB (JDBC)
- Apache Tomcat 8.5

### Roles & Permissions
| Role | Capabilities |
|------|--------------|
| **Administrator** | Full system admin – create / edit / soft‑delete users, offices, programs, terms, cycles. **Can NOT approve office clearances** (by design). |
| **Registrar** | Finalize clearances after officer decisions – approve/reject, view reports. |
| **Clearance Officer** | Handle office‑specific clearance requests – clear, reject, comment. |
| **Student** | Submit clearance requests, track status, respond to officer comments. |

### Demo Accounts (Default Passwords – Change on first login!)
| Role | Username | Password |
|------|----------|----------|
| Administrator | `kurt` | `admin123` |
| Registrar | `registrar1` | `registrar123` |
| Officer | `officer.lib` | `officer123` |
| Student | `2025-1001` | `student123` |

### Features Overview
- Role‑based access control (Admin, Registrar, Officer, Student)
- Create / view / edit / soft‑delete users, offices, programs, terms, cycles
- Clearance request workflow (student → officer decisions → registrar final approval)
- Real‑time notifications, audit logs, and history tracking
- Responsive dashboards per role

### Build Instructions (Optional)
If you have NetBeans or Maven installed:
```bash
mvn clean package
# Produces target/ClearanceSystem.war
```

### Notes
- Passwords are initially plaintext for demo; production deployment should hash them.
- Adjust Tomcat memory settings (`setenv.sh`/`setenv.bat`) if needed.
- Ensure MariaDB is running on port 3306 before importing the SQL file.

---

**Issues / Support** → Open an issue if you run into problems.
**Pull Requests** → Fork and PR changes; ensure tests pass before merging.
**Discussions** → Ask questions, share ideas, or request features.