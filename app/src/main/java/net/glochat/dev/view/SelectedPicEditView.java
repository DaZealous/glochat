package net.glochat.dev.view;

public interface SelectedPicEditView {
    void selectImage(String image, int newPos);
    int getPrevPos();
    void removeImage(String image, int pos);
}
