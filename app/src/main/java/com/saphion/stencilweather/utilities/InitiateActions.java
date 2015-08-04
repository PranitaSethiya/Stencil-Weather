package com.saphion.stencilweather.utilities;

import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by sachin on 3/8/15.
 */
public class InitiateActions {

    public static void initiateActions(final View actionsContainer){

        final boolean hidden = actionsContainer.getVisibility() == View.GONE;


        // get the center for the clipping circle
        int cx = actionsContainer.getRight();
        int cy = actionsContainer.getTop();

        // get the final radius for the clipping circle
        float finalRadius = hypo(actionsContainer.getWidth(), actionsContainer.getHeight());

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(actionsContainer, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);

        SupportAnimator animator_reverse = animator.reverse();

        if (hidden) {
            actionsContainer.setVisibility(View.VISIBLE);
            animator.start();
//            hidden = false;
        } else {
            animator_reverse.addListener(new SupportAnimator.AnimatorListener() {

                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    actionsContainer.setVisibility(View.INVISIBLE);
//                    hidden = true;
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            animator_reverse.start();
        }

    }

    static float hypo(int a, int b){
        return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

}
