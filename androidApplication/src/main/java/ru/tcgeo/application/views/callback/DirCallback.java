package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.DirHolder;

public interface DirCallback {
    void onOpen(DirHolder holder);

    void onExit(DirHolder holder);

    void onEnter(DirHolder holder);
}
