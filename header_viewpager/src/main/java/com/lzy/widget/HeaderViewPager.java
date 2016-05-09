package com.lzy.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class HeaderViewPager extends LinearLayout {

    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = 2;

    private int topOffset = 0;      //滚动的最大偏移量

    private Scroller mScroller;
    private int mTouchSlop;         //表示滑动的时候，手的移动要大于这个距离才开始移动控件。
    private int mMinimumVelocity;   //允许执行一个fling手势动作的最小速度值
    private int mMaximumVelocity;   //允许执行一个fling手势动作的最大速度值
    private int sysVersion;         //当前sdk版本，用于判断api版本
    private View mHeadView;         //需要被滑出的头部
    private int mHeadHeight;        //滑出头部的高度
    private int maxY = 0;           //最大滑出的距离，等于 mHeadHeight
    private int minY = 0;           //最小的距离， 头部在最顶部
    private int mCurY;              //当前已经滚动的距离
    private VelocityTracker mVelocityTracker;
    private int mDirection;
    private int mLastScrollerY;
    private boolean mDisallowIntercept;  //是否允许拦截事件
    private boolean isClickHead;         //当前点击区域是否在头部
    private OnScrollListener onScrollListener;   //滚动的监听
    private HeaderScrollHelper mScrollable;

    public interface OnScrollListener {
        void onScroll(int currentY, int maxY);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public HeaderViewPager(Context context) {
        this(context, null);
    }

    public HeaderViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HeaderViewPager);
        topOffset = a.getDimensionPixelSize(a.getIndex(R.styleable.HeaderViewPager_hvp_topOffset), topOffset);
        a.recycle();

        mScroller = new Scroller(context);
        mScrollable = new HeaderScrollHelper();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();   //表示滑动的时候，手的移动要大于这个距离才开始移动控件。
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity(); //允许执行一个fling手势动作的最小速度值
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity(); //允许执行一个fling手势动作的最大速度值
        sysVersion = Build.VERSION.SDK_INT;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mHeadView != null && !mHeadView.isClickable()) {
            mHeadView.setClickable(true);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeadView = getChildAt(0);
        measureChildWithMargins(mHeadView, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
        mHeadHeight = mHeadView.getMeasuredHeight();
        maxY = mHeadHeight - topOffset;
        //让测量高度加上头部的高度
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + maxY, MeasureSpec.EXACTLY));
    }

    /** @param disallowIntercept 作用同 requestDisallowInterceptTouchEvent */
    public void requestHeaderViewPagerDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        mDisallowIntercept = disallowIntercept;
    }

    private float mDownX;  //第一次按下的x坐标
    private float mDownY;  //第一次按下的y坐标
    private float mLastY;  //最后一次移动的Y坐标
    private boolean verticalScrollFlag = false;   //是否允许垂直滚动

    /**
     * 说明：一旦dispatTouchEvent返回true，即表示当前View就是事件传递需要的 targetView，事件不会再传递给
     * 其他View，如果需要将事件继续传递给子View，可以手动传递
     * 由于dispatchTouchEvent处理事件的优先级高于子View，也高于onTouchEvent,所以在这里进行处理
     * 好处一：当有子View，并且子View可以获取焦点的时候，子View的onTouchEvent会优先处理，如果当前逻辑
     * 在onTouchEnent中，则事件无法到达，逻辑失效
     * 好处二：当子View是拥有滑动事件时，例如ListView，ScrollView等，不需要对子View的事件进行拦截，可以
     * 全部让该父控件处理，在需要的地方手动将事件传递给子View，保证滑动的流畅性，结尾两行代码就是证明：
     * super.dispatchTouchEvent(ev);
     * return true;
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentX = ev.getX();                   //当前手指相对于当前view的X坐标
        float currentY = ev.getY();                   //当前手指相对于当前view的Y坐标
        float shiftX = Math.abs(currentX - mDownX);   //当前触摸位置与第一次按下位置的X偏移量
        float shiftY = Math.abs(currentY - mDownY);   //当前触摸位置与第一次按下位置的Y偏移量
        float deltaY;                                 //滑动的偏移量，即连续两次进入Move的偏移量
        obtainVelocityTracker(ev);                    //初始化速度追踪器
        switch (ev.getAction()) {
            //Down事件主要初始化变量
            case MotionEvent.ACTION_DOWN:
                mDisallowIntercept = false;
                verticalScrollFlag = false;
                mDownX = currentX;
                mDownY = currentY;
                mLastY = currentY;
                checkIsClickHead((int) currentY, mHeadHeight, getScrollY());
                mScroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDisallowIntercept) break;
                deltaY = mLastY - currentY; //连续两次进入move的偏移量
                mLastY = currentY;
                if (shiftX > mTouchSlop && shiftX > shiftY) {
                    //水平滑动
                    verticalScrollFlag = false;
                } else if (shiftY > mTouchSlop && shiftY > shiftX) {
                    //垂直滑动
                    verticalScrollFlag = true;
                }
                /**
                 * 这里要注意，对于垂直滑动来说，给出以下三个条件
                 * 头部没有固定，允许滑动的View处于第一条可见，当前按下的点在头部区域
                 * 三个条件满足一个即表示需要滚动当前布局，否者不处理，将事件交给子View去处理
                 */
                if (verticalScrollFlag && (!isStickied() || mScrollable.isTop() || isClickHead)) {
                    //如果是向下滑，则deltaY小于0，对于scrollBy来说
                    //正值为向上和向左滑，负值为向下和向右滑，这里要注意
                    scrollBy(0, (int) (deltaY + 0.5));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (verticalScrollFlag) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity); //1000表示单位，每1000毫秒允许滑过的最大距离是mMaximumVelocity
                    float yVelocity = mVelocityTracker.getYVelocity();  //获取当前的滑动速度
                    mDirection = yVelocity > 0 ? DIRECTION_DOWN : DIRECTION_UP;  //下滑速度大于0，上滑速度小于0
                    //根据当前的速度和初始化参数，将滑动的惯性初始化到当前View，至于是否滑动当前View，取决于computeScroll中计算的值
                    //这里不判断最小速度，确保computeScroll一定至少执行一次
                    mScroller.fling(0, getScrollY(), 0, -(int) yVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                    mLastScrollerY = getScrollY();
                    invalidate();  //更新界面，该行代码会导致computeScroll中的代码执行
                    //阻止快读滑动的时候点击事件的发生，滑动的时候，将Up事件改为Cancel就不会发生点击了
                    if ((shiftX > mTouchSlop || shiftY > mTouchSlop)) {
                        if (isClickHead || !isStickied()) {
                            int action = ev.getAction();
                            ev.setAction(MotionEvent.ACTION_CANCEL);
                            boolean dd = super.dispatchTouchEvent(ev);
                            ev.setAction(action);
                            return dd;
                        }
                    }
                }
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        //手动将事件传递给子View，让子View自己去处理事件
        super.dispatchTouchEvent(ev);
        //消费事件，返回True表示当前View需要消费事件，就是事件的TargetView
        return true;
    }

    private void checkIsClickHead(int downY, int headHeight, int scrollY) {
        isClickHead = ((downY + scrollY) <= headHeight);
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            final int currY = mScroller.getCurrY();
            if (mDirection == DIRECTION_UP) {
                // 手势向上划
                if (isStickied()) {
                    //这里主要是将快速滚动时的速度对接起来，让布局看起来滚动连贯
                    int distance = mScroller.getFinalY() - currY;    //除去布局滚动消耗的时间后，剩余的时间
                    int duration = calcDuration(mScroller.getDuration(), mScroller.timePassed()); //除去布局滚动的距离后，剩余的距离
                    mScrollable.smoothScrollBy(getScrollerVelocity(distance, duration), distance, duration);
                    //外层布局已经滚动到指定位置，不需要继续滚动了
                    mScroller.abortAnimation();
                    return;
                } else {
                    scrollTo(0, currY);  //将外层布局滚动到指定位置
                    invalidate();        //移动完后刷新界面
                }
            } else {
                // 手势向下划，内部View已经滚动到顶了，需要滚动外层的View
                if (mScrollable.isTop() || isClickHead) {
                    int deltaY = (currY - mLastScrollerY);
                    int toY = getScrollY() + deltaY;
                    scrollTo(0, toY);
                    if (mCurY <= minY) {
                        mScroller.abortAnimation();
                        return;
                    }
                }
                //向下滑动时，初始状态可能不在顶部，所以要一直重绘，让computeScroll一直调用
                //确保代码能进入上面的if判断
                invalidate();
            }
            mLastScrollerY = currY;
        }
    }

    @SuppressLint("NewApi")
    private int getScrollerVelocity(int distance, int duration) {
        if (mScroller == null) {
            return 0;
        } else if (sysVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return (int) mScroller.getCurrVelocity();
        } else {
            return distance / duration;
        }
    }

    /** 对滑动范围做限制 */
    @Override
    public void scrollBy(int x, int y) {
        int scrollY = getScrollY();
        int toY = scrollY + y;
        if (toY >= maxY) {
            toY = maxY;
        } else if (toY <= minY) {
            toY = minY;
        }
        y = toY - scrollY;
        super.scrollBy(x, y);
    }

    /** 对滑动范围做限制 */
    @Override
    public void scrollTo(int x, int y) {
        if (y >= maxY) {
            y = maxY;
        } else if (y <= minY) {
            y = minY;
        }
        mCurY = y;
        if (onScrollListener != null) {
            onScrollListener.onScroll(y, maxY);
        }
        super.scrollTo(x, y);
    }

    /** 头部是否已经固定 */
    public boolean isStickied() {
        return mCurY == maxY;
    }

    private int calcDuration(int duration, int timepass) {
        return duration - timepass;
    }

    public int getMaxY() {
        return maxY;
    }

    public boolean isHeadTop() {
        return mCurY == minY;
    }

    /** 是否允许下拉，与PTR结合使用 */
    public boolean canPtr() {
        return verticalScrollFlag && mCurY == minY && mScrollable.isTop();
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
    }

    public void setCurrentScrollableContainer(HeaderScrollHelper.ScrollableContainer scrollableContainer) {
        mScrollable.setCurrentScrollableContainer(scrollableContainer);
    }
}