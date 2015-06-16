package com.treefrogapps.TaDo;


import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Animations {

    public static Animation animation;

    public static Animation alphaMoveInAnim(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.alpha_move_in_anim);
    }

    public static Animation alphaMoveOutAnim(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.alpha_move_out_anim);
    }

    public static Animation floatingActionButtonAnim(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.fab_anim);
    }

    public static Animation alphaFadeOutAndIn(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.alpha_fade_out_and_in);
    }

    public static Animation moveOut(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.move_out_anim);
    }
}

