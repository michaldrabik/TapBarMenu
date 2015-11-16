package com.michaldrabik.tapbarmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * @author Michal Drabik (michal.drabik0@gmail.com) on 2015-11-13.
 */
public class TapBarMenu extends LinearLayout {

  private static final int DURATION = 500;
  private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator(2.5f);

  private static final int LEFT_ANIMATOR_ID = 0;
  private static final int RIGHT_ANIMATOR_ID = 1;
  private static final int RADIUS_ANIMATOR_ID = 2;

  private enum State {
    OPENED,
    CLOSED
  }

  private State state = State.CLOSED;
  private Paint paint;
  private ValueAnimator leftAnimator;
  private ValueAnimator rightAnimator;
  private ValueAnimator radiusAnimator;
  private AnimatorSet animatorSet;
  private float width;
  private float height;
  private float buttonLeft;
  private float buttonRight;
  private float buttonLeftInitial;
  private float buttonRightInitial;
  private float radius;
  private float iconLeft;
  private float iconRight;
  private float iconTop;
  private float iconBottom;
  private float yPosition;
  private OnClickListener onClickListener;

  private AnimatedVectorDrawable iconDrawable;

  public TapBarMenu(Context context) {
    super(context);
    init();
  }

  public TapBarMenu(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public TapBarMenu(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setWillNotDraw(false);
    setGravity(Gravity.CENTER);
    setupAnimators();
    paint = new Paint();
    paint.setColor(0xFFE55452);
    paint.setAntiAlias(true);
    paint.setShadowLayer(4, 2, 2, ContextCompat.getColor(getContext(), R.color.dark_gray));
    iconDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.icon_animated, null);
  }

  private void setupAnimators() {
    leftAnimator = new ValueAnimator();
    rightAnimator = new ValueAnimator();
    radiusAnimator = new ValueAnimator();
    animatorSet = new AnimatorSet();
    animatorSet.setDuration(DURATION);
    animatorSet.setInterpolator(DECELERATE_INTERPOLATOR);
  }

  private void setupMenuItems() {
    for (int i = 0; i < getChildCount(); i++) {
      getChildAt(i).setVisibility(GONE);
    }
  }

  public void setTapBarMenuBackgroundColor(int colorResId) {
    paint.setColor(ContextCompat.getColor(getContext(), colorResId));
  }

  public void toggle() {
    if (state == State.OPENED) {
      close();
    } else {
      open();
    }
  }

  public void open() {
    state = State.OPENED;
    leftAnimator = ValueAnimator.ofFloat(buttonLeft, 0);
    rightAnimator = ValueAnimator.ofFloat(buttonRight, width);
    radiusAnimator = ValueAnimator.ofFloat(radius, 0);
    setAnimatorsUpdateListeners();
    showIcons(true);

    animatorSet.playTogether(radiusAnimator, leftAnimator, rightAnimator);
    animatorSet.start();
    iconDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.icon_animated, null);
    iconDrawable.start();
    ViewGroup parentView = (ViewGroup) TapBarMenu.this.getParent();
    this.animate().y(parentView.getBottom() - height).setDuration(DURATION).setInterpolator(DECELERATE_INTERPOLATOR).start();
  }

  public void close() {
    state = State.CLOSED;
    showIcons(false);
    updateDimensions(width, height);
    leftAnimator = ValueAnimator.ofFloat(0, buttonLeft);
    rightAnimator = ValueAnimator.ofFloat(width, buttonRight);
    radiusAnimator = ValueAnimator.ofFloat(0, radius);
    setAnimatorsUpdateListeners();

    animatorSet.playTogether(radiusAnimator, leftAnimator, rightAnimator);
    animatorSet.removeAllListeners();
    animatorSet.start();
    iconDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.icon_close_animated, null);
    iconDrawable.start();
    this.animate().y(yPosition).setDuration(DURATION).setInterpolator(DECELERATE_INTERPOLATOR).start();
  }

  public boolean isOpened() {
    return state == State.OPENED;
  }

  @Override public void setOnClickListener(OnClickListener listener) {
    onClickListener = listener;
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    yPosition = getY();
    updateDimensions(w, h);
    setupMenuItems();
  }

  @Override protected void onDraw(Canvas canvas) {
    canvas.drawRoundRect(buttonLeft, 0, buttonRight, height, radius, radius, paint);
    iconDrawable.setBounds((int) iconLeft, (int) iconTop, (int) iconRight, (int) iconBottom);
    iconDrawable.draw(canvas);
  }

  private void updateDimensions(float w, float h) {
    width = w;
    height = h;
    radius = h;
    buttonLeft = ((w / 2) - (h / 2)) + getPaddingLeft();
    buttonRight = buttonLeft + h;
    buttonLeftInitial = buttonLeft;
    buttonRightInitial = buttonRight;
    iconLeft = buttonLeft + h / 3;
    iconTop = h / 3;
    iconRight = buttonRight - h / 3;
    iconBottom = h - h / 3;
  }

  private void setAnimatorsUpdateListeners() {
    leftAnimator.addUpdateListener(createAnimatorUpdateListener(LEFT_ANIMATOR_ID));
    rightAnimator.addUpdateListener(createAnimatorUpdateListener(RIGHT_ANIMATOR_ID));
    radiusAnimator.addUpdateListener(createAnimatorUpdateListener(RADIUS_ANIMATOR_ID));
  }

  private void showIcons(final boolean show) {
    for (int i = 0; i < getChildCount(); i++) {
      final View view = getChildAt(i);
      view.setTranslationY(show ? view.getHeight() : 0f);
      view.setScaleX(show ? 0f : 1f);
      view.setScaleY(show ? 0f : 1f);
      view.setVisibility(VISIBLE);
      view.setAlpha(show ? 0f : 1f);
      view.animate()
          .scaleX(show ? 1f : 0f)
          .scaleY(show ? 1f : 0f)
          .translationY(0f)
          .alpha(show ? 1f : 0f)
          .setInterpolator(DECELERATE_INTERPOLATOR)
          .setDuration(show ? DURATION / 2 : DURATION / 3)
          .setStartDelay(show ? DURATION / 4 : 0)
          .setListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
              super.onAnimationEnd(animation);
              view.setVisibility(show ? VISIBLE : GONE);
            }
          })
          .start();
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if ((event.getX() > buttonLeftInitial && event.getX() < buttonRightInitial) && (event.getAction() == MotionEvent.ACTION_UP)) {
      if (onClickListener != null) {
        onClickListener.onClick(this);
      }
    }
    return true;
  }

  @Override protected void onDetachedFromWindow() {
    onDestroy();
    super.onDetachedFromWindow();
  }

  private void onDestroy() {
    iconDrawable = null;
    leftAnimator = null;
    rightAnimator = null;
    radiusAnimator = null;
    onClickListener = null;
  }

  @NonNull private ValueAnimator.AnimatorUpdateListener createAnimatorUpdateListener(final int type) {
    return new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        switch (type) {
          case LEFT_ANIMATOR_ID:
            buttonLeft = (float) valueAnimator.getAnimatedValue();
            break;
          case RIGHT_ANIMATOR_ID:
            buttonRight = (float) valueAnimator.getAnimatedValue();
            break;
          case RADIUS_ANIMATOR_ID:
            radius = (float) valueAnimator.getAnimatedValue();
            break;
        }
        invalidate();
      }
    };
  }
}
