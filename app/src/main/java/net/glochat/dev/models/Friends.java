package net.glochat.dev.models;


public class Friends {

    public String uid;

    public Friends(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "uid=" + uid +
                '}';
    }



}
