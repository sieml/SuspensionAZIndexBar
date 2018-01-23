package mcxtzhang.itemdecorationdemo.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created With Android Studio
 * Email: sielee@163.com
 * Author: Lee Sie
 * CopyRight: CL
 * <p>
 * Description: TODO
 * </p>
 */

public class InnerRecyclerVIew extends RecyclerView {
    private ViewGroup parent;
    private int mLastX, mLastY;
    private int top = 0;
    private int dismissHeight = 0;

    public InnerRecyclerVIew(Context context) {
        super(context);
    }

    public InnerRecyclerVIew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerRecyclerVIew(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setNestScrollableView(ViewGroup view) {
        parent = view;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (top == 0) {
            top = getParent() != null ? ((View) getParent()).getTop() : 0;
            dismissHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (top == 0) {
            top = getParent() != null ? ((View) getParent()).getTop() : 0;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 发生down事件时,记录y坐标
                mLastY = y;
                mLastX = x;
                parent.requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                /*if (deltaY < 0) {//上移
                    if (dismissHeight < top) {//FrameLayout控件未在最顶部, 父控件ScrollView去滑动
                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                } else {//下移
                    if (1.75 * dismissHeight >= top) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                }*/
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(event);
    }
}
