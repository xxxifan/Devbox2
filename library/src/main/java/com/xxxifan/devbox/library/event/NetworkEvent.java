package com.xxxifan.devbox.library.event;

/**
 * Created by xifan on 6/23/16.
 */
public class NetworkEvent extends BaseEvent {
    public String message;

    public NetworkEvent(String message) {
        this.message = message;
    }
}
