package net.glochat.dev.interactors.interfaces;

import java.util.List;

public interface OnDataChangedListener<T> {

    void onListChanged(List<T> list);
}
