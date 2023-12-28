package com.fde.gallery.event;

public interface ViewEvent {
    void onRightEvent(int pos );

//    void onDeleteEvent(int pos ,String content);

    void onSelectEvent(int pos ,boolean isSelect);
}
