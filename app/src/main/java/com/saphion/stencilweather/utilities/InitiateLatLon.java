package com.saphion.stencilweather.utilities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.activities.MapActivity;

import io.codetail.animation.SupportAnimator;


public class InitiateLatLon {

    public static void handleToolBar(final Activity activity, final View search, final Toolbar toolbarMain, final EditText etLat, final EditText etLon, final View background, MaterialMenuDrawable materialMenu) {
        final Animation fade_in = AnimationUtils.loadAnimation(activity.getApplicationContext(), android.R.anim.fade_in);
        if (search.getVisibility() == View.VISIBLE) {
            etLat.setText("");
            etLon.setText("");
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animatorHide = ViewAnimationUtils.createCircularReveal(search,
                    search.getWidth() - (int) convertDpToPixel(56, activity),
                    (int) convertDpToPixel(23, activity),
                    (float) Math.hypot(search.getWidth(), search.getHeight()),
                    0);
                animatorHide.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        search.setVisibility(View.GONE);
                        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(search.getWindowToken(), 0);
                        background.setVisibility(View.GONE);
                        toolbarMain.setTitle("Weather Map");
                        toolbarMain.getMenu().clear();
                        toolbarMain.inflateMenu(R.menu.map_actions);
                        ((MapActivity)activity).setMenuSelection();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animatorHide.setDuration(300);
                animatorHide.start();
            } else {

                final SupportAnimator animatorHide = io.codetail.animation.ViewAnimationUtils.createCircularReveal(search,
                        search.getWidth() - (int) convertDpToPixel(56, activity),
                        (int) convertDpToPixel(23, activity),
                        (float) Math.hypot(search.getWidth(), search.getHeight()),
                        0);
                animatorHide.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        search.setVisibility(View.GONE);
                        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(search.getWindowToken(), 0);
                        background.setVisibility(View.GONE);
                        toolbarMain.setTitle("Weather Map");
                        toolbarMain.getMenu().clear();
                        toolbarMain.inflateMenu(R.menu.map_actions);
                        ((MapActivity)activity).setMenuSelection();
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animatorHide.setDuration(300);
                animatorHide.start();

            }


        } else {
            toolbarMain.getMenu().clear();
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.X);
            toolbarMain.setTitle("Search");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animator = ViewAnimationUtils.createCircularReveal(search,
                    search.getWidth() - (int) convertDpToPixel(56, activity),
                    (int) convertDpToPixel(23, activity),
                    0,
                    (float) Math.hypot(search.getWidth(), search.getHeight()));
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        fade_in.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                search.setVisibility(View.VISIBLE);
                if (search.getVisibility() == View.VISIBLE) {
                    animator.setDuration(300);
                    animator.start();
                    search.setEnabled(true);
                }
                fade_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        background.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                final SupportAnimator animator = io.codetail.animation.ViewAnimationUtils.createCircularReveal(search,
                        search.getWidth() - (int) convertDpToPixel(56, activity),
                        (int) convertDpToPixel(23, activity),
                        0,
                        (float) Math.hypot(search.getWidth(), search.getHeight()));
                animator.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                search.setVisibility(View.VISIBLE);
                if (search.getVisibility() == View.VISIBLE) {
                    animator.setDuration(300);
                    animator.start();
                    search.setEnabled(true);
                }
                fade_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        background.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            etLat.requestFocus();
        }
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }
}