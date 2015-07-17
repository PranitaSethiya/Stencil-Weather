package com.saphion.stencilweather.utilities;

import android.animation.Animator;
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
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.saphion.stencilweather.R;


public class InitiateSearch {

    public static void handleToolBar(final Context context, final CardView search, final Toolbar toolbarMain, /*final View view,*/ final ListView listView, final EditText editText, final String title) {
        final Animation fade_in = AnimationUtils.loadAnimation(context.getApplicationContext(), android.R.anim.fade_in);
//        final Animation fade_out = AnimationUtils.loadAnimation(context.getApplicationContext(), android.R.anim.fade_out);
        if (search.getVisibility() == View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animatorHide = ViewAnimationUtils.createCircularReveal(search,
                    search.getWidth() - (int) convertDpToPixel(56, context),
                    (int) convertDpToPixel(23, context),
                    (float) Math.hypot(search.getWidth(), search.getHeight()),
                    0);
                animatorHide.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        view.startAnimation(fade_out);
//                        view.setVisibility(View.INVISIBLE);
                        search.setVisibility(View.GONE);
                        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(search.getWindowToken(), 0);
                        listView.setVisibility(View.GONE);
                        toolbarMain.setNavigationIcon(R.drawable.ic_menu);
                        toolbarMain.setTitle(title);
                        toolbarMain.getMenu().clear();
                        toolbarMain.inflateMenu(R.menu.menu_add);
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
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(search.getWindowToken(), 0);
//                view.startAnimation(fade_out);
//                view.setVisibility(View.INVISIBLE);

                YoYo.with(Techniques.SlideOutRight)
                        .duration(500).interpolate(new BounceInterpolator()).withListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                        search.setVisibility(View.GONE);
                        editText.setText("");
                        toolbarMain.setNavigationIcon(R.drawable.ic_menu);
                        toolbarMain.setTitle(title);
                        toolbarMain.getMenu().clear();
                        toolbarMain.inflateMenu(R.menu.menu_add);
                        search.setEnabled(false);

                    }

                    @Override
                    public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

                    }
                }).playOn(search);
            }


        } else {
            toolbarMain.setTitle("");
            toolbarMain.getMenu().clear();
            toolbarMain.setNavigationIcon(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animator = ViewAnimationUtils.createCircularReveal(search,
                    search.getWidth() - (int) convertDpToPixel(56, context),
                    (int) convertDpToPixel(23, context),
                    0,
                    (float) Math.hypot(search.getWidth(), search.getHeight()));
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        view.setVisibility(View.VISIBLE);
//                        view.startAnimation(fade_in);
                        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
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
                        listView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                search.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInRight)
                        .duration(500).interpolate(new BounceInterpolator())
                        .playOn(search);
                search.setEnabled(true);
                listView.setVisibility(View.VISIBLE);
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }

            editText.requestFocus();
        }
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }
}