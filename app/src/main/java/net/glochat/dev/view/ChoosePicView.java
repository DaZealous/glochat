package net.glochat.dev.view;

public interface ChoosePicView {
    boolean imageExist(String image);
    void addImage(String image);
    void removeImage(String image);
}
