package com.treefrogapps.TaDo;


import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Animations {

    public static Animation animation;

    public static Animation moveInAnimLeft(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.move_in_anim_left);
    }

    public static Animation moveOutAnimLeft(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.move_out_anim_left);
    }

    public static Animation moveInAnimRight(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.move_in_anim_right);
    }

    public static Animation moveOutAnimRight(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.move_out_anim_right);
    }

    public static Animation moveInAnimBottom(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.move_in_anim_bottom);
    }

    public static Animation alphaFadeIn(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.alpha_fade_in);
    }

    public static Animation alphaFadeOut(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.alpha_fade_out);
    }

    public static Animation floatingActionButtonAnim(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.fab_anim);
    }

    public static Animation moveOut(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.move_out_anim);
    }

    public static Animation scale1to0(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.scale_x_1_to_0);
    }

    public static Animation scale0to1(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.scale_x_0_to_1);
    }

    public static Animation scaleXY0to1(Context context) {
        return animation = AnimationUtils.loadAnimation(context, R.anim.scale_xy_0_to_1);
    }

    public static Animation rotate_infinite(Context context){
        return animation = AnimationUtils.loadAnimation(context, R.anim.progress_indeterminate);
    }
}

