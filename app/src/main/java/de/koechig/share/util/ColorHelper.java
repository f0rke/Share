package de.koechig.share.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import de.koechig.share.R;


/**
 * Created by Mumpi_000 on 09.05.2017.
 */

public class ColorHelper {

    private final Context mContext;

    private final int[] colorRes;

    public ColorHelper(Context context) {
        mContext = context;
        colorRes = new int[]{
                R.color.md_amber_500,
                R.color.md_blue_500,
                R.color.md_cyan_500,
                R.color.md_green_500,
                R.color.md_indigo_500,
                R.color.md_lime_500,
                R.color.md_orange_500,
                R.color.md_pink_500,
                R.color.md_purple_500,
                R.color.md_red_500,
                R.color.md_teal_500,
                R.color.md_yellow_500
        };
    }

    @ColorInt
    public int getRandomColor() {
        return ContextCompat.getColor(mContext, getRandomColorRes());
    }

    @ColorRes
    public int getRandomColorRes() {
        return colorRes[(int) (colorRes.length * Math.random())];
    }

    public ColorStateList getRandomColorStateList() {
        return ContextCompat.getColorStateList(mContext, getRandomColorRes());
    }

    public boolean isColorVeryLight(@ColorInt int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.3) {
            return true; // It's a light color
        } else {
            return false; // It's a dark color
        }
    }

    @ColorInt
    public int getColor(@ColorRes int color) {
        return ContextCompat.getColor(mContext, color);
    }

    @ColorInt
    public int getSuitableTextColor(@ColorInt int backgroundColor) {
        return isColorVeryLight(backgroundColor) ? getColor(R.color.text_dark) : getColor(R.color.text_light);
    }
}
