# Architecture Explained — Simple Version (for the Defense)

## 1. What the Tech Stack Means

| Piece | What it is | What it does in our system |
|---|---|---|
| **NetBeans** | IDE (writing tool) | Where we write all the Java + JSP code |
| **Java Servlets** | Backend logic (the "brain") | Receives requests from the browser, talks to the database, decides what to show |
| **JSP (JavaServer Pages)** | Frontend pages (the "face") | The actual web pages the user sees (login, dashboards, tables) |
| **HTML / CSS / JS** | Web basics | Structure, design/colors, and click actions on the pages |
| **Apache Tomcat 8.5** | Web server (from XAMPP) | Runs our app; listens on `http://localhost:8080/ClearanceSystem` |
| **MariaDB (XAMPP)** | Database | Stores all records: users, offices, clearances, logs |
| **JDBC + MariaDB Connector/J** | Bridge | The Java code ↔ database connection |
| **MVC pattern** | Architecture style | **M**odel (data), **V**iew (page), **C**ontroller (logic) |

## 2. How Our Files Map to MVC (this is the "explain your architecture" answer)

We use a **simplified MVC**. Every feature has exactly 3 pieces:

```
xxx.jsp            = the VIEW  (what the user sees)
xxxServlet.java    = the CONTROLLER (the brain: reads input, calls DAO, sends to JSP)
xxxDAO.java        = the MODEL / DATA ACCESS (all SQL goes here)
```

Concrete example — the **Login** feature:

1. User opens `index.jsp` (the View — a form with Username + Password).
2. User clicks SIGN IN → browser sends the form to `/login`.
3. `LoginServlet.java` (the Controller) receives it:
   - reads `username` and `password` from the request
   - calls `UserDAO.authenticate(username, password)`
4. `UserDAO.java` (the Data layer) runs the SQL:
   - `SELECT ... FROM users WHERE username = ?`
   - verifies the password against the stored hash
   - returns a `User` object (or null if wrong)
5. Back in the Servlet:
   - if user found → save user in **session**, redirect to `/home`
   - `HomeServlet` checks the **role** and sends the user to the correct dashboard
6. The dashboard JSP renders the page.

> One-line answer: **"We separate data access (DAO), business logic (Servlet), and display (JSP). Each screen has one DAO, one Servlet, and one JSP — that's our architecture."**

## 3. The 3-Tier Flow (draw this on the board)

```
[BROWSER]  →  [TOMCAT / SERVLETS]  →  [MARIADB]
 (user)        (our Java code)        (database)
    ↑                                     ↓
    └────────── HTML/JSP page ←───────────┘
```

- Browser = client (just shows pages)
- Tomcat + Servlets + DAO = application layer (all logic)
- MariaDB = data layer (everything is stored)

## 4. Why We Chose Each Piece (panelists love "why")

| Question they may ask | Our answer |
|---|---|
| Why Servlets and JSP, not Spring? | Course requirement — simple, standard Java web tech; easier for us to explain and maintain |
| Why MVC / DAO pattern? | Keeps code organized: if a page breaks, we only fix the JSP; if SQL breaks, only the DAO. No spaghetti code |
| Why Tomcat? | The standard free server for Java web apps; ships with XAMPP |
| Why MariaDB? | Free, part of XAMPP, SQL standard; phpMyAdmin for easy viewing |
| Why password hashing (SHA-256 + salt)? | Security: we never store plain-text passwords. Even if the DB leaks, passwords can't be read |

## 5. Where Each Role's Code Lives (for "walk us through the system")

| Role | View (JSP) | Controller (Servlet) | Data (DAO) |
|---|---|---|---|
| Admin | `admin/*.jsp` (user-list, office-list, term-list, cycle-list, audit-log) | UserServlet, OfficeServlet, TermServlet, CycleServlet, AuditServlet | UserDAO, OfficeDAO, TermDAO, CycleDAO, AuditDAO |
| Student | `student/my-clearance.jsp`, `notifications.jsp` | StudentServlet, NotificationServlet | ClearanceDAO |
| Officer | `officer/queue.jsp` | OfficerServlet | OfficerDAO |
| Registrar | `registrar/verification.jsp` | RegistrarServlet | RegistrarDAO |
| All roles | `index.jsp` (login) | LoginServlet, LogoutServlet, HomeServlet, AuthFilter | UserDAO |

## 6. Session & Security (bonus points)

- After login, the `User` object is stored in the **HTTP session** → every page can know who is logged in.
- **AuthFilter** (a filter = checkpoint before any page) checks:
  - `/student/*` → only STUDENT role may pass
  - `/officer/*` → only OFFICER
  - `/registrar/*` → only REGISTRAR
  - `/admin/*` → only ADMIN
- If someone types an admin URL as a student, the filter kicks them back to login. (We can demo this — great defense moment.)
- Passwords stored as `sha256$salt$hash` — verified with `PasswordUtil`.

## 7. 30-Second Pitch (memorize this)

> "Our system is a Java web application that automates the school clearance process. We use Servlets and JSP following a DAO/MVC pattern, running on Apache Tomcat with a MariaDB database. There are four roles — Student, Clearance Officer, Registrar, and Admin — each with their own portal. The flow is: Admin sets up terms and clearance cycles with required offices, students open their clearance, officers approve or request action per office, the student responds, and the Registrar does the final approval. Every decision is recorded in history and audit logs for tracing, and students get notifications at every step."
