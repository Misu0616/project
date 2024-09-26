package com.jica.project.model;

public class memberInfo {
    String memKey;
    String memId;
    String memPassword;
    String checkMemPW;
    String memEmail;

    public memberInfo() {
    }

    public memberInfo(String memKey, String memId, String memPassword, String checkMemPW, String memEmail) {
        this.memKey = memId;
        this.memId = memId;
        this.memPassword = memPassword;
        this.checkMemPW = checkMemPW;
        this.memEmail = memEmail;
    }

    public memberInfo(String memId, String memPassword, String memEmail) {
        this.memId = memId;
        this.memPassword = memPassword;
        this.memEmail = memEmail;
    }

    public String getMemKey() {
        return memKey;
    }

    public void setMemKey(String memKey) {
        this.memKey = memKey;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getMemPassword() {
        return memPassword;
    }

    public void setMemPassword(String memPassword) {
        this.memPassword = memPassword;
    }

    public String getCheckMemPW() {
        return checkMemPW;
    }

    public void setCheckMemPW(String checkMemPW) {
        this.checkMemPW = checkMemPW;
    }

    public String getMemEmail() {
        return memEmail;
    }

    public void setMemEmail(String memEmail) {
        this.memEmail = memEmail;
    }

    @Override
    public String toString() {
        return "memberInfo{" +
                "memKey='" + memKey + '\'' +
                ", memId='" + memId + '\'' +
                ", memPassword='" + memPassword + '\'' +
                ", checkMemPW='" + checkMemPW + '\'' +
                ", memEmail='" + memEmail + '\'' +
                '}';
    }
}

