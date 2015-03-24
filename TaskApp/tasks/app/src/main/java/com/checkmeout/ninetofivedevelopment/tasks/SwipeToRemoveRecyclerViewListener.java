package com.checkmeout.ninetofivedevelopment.tasks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by milord on 22-Mar-15.
 */
public class SwipeToRemoveRecyclerViewListener implements View.OnTouchListener {
    private int slop, minFlingVelocity, maxFlingVelocity;
    private long animTime;

    private RecyclerView recyclerView;
    private RemoveCallBacks callBacks;
    private int viewWidth = 1;

    private List<PendingRemoveData> pendingRemoveDataList = new ArrayList<PendingRemoveData>();
    private int removeAnimationCount = 0;
    private float downX, downY;
    private boolean swiping;
    private int swipingSlop;
    private VelocityTracker velocityTracker;
    private int downPosition;
    private View downView;
    private boolean paused;

    public interface RemoveCallBacks {
        boolean canRemove(int position);

        void onRemove(RecyclerView recyclerView, int[] reverseSortedPositions);
    }

    public SwipeToRemoveRecyclerViewListener(RecyclerView recyclerView, RemoveCallBacks callBacks) {
        ViewConfiguration vc = ViewConfiguration.get(recyclerView.getContext());
        slop = vc.getScaledEdgeSlop();
        minFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        animTime = recyclerView.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        this.recyclerView = recyclerView;
        this.callBacks = callBacks;
    }

    public void setEnabled(boolean enabled) {
        paused = !enabled;
    }

    public RecyclerView.OnScrollListener makeScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                setEnabled(newState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        };
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (viewWidth < 2) {
            viewWidth = this.recyclerView.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (paused)
                    return false;

                Rect rect = new Rect();
                int childCount = this.recyclerView.getChildCount();
                int[] listViewCoords = new int[2];
                this.recyclerView.getLocationOnScreen(listViewCoords);
                int x = (int) motionEvent.getRawX() - listViewCoords[0];
                int y = (int) motionEvent.getRawY() - listViewCoords[1];
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = this.recyclerView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        downView = child;
                        break;
                    }
                }

                if (downView != null) {
                    downX = motionEvent.getRawX();
                    downY = motionEvent.getRawY();
                    downPosition = this.recyclerView.getChildPosition(downView);
                    if (this.callBacks.canRemove(downPosition)) {
                        velocityTracker = VelocityTracker.obtain();
                        velocityTracker.addMovement(motionEvent);
                    }
                    else {
                        downView = null;
                    }
                }
                return false;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (velocityTracker == null)
                    break;

                if (this.downView != null && swiping) {
                    this.downView.animate().translationX(0).alpha(1).setDuration(animTime).setListener(null);
                }
                velocityTracker.recycle();
                velocityTracker = null;
                downX = 0;
                downY = 0;
                this.downView = null;
                downPosition = ListView.INVALID_POSITION;
                swiping = false;

                break;
            }

            case MotionEvent.ACTION_UP: {
                if (velocityTracker == null)
                    break;

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

                if (remove && downPosition != ListView.INVALID_POSITION) {
                    final View downView = this.downView;
                    final int downPosition = this.downPosition;
                    ++removeAnimationCount;
                    downView.animate().translationX(removeRight ? viewWidth : -viewWidth).alpha(0)
                            .setDuration(animTime).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    performRemove(downView, downPosition);
                                }
                            });
                } else {
                    this.downView.animate().translationX(0).alpha(1).setDuration(animTime).setListener(null);
                }

                velocityTracker.recycle();
                velocityTracker = null;
                downX = 0;
                downY = 0;
                this.downView = null;
                downPosition = ListView.INVALID_POSITION;
                swiping = false;

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (velocityTracker == null || paused) {
                    break;
                }

                velocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - downX;
                float deltaY = motionEvent.getRawY() - downY;

                if (Math.abs(deltaX) > slop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
                    swiping = true;
                    swipingSlop  = (deltaX > 0 ? slop : -slop);
                    this.recyclerView.requestDisallowInterceptTouchEvent(true);

                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    this.recyclerView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (swiping) {
                    downView.setTranslationX(deltaX - swipingSlop);
                    downView.setAlpha(Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaX) / viewWidth)));
                    return true;
                }

                break;
            }
        }
        return false;
    }

    class PendingRemoveData implements Comparable<PendingRemoveData> {
        public int position;
        public View view;

        public PendingRemoveData(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public int compareTo(PendingRemoveData other) {
            return other.position - position;
        }
    }

    private void performRemove(final View removeView, final int removePosition) {
        final ViewGroup.LayoutParams params = removeView.getLayoutParams();
        final int ogHeight = removeView.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(ogHeight, 1).setDuration(animTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                --removeAnimationCount;

                if (removeAnimationCount == 0) {
                    Collections.sort(pendingRemoveDataList);

                    int[] removePositions = new int[pendingRemoveDataList.size()];
                    for (int i = pendingRemoveDataList.size() - 1; i >= 0; i--) {
                        removePositions[i] = pendingRemoveDataList.get(i).position;
                    }
                    callBacks.onRemove(recyclerView, removePositions);

                    downPosition = ListView.INVALID_POSITION;

                    ViewGroup.LayoutParams params;
                    for (PendingRemoveData pendingRemoveData : pendingRemoveDataList) {
                        pendingRemoveData.view.setAlpha(1f);
                        pendingRemoveData.view.setTranslationX(0);
                        params = pendingRemoveData.view.getLayoutParams();
                        params.height = ogHeight;
                        pendingRemoveData.view.setLayoutParams(params);
                    }

                    long time = SystemClock.uptimeMillis();
                    MotionEvent cancelEvent = MotionEvent.obtain(time, time, MotionEvent.ACTION_CANCEL, 0, 0, 0);
                    recyclerView.dispatchTouchEvent(cancelEvent);

                    pendingRemoveDataList.clear();
                }
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                params.height = (Integer) valueAnimator.getAnimatedValue();
                removeView.setLayoutParams(params);
            }
        });

        pendingRemoveDataList.add(new PendingRemoveData(removePosition, removeView));
        animator.start();
    }
}
