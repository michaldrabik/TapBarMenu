package com.michaldrabik.tapbarmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
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

  private static final int ANIMATION_DURATION = 500;
  private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator(2.5f);
  private static final int BUTTON_POSITION_LEFT = 0;
  private static final int BUTTON_POSITION_CENTER = 1;
  private static final int BUTTON_POSITION_RIGHT = 2;

  private enum State {
    OPENED,
    CLOSED
  }

  private State state = State.CLOSED;
  private Paint paint;
  private ValueAnimator leftAnimator;
  private ValueAnimator rightAnimator;
  private ValueAnimator topAnimator;
  private ValueAnimator bottomAnimator;
  private ValueAnimator radiusAnimator;
  private AnimatorSet animatorSet;
  private float width;
  private float height;
  private float buttonLeft;
  private float buttonRight;
  private float buttonTop;
  private float buttonBottom;
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

  private int backgroundColor;
  private int buttonSize;
  private int buttonOpenPosition;
  private int buttonPaddingRight;
  private int buttonPaddingLeft;

  public TapBarMenu(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public TapBarMenu(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    setWillNotDraw(false);
    setupAttributes(attrs);
    setGravity(Gravity.CENTER);
    setupAnimators();
    setupPaint();
    iconOpenDrawable = ResourcesCompat.getDrawable(getContext().getApplicationContext(), R.drawable.icon_animated);
    iconCloseDrawable = ResourcesCompat.getDrawable(getContext().getApplicationContext(), R.drawable.icon_close_animated);
  }

  private void setupAttributes(AttributeSet attrs) {
    TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TapBarMenu, 0, 0);
    backgroundColor = typedArray.getColor(R.styleable.TapBarMenu_tbm_backgroundColor, ContextCompat.getColor(getContext(), R.color.red));
    buttonSize = typedArray.getDimensionPixelSize(R.styleable.TapBarMenu_tbm_buttonSize, getResources().getDimensionPixelSize(R.dimen.defaultButtonSize));
    buttonPaddingRight = typedArray.getDimensionPixelSize(R.styleable.TapBarMenu_tbm_buttonPaddingRight, 0);
    buttonPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.TapBarMenu_tbm_buttonPaddingLeft, 0);
    buttonOpenPosition = typedArray.getInt(R.styleable.TapBarMenu_tbm_buttonOpenPosition, BUTTON_POSITION_CENTER);
    typedArray.recycle();
  }

  private void setupAnimators() {
    leftAnimator = new ValueAnimator();
    rightAnimator = new ValueAnimator();
    topAnimator = new ValueAnimator();
    bottomAnimator = new ValueAnimator();
    radiusAnimator = new ValueAnimator();
    animatorSet = new AnimatorSet();
    animatorSet.setDuration(ANIMATION_DURATION);
    animatorSet.setInterpolator(DECELERATE_INTERPOLATOR);
  }

  private void setupMenuItems() {
    for (int i = 0; i < getChildCount(); i++) {
      getChildAt(i).setVisibility(GONE);
    }
  }

  private void setupPaint() {
    paint = new Paint();
    paint.setColor(backgroundColor);
    paint.setAntiAlias(true);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    setupMenuItems();
  }

  public void setTapBarMenuBackgroundColor(int colorResId) {
    backgroundColor = ContextCompat.getColor(getContext(), colorResId);
    paint.setColor(backgroundColor);
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
    showIcons(true);
    leftAnimator = ValueAnimator.ofFloat(buttonLeft, 0);
    rightAnimator = ValueAnimator.ofFloat(buttonRight, width);
    radiusAnimator = ValueAnimator.ofFloat(radius, 0);
    topAnimator = ValueAnimator.ofFloat(buttonTop, 0);
    bottomAnimator = ValueAnimator.ofFloat(buttonBottom, height);
    setAnimatorsUpdateListeners();

    animatorSet.playTogether(radiusAnimator, leftAnimator, rightAnimator, topAnimator, bottomAnimator);
    animatorSet.start();
    ((Animatable) iconOpenDrawable).start();
    ViewGroup parentView = (ViewGroup) TapBarMenu.this.getParent();
    this.animate().y(parentView.getBottom() - height).setDuration(ANIMATION_DURATION).setInterpolator(DECELERATE_INTERPOLATOR).start();
  }

  public void close() {
    updateDimensions(width, height);
    state = State.CLOSED;
    showIcons(false);
    leftAnimator = ValueAnimator.ofFloat(0, buttonLeft);
    rightAnimator = ValueAnimator.ofFloat(width, buttonRight);
    radiusAnimator = ValueAnimator.ofFloat(0, radius);
    topAnimator = ValueAnimator.ofFloat(0, buttonTop);
    bottomAnimator = ValueAnimator.ofFloat(height, buttonBottom);
    setAnimatorsUpdateListeners();

    animatorSet.playTogether(radiusAnimator, leftAnimator, rightAnimator, topAnimator, bottomAnimator);
    animatorSet.removeAllListeners();
    animatorSet.start();
    ((Animatable) iconCloseDrawable).start();
    this.animate().y(yPosition).setDuration(ANIMATION_DURATION).setInterpolator(DECELERATE_INTERPOLATOR).start();
  }

  public boolean isOpened() {
    return state == State.OPENED;
  }

  @Override public void setOnClickListener(OnClickListener listener) {
    onClickListener = listener;
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    updateDimensions(w, h);
    yPosition = getY();
  }

  @Override protected void onDraw(Canvas canvas) {
    canvas.drawPath(createRoundedRectPath(buttonLeft, buttonTop, buttonRight, buttonBottom, radius, radius, false),
        paint);
    if (state == State.CLOSED) {
      iconCloseDrawable.draw(canvas);
    } else {
      iconOpenDrawable.draw(canvas);
    }
  }

  private void updateDimensions(float w, float h) {
    width = w;
    height = h;
    radius = buttonSize;
    setButtonOpenPosition(w, h);
    iconLeft = buttonLeft + buttonSize / 3;
    iconTop = (height - buttonSize) / 2 + buttonSize / 3;
    iconRight = buttonRight - buttonSize / 3;
    iconBottom =(height + buttonSize) / 2 - buttonSize / 3;
    iconOpenDrawable.setBounds((int) iconLeft, (int) iconTop, (int) iconRight, (int) iconBottom);
    iconCloseDrawable.setBounds((int) iconLeft, (int) iconTop, (int) iconRight, (int) iconBottom);
  }

  private void setButtonOpenPosition(float w, float h) {
    if (buttonOpenPosition == BUTTON_POSITION_CENTER) {
      buttonLeft = ((w / 2) - (buttonSize / 2));
    } else if (buttonOpenPosition == BUTTON_POSITION_LEFT) {
      buttonLeft = 0;
    } else {
      buttonLeft = w - buttonSize;
    }
    int padding = buttonPaddingLeft - buttonPaddingRight;
    buttonLeft += padding;
    buttonRight = buttonLeft + buttonSize;
    buttonTop = (height - buttonSize) / 2;
    buttonBottom =  (height + buttonSize) / 2;
    buttonLeftInitial = buttonLeft;
    buttonRightInitial = buttonRight;
  }

  private void setAnimatorsUpdateListeners() {
    leftAnimator.addUpdateListener(leftAnimatorUpdateListener);
    rightAnimator.addUpdateListener(rightAnimatorUpdateListener);
    topAnimator.addUpdateListener(topAnimatorUpdateListener);
    bottomAnimator.addUpdateListener(bottomAnimatorUpdateListener);
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
          .setDuration(show ? ANIMATION_DURATION / 2 : ANIMATION_DURATION / 3)
          .setStartDelay(show ? ANIMATION_DURATION / 4 : 0)
          .setListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
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

  @Override public boolean onTouchEvent(@NonNull MotionEvent event) {
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
    iconOpenDrawable = null;
    iconCloseDrawable = null;
    leftAnimator = null;
    rightAnimator = null;
    radiusAnimator = null;
    topAnimator = null;
    bottomAnimator = null;
    animatorSet = null;
    leftAnimatorUpdateListener = null;
    rightAnimatorUpdateListener = null;
    radiusAnimatorUpdateListener = null;
    topAnimatorUpdateListener = null;
    bottomAnimatorUpdateListener = null;
    onClickListener = null;
  }

  private ValueAnimator.AnimatorUpdateListener leftAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
      buttonLeft = (float) valueAnimator.getAnimatedValue();
    }
  };

  private ValueAnimator.AnimatorUpdateListener rightAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
      buttonRight = (float) valueAnimator.getAnimatedValue();
    }
  };

  private ValueAnimator.AnimatorUpdateListener topAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
      buttonTop = (float) valueAnimator.getAnimatedValue();
    }
  };

  private ValueAnimator.AnimatorUpdateListener bottomAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
      buttonBottom = (float) valueAnimator.getAnimatedValue();
    }
  };

  private ValueAnimator.AnimatorUpdateListener radiusAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
    @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
      radius = (float) valueAnimator.getAnimatedValue();
      invalidate();
    }
  };
}
