package at.maui.flopsydroid.android.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by maui on 07.07.14.
 */
public class WearButton extends ImageButton {

    public WearButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WearButton(Context context) {
        super(context);
    }

    public WearButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
