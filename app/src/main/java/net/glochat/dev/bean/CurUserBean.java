package net.glochat.dev.bean;



public class CurUserBean {
    private VideoBe.UserBean userBean;

    public CurUserBean(VideoBe.UserBean userBean) {
        this.userBean = userBean;
    }

    public VideoBe.UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(VideoBe.UserBean userBean) {
        this.userBean = userBean;
    }
}
