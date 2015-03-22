package com.checkmeout.ninetofivedevelopment.tasks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by milord on 22-Mar-15.
 */
/*public class SwipeToRemoveListener implements View.OnTouchListener {

    private int slop, minFlingVelocity, maxFlingVelocity;
    private long animTime;

    private View view;
    private RemoveCallbacks callBacks;
    private int viewWidth = 1;

    private float downX, downY;
    private boolean swiping;
    private int swipingSlop;
    private Object token;
    private VelocityTracker velocityTracker;
    private float translationX;

    public interface RemoveCallbacks {
        boolean canRemove(Object token);

        void onRemove(View view, Object token);
    }

    public SwipeToRemoveListener(View view, Object token, RemoveCallbacks callBacks) {
        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        slop = vc.getScaledEdgeSlop();
        minFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        animTime = view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        this.view = view;
        this.token = token;
        this.callBacks = callBacks;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        motionEvent.offsetLocation(translationX, 0);

        if (viewWidth < 2) {
            viewWidth = this.view.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                downX = motionEvent.getRawX();
                downY = motionEvent.getRawY();
                if (this.callBacks.canRemove(this.token)) {
                    velocityTracker = VelocityTracker.obtain();
                    velocityTracker.addMovement(motionEvent);
                }
                return false;
            }

            case MotionEvent.ACTION_UP: {
                if (velocityTracker == null) {
                    break;
                }

                float deltaX = motionEvent.getRawX() - downX;
                velocityTracker.addMovement(motionEvent);
                velocityTracker.computeCurrentVelocity(1000);
                float velocityX = velocityTracker.getXVelocity();
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(velocityTracker.getYVelocity());
                boolean remove = false;
                boolean removeRight = false;

                if (Math.abs(deltaX) > viewWidth / 2 && swiping) {
                    remove = true;
                    removeRight = deltaX > 0;
                }
                else if (minFlingVelocity <= absVelocityX && absVelocityX <= maxFlingVelocity
                        && absVelocityY < absVelocityX && swiping) {
                    remove = (velocityX < 0) == (deltaX < 0);
                    removeRight = velocityTracker.getXVelocity() > 0;
                }

                if (remove) {
                    this.view.animate().translationX(removeRight ? viewWidth : -viewWidth).alpha(0)
                            .setDuration(animTime).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            performRemove();
                        }
                    });
                }
                else if (swiping) {
                    this.view.animate().translationX(0).alpha(1).setDuration(animTime).setListener(null);
                }
                velocityTracker.recycle();
                velocityTracker = null;
                translationX = 0;
                downX = 0;
                downY = 0;
                swiping = false;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (velocityTracker == null) {
                    break;
                }

                this.view.animate().translationX(0).alpha(1).setDuration(animTime).setListener(null);
                velocityTracker.recycle();
                velocityTracker = null;
                translationX = 0;
                downX = 0;
                downY = 0;
                swiping = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (velocityTracker == null) {
                    break;
                }

                velocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - downX;
                float deltaY = motionEvent.getRawY() - downY;

                if (Math.abs(deltaX) > this.slop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
                    swiping = true;
                    swipingSlop = (deltaX > 0 ? this.slop : -this.slop);
                    this.view.getParent().requestDisallowInterceptTouchEvent(true);

                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex() <<
                                MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    this.view.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (swiping) {
                    translationX = deltaX;
                    this.view.setTranslationX(deltaX - swipingSlop);
                    this.view.setAlpha(Math.max(0f, Math.min(1f, 1f- 2f * Math.abs(deltaX) / this.viewWidth)));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private void performRemove() {
        final ViewGroup.LayoutParams params = this.view.getLayoutParams();
        final int ogHeight = this.view.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(ogHeight, 1).setDuration(animTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callBacks.onRemove(view, token);
                view.setAlpha(1f);
                view.setTranslationX(0);
                params.height = ogHeight;
                view.setLayoutParams(params);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                params.height = (Integer) valueAnimator.getAnimatedValue();
                view.setLayoutParams(params);
            }
        });

        animator.start();
    }
}*/
