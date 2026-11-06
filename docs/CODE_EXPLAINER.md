# CODE EXPLAINER — Defense Notes (English)

This guide walks through the actual code files so you can explain ANY line
the panelists ask about. Read each section, then say it out loud once.

---

## 1. DBConnection.java — "How do we connect to the database?"

```java
private static final String URL = "jdbc:mariadb://localhost:3306/academic_clearance_db";
private static final String USER = "root";
private static final String PASSWORD = "";
```

**What to say:**
> "We have one central file that opens the database connection. Every DAO
> calls `DBConnection.getConnection()`. The URL says: use the MariaDB driver
> (`jdbc:mariadb`), connect to this computer (`localhost`), port `3306`,
> database `academic_clearance_db`. User is root with no password because
> that's the default XAMPP setup."

Key line: `Class.forName("org.mariadb.jdbc.Driver")` — loads the driver
library so Java knows how to talk to MariaDB.

---

## 2. UserDAO.java — "How does login actually work?"

Two important methods:

### findByUsername() — gets the user + role + student info in ONE query
```sql
SELECT ... FROM users u
JOIN roles r ON r.role_id = u.role_id
LEFT JOIN students s ON s.user_id = u.user_id
LEFT JOIN programs p ON p.program_id = s.program_id
WHERE u.username = ?
```
**What to say:**
> "One query pulls the account, its role, and (if the user is a student)
> the student profile and program. `LEFT JOIN` means: if there is no
> student row, we still get the user — that's how officers and admins work
> with the same users table."

### authenticate() — the actual check
```java
User user = findByUsername(username);
if (user == null) return null;                    // no such user
if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) return null; // wrong password
if (!user.isActive()) return null;                // disabled account
return user;
```
**What to say:**
> "Three checks: does the username exist? Does the password hash match?
> Is the account ACTIVE? Only if all three pass do we return the user."

---

## 3. PasswordUtil.java — "Why hash passwords?"

```java
// hash = sha256$<random salt>$<sha256(salt+password)>
public static String hashPassword(String password) { ... new SecureRandom() ... }
public static boolean verifyPassword(String password, String storedHash) { ... }
```
**What to say:**
> "We never store the real password. We add a random 'salt' (extra random
> characters), then run SHA-256. Even two users with the same password get
> different hashes because the salt is random. To check a login, we
> re-compute the hash with the stored salt and compare. If the database
> leaks, attackers still cannot read any password."

---

## 4. LoginServlet.java — "Walk us through login"

```java
String username = request.getParameter("username");   // 1. read form
String password = request.getParameter("password");
User user = userDAO.authenticate(username.trim(), password); // 2. check DB

if (user != null) {
    HttpSession session = request.getSession(true);   // 3. create session
    session.setAttribute("loggedUser", user);          //    remember WHO logged in
    session.setMaxInactiveInterval(30 * 60);           //    30-minute timeout
    response.sendRedirect(request.getContextPath() + "/home"); // 4. go to role router
} else {
    request.setAttribute("error", "Mali ang username o password...");
    request.getRequestDispatcher("/index.jsp").forward(request, response); // 5. show error
}
```
**What to say:**
> "The servlet reads the username and password from the form, asks the DAO
> to authenticate, and if valid, stores the whole User object in the
> session so every later page knows who is logged in. It then redirects to
> `/home`, which routes by role. If invalid, we forward back to the login
> page with an error message. We use POST so the password never appears in
> the URL."

---

## 5. AuthFilter.java — "How do you stop students from opening admin pages?"

```java
if (path.contains("/student/") && user.isStudent()) allowed = true;
else if (path.contains("/officer/") && user.isOfficer()) allowed = true;
...
if (!allowed) { session.invalidate(); sendRedirect(index.jsp); }
```
**What to say:**
> "A filter is a checkpoint that runs BEFORE any page in a folder. The
> web.xml maps it to `/student/*`, `/officer/*`, `/registrar/*`, `/admin/*`.
> If a student types the admin URL, the filter sees role STUDENT inside
> `/admin/` → not allowed → we destroy the session and send them to login.
> This is our role-based access control."

---

## 6. HomeServlet.java — "Where does each role go after login?"

```java
if (user.isAdmin())     → /admin/admin-dashboard.jsp
else if (user.isRegistrar()) → /registrar/registrar-dashboard.jsp
else if (user.isOfficer())   → /officer/officer-dashboard.jsp
else if (user.isStudent())   → /student/student-dashboard.jsp
```
**What to say:**
> "One router. After login the user lands here and we simply look at the
> role code to send them to the right dashboard. Role determines
> everything — the URL, the permissions, the features."

---

## 7. ClearanceDAO.java — "How is a student's clearance created?"

### findOrCreateRequest()
```java
// 1. SELECT existing request (student_id + cycle_id)
// 2. If none: INSERT new request, then createItemsForRequest()
```
### createItemsForRequest() — the clever part
```sql
INSERT INTO clearance_items (request_id, requirement_id, status)
SELECT ?, requirement_id, 'PENDING' FROM clearance_requirements
WHERE cycle_id = ? AND is_required = 1
```
**What to say:**
> "When a student opens My Clearance, we check if a request already exists
> for them in this cycle. If not, we create one, then ONE SQL statement
> copies every required office from the cycle into `clearance_items` as
> PENDING. That's the automation — the student doesn't add offices; the
> system generates them from what the admin configured."

### recalcOverall() — the status calculator
```java
if any item is ACTION_REQUIRED      → overall = ACTION_REQUIRED
else if any item is PENDING/IN_REVIEW → overall = IN_PROGRESS
else (all CLEARED/NOT_APPLICABLE)    → overall = AWAITING_REGISTRAR
```
**What to say:**
> "The student never edits their overall status. We recalculate it from
> the items after every decision: any action needed → red status; still
> waiting on offices → in progress; everything cleared → awaiting the
> registrar. The status is always derived, never hand-typed."

---

## 8. OfficerDAO.java — "How does the officer decide?"

### getQueue() — what the officer sees
```sql
... FROM clearance_items i
JOIN clearance_requests r ... JOIN students s ... JOIN programs p ...
JOIN clearance_requirements req ... JOIN offices o ...
WHERE req.office_id = ? AND i.status IN ('PENDING','IN_REVIEW','ACTION_REQUIRED')
```
**What to say:**
> "The officer only sees items for THEIR assigned office (WHERE office_id
> = ?), and only items that still need work (PENDING, IN_REVIEW, or
> returned ACTION_REQUIRED). Cleared items disappear from the queue."

### decide() — the whole transaction (this is the heart of the system)
1. `UPDATE clearance_items SET status, remarks, reviewed_by, reviewed_at`
2. `INSERT INTO clearance_history` (old status, new status, who, when)
3. `INSERT INTO notifications` (tell the student)
4. Recalculate overall status
5. `INSERT INTO audit_logs` (DECISION / CLEARANCE_ITEM / itemId)

**What to say:**
> "One action does five things in a single transaction: updates the item,
> records the history, notifies the student, recalculates the overall
> status, and writes an audit log. That is why tracing is possible — we
> know exactly which officer cleared which item and when."

---

## 9. RegistrarDAO.java — "What does the registrar do?"

### finalApprove()
1. Look up the student's user_id
2. `UPDATE clearance_requests SET overall_status='CLEARED', completed_at=NOW(), final_checked_by=?, final_checked_at=NOW()`
3. Insert notification "Clearance Approved"
4. Insert audit log FINAL_APPROVE

### returnForAction()
Same idea but status goes back to `IN_PROGRESS` with the reason in a notification.

**What to say:**
> "The registrar is the last gate. They only see requests where every
> office is already cleared (AWAITING_REGISTRAR). Approve → CLEARED with a
> completion timestamp. Return → the request goes back to IN_PROGRESS and
> the student gets the reason. Nothing is permanently deleted anywhere —
> status changes only."

---

## 10. The Pattern — "What is a DAO?"

**What to say (memorize this):**
> "A DAO is a Data Access Object. All SQL lives inside these classes —
> the servlets never write SQL. If the database changes, we only edit the
> DAO. If the page changes, we only edit the JSP. That separation is our
> MVC architecture and it makes the code maintainable."

---

## 11. Quick Cheat Sheet — file roles

| File | Role in one sentence |
|---|---|
| DBConnection.java | Opens DB connections |
| PasswordUtil.java | Hashes + verifies passwords |
| UserDAO.java | All SQL about users/students |
| LoginServlet.java | Checks credentials, starts session |
| HomeServlet.java | Routes by role |
| AuthFilter.java | Blocks wrong-role page access |
| ClearanceDAO.java | Student clearance: create request, items, status, notifications |
| OfficerDAO.java | Officer queue + decisions + history + audit |
| RegistrarDAO.java | Final approve / return + audit |
| AuditDAO.java | Reads audit logs for admin |
| UserServlet.java | Admin creates/edits/disables accounts |
| OfficeServlet.java | Admin manages offices & programs |
| TermServlet.java | Admin manages academic terms |
| CycleServlet.java | Admin manages clearance cycles + required offices |
| AuditServlet.java | Admin views audit logs |
| *.jsp | The actual pages (View) |

---

## 12. Likely Panelist Questions + Our Answers

**Q: Why MariaDB and not MySQL?**
> "They're compatible; MariaDB is what ships with XAMPP. Our JDBC driver
> (MariaDB Connector/J) supports both."

**Q: What is JSP?**
> "JavaServer Pages — an HTML file where we can embed Java. It becomes a
> servlet on the server; the browser only receives HTML."

**Q: How many tables?**
> "16 tables covering accounts, school data, clearance workflow, history,
> notifications, attachments, and audit logs."

**Q: Where is the workflow logic?**
> "In the DAOs, mostly in `decide()` (OfficerDAO) and `recalcOverall()`
> (ClearanceDAO). The status is always derived from the items."

**Q: What if the student doesn't clear on time?**
> "The admin can close the cycle. Any request still not CLEARED can be
> marked CLOSED_UNCLEARED by the registrar — the enum already exists."

**Q: Can an officer approve another office's item?**
> "No. The queue query filters by the officer's assigned offices
> (`user_offices`), and the decision form only shows items from that
> office's queue."
