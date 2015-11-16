package com.michaldrabik.tapbarmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import com.wnafee.vector.compat.ResourcesCompat;

/**
 * @author Michal Drabik (michal.drabik0@gmail.com) on 2015-11-13.
 */
public class TapBarMenu extends LinearLayout {

  private static final int DURATION = 500;
  private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator(2.5f);

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
  private Drawable iconOpenDrawable;
  private Drawable iconCloseDrawable;

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
    setupPaint();
    iconOpenDrawable = ResourcesCompat.getDrawable(getContext().getApplicationContext(), R.drawable.icon_animated);
    iconCloseDrawable = ResourcesCompat.getDrawable(getContext().getApplicationContext(), R.drawable.icon_close_animated);
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

  private void setupPaint() {
    paint = new Paint();
    paint.setColor(0xFFE55452);
    paint.setAntiAlias(true);
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
    ((Animatable) iconOpenDrawable).start();
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
    ((Animatable) iconCloseDrawable).start();
    this.animate().y(yPosition).setDuration(DURATION).setInterpolator(DECELERATE_INTERPOLATOR).start();
  }

  public boolean isOpened() {
    return state == State.OPENED;
  }

  @Override
  public void setOnClickListener(OnClickListener listener) {
    onClickListener = listener;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    updateDimensions(w, h);
    setupMenuItems();
    yPosition = getY();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.drawPath(createRoundedRectPath(buttonLeft, 0, buttonRight, height, radius, radius, false), paint);
    if (state == State.CLOSED) {
      iconCloseDrawable.draw(canvas);
    } else {
      iconOpenDrawable.draw(canvas);
    }
  }

  private void updateDimensions(float w, float h) {
    width = w;
    height = h;
    radius = h;
    buttonLeft = ((w / 2) - (h / 2)) + getPaddingLeft() - getPaddingRight();
    buttonRight = buttonLeft + h;
    buttonLeftInitial = buttonLeft;
    buttonRightInitial = buttonRight;
    iconLeft = buttonLeft + h / 3;
    iconTop = h / 3;
    iconRight = buttonRight - h / 3;
    iconBottom = h - h / 3;
    iconOpenDrawable.setBounds((int) iconLeft, (int) iconTop, (int) iconRight, (int) iconBottom);
    iconCloseDrawable.setBounds((int) iconLeft, (int) iconTop, (int) iconRight, (int) iconBottom);
  }

  private void setAnimatorsUpdateListeners() {
    leftAnimator.addUpdateListener(leftAnimatorUpdateListener);
    rightAnimator.addUpdateListener(rightAnimatorUpdateListener);
    radiusAnimator.addUpdateListener(radiusAnimatorUpdateListener);
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
            @Override
            public void onAnimationEnd(Animator animation) {
              super.onAnimationEnd(animation);
              view.setVisibility(show ? VISIBLE : GONE);
            }
          })
          .start();
    }
  }

  private Path createRoundedRectPath(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return createRoundedRectPathApi21(left, top, right, bottom, rx, ry, conformToOriginalPost);
    } else {
      return createRoundedRectPathPreApi21(left, top, right, bottom, rx, ry, conformToOriginalPost);
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private Path createRoundedRectPathApi21(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {
    Path path = new Path();
    if (rx < 0) rx = 0;
    if (ry < 0) ry = 0;
    float width = right - left;
    float height = bottom - top;
    if (rx > width / 2) rx = width / 2;
    if (ry > height / 2) ry = height / 2;
    float widthMinusCorners = (width - (2 * rx));
    float heightMinusCorners = (height - (2 * ry));
    path.moveTo(right, top + ry);
    path.arcTo(right - 2 * rx, top, right, top + 2 * ry, 0, -90, false);
    path.rLineTo(-widthMinusCorners, 0);
    path.arcTo(left, top, left + 2 * rx, top + 2 * ry, 270, -90, false);
    path.rLineTo(0, heightMinusCorners);
    if (conformToOriginalPost) {
      path.rLineTo(0, ry);
      path.rLineTo(width, 0);
      path.rLineTo(0, -ry);
    } else {
      path.arcTo(left, bottom - 2 * ry, left + 2 * rx, bottom, 180, -90, false);
      path.rLineTo(widthMinusCorners, 0);
      path.arcTo(right - 2 * rx, bottom - 2 * ry, right, bottom, 90, -90, false);
    }
    path.rLineTo(0, -heightMinusCorners);
    path.close();
    return path;
  }

  private Path createRoundedRectPathPreApi21(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {
    Path path = new Path();
    if (rx < 0) rx = 0;
    if (ry < 0) ry = 0;
    float width = right - left;
    float height = bottom - top;
    if (rx > width / 2) rx = width / 2;
    if (ry > height / 2) ry = height / 2;
    float widthMinusCorners = (width - (2 * rx));
    float heightMinusCorners = (height - (2 * ry));
    path.moveTo(right, top + ry);
    path.rQuadTo(0, -ry, -rx, -ry);
    path.rLineTo(-widthMinusCorners, 0);
    path.rQuadTo(-rx, 0, -rx, ry);
    path.rLineTo(0, heightMinusCorners);
    if (conformToOriginalPost) {
      path.rLineTo(0, ry);
      path.rLineTo(width, 0);
      path.rLineTo(0, -ry);
    } else {
      path.rQuadTo(0, ry, rx, ry);
      path.rLineTo(widthMinusCorners, 0);
      path.rQuadTo(rx, 0, rx, -ry);
    }
    path.rLineTo(0, -heightMinusCorners);
    path.close();
    return path;
  }

  @Override
  public boolean onTouchEvent(@NonNull MotionEvent event) {
    if ((event.getX() > buttonLeftInitial && event.getX() < buttonRightInitial) && (event.getAction() == MotionEvent.ACTION_UP)) {
      if (onClickListener != null) {
        onClickListener.onClick(this);
      }
    }
    return true;
  }

  @Override
  protected void onDetachedFromWindow() {
    onDestroy();
    super.onDetachedFromWindow();
  }

  private void onDestroy() {
    iconOpenDrawable = null;
    iconCloseDrawable = null;
    leftAnimator = null;
    rightAnimator = null;
    radiusAnimator = null;
    animatorSet = null;
    leftAnimatorUpdateListener = null;
    rightAnimatorUpdateListener = null;
    radiusAnimatorUpdateListener = null;
    onClickListener = null;
  }

  private ValueAnimator.AnimatorUpdateListener leftAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
      buttonLeft = (float) valueAnimator.getAnimatedValue();
    }
  };

  private ValueAnimator.AnimatorUpdateListener rightAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
      buttonRight = (float) valueAnimator.getAnimatedValue();
    }
  };

  private ValueAnimator.AnimatorUpdateListener radiusAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
      radius = (float) valueAnimator.getAnimatedValue();
      invalidate();
    }
  };
}
