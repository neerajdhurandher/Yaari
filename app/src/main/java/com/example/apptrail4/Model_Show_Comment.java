package com.example.apptrail4;

public class Model_Show_Comment {

    String commenttxt,time,cId,uName,uDp,uUID;

    public Model_Show_Comment() {
    }

    public Model_Show_Comment(String commenttxt, String time, String cId, String uName, String uDp, String uUID) {
        this.commenttxt = commenttxt;
        this.time = time;
        this.cId = cId;
        this.uName = uName;
        this.uDp = uDp;
        this.uUID = uUID;
    }

    public String getCommenttxt() {
        return commenttxt;
    }

    public void setCommenttxt(String commenttxt) {
        this.commenttxt = commenttxt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuUID() {
        return uUID;
    }

    public void setuUID(String uUID) {
        this.uUID = uUID;
    }
}
