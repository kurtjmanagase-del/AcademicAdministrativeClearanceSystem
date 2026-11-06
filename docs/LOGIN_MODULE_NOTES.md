# =====================================================================
#  LOGIN MODULE — panimula ng Authentication & Authorization
#  Tech stack: Java Servlet (JAKARTA) + JDBC + JSP + XAMPP MariaDB
# =====================================================================
#
#  PROJEKTO:  AcademicAdministrativeClearanceManagementSystem
#  STRUCTURE:
#    src/main/java/com/clearancesystem/
#        config/DBConnection.java          - koneksyon sa MySQL/MariaDB
#        model/User.java                   - representasyon ng user
#        dao/UserDAO.java                  - data access para sa users table
#        util/PasswordUtil.java            - SHA-256 hash + salt para sa password
#        servlet/LoginServlet.java         - humahawak ng login form
#        servlet/LogoutServlet.java        - nag-aalis ng session
#        servlet/HomeServlet.java          - redirect base sa role
#        filter/AuthFilter.java            - bawal pumasok nang walang login
#    src/main/webapp/
#        index.jsp                         - login page
#        student/student-dashboard.jsp     - landing page ng Student
#        officer/officer-dashboard.jsp     - landing page ng Officer
#        registrar/registrar-dashboard.jsp - landing page ng Registrar
#        admin/admin-dashboard.jsp         - landing page ng Admin
#        WEB-INF/web.xml                   - configuration ng servlets
#
#  MGA ROLE AT LANDING PAGE:
#    STUDENT   -> /student/student-dashboard.jsp
#    OFFICER   -> /officer/officer-dashboard.jsp
#    REGISTRAR -> /registrar/registrar-dashboard.jsp
#    ADMIN     -> /admin/admin-dashboard.jsp
#
#  FLOW NG LOGIN:
#    1. index.jsp -> form (username + password)
#    2. LoginServlet -> hanapin sa DB -> i-verify hash -> create session
#    3. HomeServlet -> i-check role -> redirect sa tamang dashboard
#    4. AuthFilter -> hinaharangan ang direktang pag-access sa dashboard
#
#  NOTE: XAMPP's Tomcat ay 8.5 -> gumagamit tayo ng JAVAX (hindi jakarta).
#  Maven dependency:
#  <dependency>
#      <groupId>javax.servlet</groupId>
#      <artifactId>javax.servlet-api</artifactId>
#      <version>4.0.1</version>
#      <scope>provided</scope>
#  </dependency>
#  Ang servlet mappings (LoginServlet, LogoutServlet, HomeServlet) ay nasa
#  @WebServlet annotation mismo - hindi na kailangan ng entries sa web.xml.
#
#  DEMO ACCOUNTS (mula sa database script):
#    admin          / admin123       -> System Administrator
#    registrar      / registrar123   -> Registrar
#    officer.library/ officer123    -> Clearance Officer (Library)
#    2024-0001      / student123     -> Student (Juan Dela Cruz)
#
#  SUSUNOD NA HAKBANG (pwedeng gawin pagkatapos):
#    - Student dashboard: listahan ng clearance items + status
#    - Officer queue: pending clearance requests per office
#    - Registrar final verification
#    - Admin user management
# =====================================================================
