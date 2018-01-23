package mcxtzhang.itemdecorationdemo.ui;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.amar.library.ui.StickyScrollView;

/**
 * Created With Android Studio
 * Email: sielee@163.com
 * Author: Lee Sie
 * CopyRight: CL
 * <p>
 * Description: TODO
 * </p>
 */

public class NestScrollView extends StickyScrollView {

    private boolean isIntercept;
    private boolean isSolve;//是否完成了拦截判断，如果决定拦截，那么同系列事件就不能设置为不拦截
    private PointF mPointGapF = new PointF();//用来设置scrollX，即展开/关闭menu

    public NestScrollView(Context context) {
        super(context);
    }

    public NestScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPointGapF.x = ev.getX();
                mPointGapF.y = ev.getY();
                return false;//down的时候拦截后，就只能交给自己处理了

            case MotionEvent.ACTION_MOVE:
                if (!isSolve) {//是否已经决定拦截/不拦截？
                    isIntercept = (Math.abs(ev.getX() - mPointGapF.x) > Math.abs(ev.getY() - mPointGapF.y) * 2);//如果是左右滑动，且水平角度小于30°，就拦截
                    isSolve = true;
                }
                return isIntercept;//如果是xxx，就拦截
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                scrollBy((int) (mPointGapF.x - ev.getX()), 0);
                mPointGapF.x = ev.getX();
                mPointGapF.y = ev.getY();
                break;
        }
        return super.onTouchEvent(ev);
    }
}
