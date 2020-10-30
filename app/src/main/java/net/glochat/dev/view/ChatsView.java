package net.glochat.dev.view;

import net.glochat.dev.models.ChatDao;

public interface ChatsView {
    String getUserPhoto();
    String getUsernam();
    void startProgress(int view);
    void reload();
    void shareMessage(ChatDao dao);
}
