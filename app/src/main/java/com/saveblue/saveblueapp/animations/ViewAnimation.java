package com.saveblue.saveblueapp.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class ViewAnimation {

    // Main FAB rotation animation
    public static boolean rotateFab(final View v, boolean rotate) {
        v.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 135f : 0f);
        return rotate;
    }

    // Show sub-FABs animation
    public static void showIn(final View v) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(0f);
        v.setTranslationY(v.getHeight());
        v.animate()
                .setDuration(200)
                .translationY(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1f)
                .start();
    }

    // Hide sub-FABs animation
    public static void showOut(final View v) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(1f);
        v.setTranslationY(0);
        v.animate()
                .setDuration(200)
                .translationY(v.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.INVISIBLE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(0f)
                .start();
    }

    // Initial settings for sub-FABs animations
    public static void init(final View v) {
        v.setVisibility(View.INVISIBLE);
        v.setTranslationY(v.getHeight());
        v.setAlpha(0f);
    }
}
