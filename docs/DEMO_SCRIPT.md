# DEMO SCRIPT — Walk Through the System (Defense-Ready)

The system is LIVE at: http://localhost:8080/ClearanceSystem

## Current test data (as of now)
- Cycle: **MidYearClearanceV2** (OPEN, 2026-2027 1st Sem) — required offices: LIB, CAS, DEPT
- Student **2025-1001** — request already exists (LIB+CAS+DEPT all CLEARED, status AWAITING_REGISTRAR)
- Student **00-0000-0001** — IN_PROGRESS (LIB, CAS cleared; DEPT PENDING)

## Demo accounts
| Role | Username | Password |
|---|---|---|
| Admin | kurt | managase |
| Student | 2025-1001 | pass123 |
| Student 2 | teststudent2 | pass123* |
| Officer (Library) | officer.lib | pass123 |
| Officer (Cashier/Dept) | officer2 | newpass9 |
| Registrar | registrar1 | reg123 |

(* verify: not all test accounts have pass123 — check in User Management if login fails)

---

# THE DEMO — 6 Acts (about 6–8 minutes)

## ACT 1 — LOGIN + ROLE ROUTING (30 sec)
1. Open `http://localhost:8080/ClearanceSystem` → LOGIN PAGE appears.
2. Say: *"This is our single login for all roles. The system checks the
   role and sends each user to their own portal."*
3. Log in as **kurt / managase** → lands on **SYSTEM ADMINISTRATION** dashboard.
4. Say: *"Admin landed on the admin dashboard because the system read the
   role from the database after login."*
5. **Security demo (great point):** while logged in as admin, type
   `http://localhost:8080/ClearanceSystem/student/student-dashboard.jsp`
   → the AuthFilter blocks it and kicks you to login.
   Say: *"A student cannot open admin pages and an admin cannot open
   student pages — the filter checks the role before any page loads."*

## ACT 2 — ADMIN SETS UP THE SCHOOL (2 min)
From the admin dashboard click each button (don't linger, just show):

1. **User Management** (`/admin/users`)
   - Show the table of all accounts (roles, status badges).
   - Click **Edit** on a user → inline form appears → change name → Save.
   - Click **Disable** on a user → badge turns red DISABLED.
   - Click **Activate** to bring it back.
   - Say: *"Accounts are never deleted — we only disable them, so history
     stays intact."*

2. **Offices & Programs** (`/admin/offices`)
   - Show the offices list (LIB, CAS, DEPT...).
   - Add one office quickly: type code + name → Create → appears in table.
   - Say: *"Offices are stored in the database, not hard-coded. The admin
     decides which offices exist."*

3. **Academic Terms** (`/admin/terms`)
   - Show 2026-2027 First Semester row (OPEN).
   - Say: *"A term is a school year + semester. We can close or reopen it."*

4. **Clearance Cycles** (`/admin/cycles`)
   - Show **MidYearClearanceV2** row: Term, Opens, Closes, Offices (LIB,
     CAS, DEPT), status badge OPEN.
   - Say: *"This is the heart of the configuration. The admin creates a
     clearance cycle and ticks WHICH offices are required. Whatever is
     ticked here becomes the student's checklist automatically."*

## ACT 3 — STUDENT OPENS CLEARANCE (1 min)
1. Log out. Log in as **2025-1001 / pass123** → STUDENT PORTAL.
2. Click **My Clearance**.
3. Say: *"The student sees their checklist. Notice it matches exactly the
   offices the admin ticked: Library, Cashier, Department. The system
   generates these automatically — the student doesn't add them."*
4. Show the status badges: CLEARED (green) for the offices already done.
5. Open **Notifications** → show the updates the student received.

## ACT 4 — OFFICER APPROVES (1.5 min)
1. Log out → log in as **officer.lib / pass123** → OFFICE STAFF PORTAL.
2. Click **Open Review Queue**.
3. Show the office dropdown (Library) and the queue table:
   - Say: *"The officer only sees students who still need THIS office —
     other offices' work is invisible."*
4. Pick a PENDING item → click **View** → detail screen shows:
   - Student info, item status, remarks, **history** (who changed what
     when), and the decision form.
5. Choose **CLEARED**, type a remark, submit.
6. Say: *"One click updates the item, records history, notifies the
   student, recalculates the overall status, and writes an audit log.
   That's our automation."*

## ACT 5 — ACTION REQUIRED LOOP (1.5 min) ← shows the "smart" part
1. As officer, pick another PENDING item → choose **ACTION REQUIRED**,
   type a remark like *"Kindly settle your outstanding balance."* → submit.
2. Log in as the student → My Clearance:
   - Show the red **ACTION REQUIRED** badge + the officer's remark.
   - Say: *"The student sees exactly what the office needs."*
3. Student types a response in the textarea → submit.
4. Back as officer → the item is back in the queue (because the student
   responded) → clear it.
5. Say: *"This is the back-and-forth loop — it's what makes the system
   track everything instead of relying on paper."*

## ACT 6 — REGISTRAR FINAL APPROVAL (1 min)
1. Log in as **registrar1 / reg123** → REGISTRAR PORTAL → **Final Verification**.
2. Show the AWAITING_REGISTRAR queue (student 2025-1001).
3. Click **View** → all office items listed with status → click
   **Approve Final**.
4. Show the Completed Clearances table below (green CLEARED).
5. Say: *"Only when every office is cleared does the registrar see the
   student — final approval makes it official with a completion date."*
6. (Optional) Log back in as student → status shows CLEARED + notification
   "Clearance Approved".

---

# WHAT TO SAY WHEN THEY ASK MID-DEMO

| They ask | Answer |
|---|---|
| "What's the difference between a term and a cycle?" | "A term is just the school year + semester. A cycle is the actual clearance period inside that term — it sets which offices are required and when it opens/closes." |
| "Why does the student have items but not the officer?" | "The student has ONE clearance request; the offices each have their own item inside it. The officer only sees items for their own office." |
| "What happens if the officer says ACTION REQUIRED?" | "The item returns to the student. The overall status becomes ACTION_REQUIRED. When the student responds, it comes back to the officer's queue." |
| "Can the registrar approve without all offices cleared?" | "No — the registrar queue only shows requests already AWAITING_REGISTRAR, which happens only when all items are CLEARED." |
| "How do you know who cleared what?" | "Every decision writes to clearance_history AND audit_logs with the user, the date, and the change. The officer detail screen shows the history table; the admin has the Audit Logs page." |
| "What if a student has no items?" | "It can't happen — opening My Clearance auto-generates one item per required office from the cycle." |

---

# ENDING LINE (memorize)

> "To summarize: the admin configures the school and the cycle, the
> student opens their clearance and sees an auto-generated checklist, the
> officers approve or request action per office, and the registrar gives
> the final approval. Every single decision is logged and the student is
> notified at every step — that's how we automate the clearance process."
