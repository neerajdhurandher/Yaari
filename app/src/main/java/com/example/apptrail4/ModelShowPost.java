package com.example.apptrail4;

public class ModelShowPost {

    String pId,uName,uEmail,uid,uImage,pDate_Time,caption,uploadImage,uBio;

    public ModelShowPost() {
    }

    public ModelShowPost(String pId, String uName, String uEmail, String uid, String uImage, String pDate_Time, String caption, String uploadImage, String uBio) {
        this.pId = pId;
        this.uName = uName;
        this.uEmail = uEmail;
        this.uid = uid;
        this.uImage = uImage;
        this.pDate_Time = pDate_Time;
        this.caption = caption;
        this.uploadImage = uploadImage;
        this.uBio = uBio;
    }


    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuImage() {
        return uImage;
    }

    public void setuImage(String uImage) {
        this.uImage = uImage;
    }

    public String getpDate_Time() {
        return pDate_Time;
    }

    public void setpDate_Time(String pDate_Time) {
        this.pDate_Time = pDate_Time;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUploadImage() {
        return uploadImage;
    }

    public void setUploadImage(String uploadImage) {
        this.uploadImage = uploadImage;
    }

    public String getuBio() {
        return uBio;
    }

    public void setuBio(String uBio) {
        this.uBio = uBio;
    }
}
