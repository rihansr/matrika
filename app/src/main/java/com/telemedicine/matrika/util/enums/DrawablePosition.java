package com.telemedicine.matrika.util.enums;

public enum DrawablePosition {

    LEFT(0), TOP( 1), RIGHT(2), BOTTOM(3);

    private int     action;

    DrawablePosition(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }
}
