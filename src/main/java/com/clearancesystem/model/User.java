package com.clearancesystem.model;

public class User {

    private int userId;
    private int roleId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String accountStatus;
    private String roleCode;

    private Integer studentId;
    private String studentNumber;
    private String programCode;
    private Integer yearLevel;
    private Integer programId;

    private String officeCodes;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getProgramCode() { return programCode; }
    public void setProgramCode(String programCode) { this.programCode = programCode; }

    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }

    public Integer getProgramId() { return programId; }
    public void setProgramId(Integer programId) { this.programId = programId; }

    public String getOfficeCodes() { return officeCodes; }
    public void setOfficeCodes(String officeCodes) { this.officeCodes = officeCodes; }

    public boolean isAdmin() { return "ADMIN".equals(roleCode); }

    public boolean isOfficer() { return "OFFICER".equals(roleCode); }

    public boolean isRegistrar() { return "REGISTRAR".equals(roleCode); }

    public boolean isStudent() { return "STUDENT".equals(roleCode); }

    public boolean isActive() { return "ACTIVE".equals(accountStatus); }
}
