package net.starly.optimize.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {

    ERROR("errorMessages"),
    NORMAL("messages"),
    CONFIG("autoClean");

    public final String key;
}
