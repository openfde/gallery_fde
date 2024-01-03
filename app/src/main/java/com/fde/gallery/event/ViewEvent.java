package com.fde.gallery.event;

import com.fde.gallery.bean.Multimedia;

public interface ViewEvent {
    void onRightEvent(int pos ,int groupPos);

//    void onDeleteEvent(int pos ,String content);

    void onSelectEvent(int pos ,int groupPos,boolean isSelect);

    void onJumpEvent(Multimedia  multimedia);
}
