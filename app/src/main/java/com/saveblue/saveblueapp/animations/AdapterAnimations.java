package com.saveblue.saveblueapp.animations;

import android.view.View;

public class AdapterAnimations {

    // Animation for rotating arrow
    public static void toggleArrow(View view, boolean isExpanded) {

        if (isExpanded) {
            view.animate().setDuration(200).rotation(180);
        } else {
            view.animate().setDuration(200).rotation(0);
        }
    }

}
