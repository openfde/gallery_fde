package com.fde.gallery.event;

import com.fde.gallery.bean.Multimedia;

public interface ViewEvent {
    void onRightEvent(int pos );

//    void onDeleteEvent(int pos ,String content);

    void onSelectEvent(int pos ,boolean isSelect);

    void onJumpEvent(Multimedia  multimedia);
}
