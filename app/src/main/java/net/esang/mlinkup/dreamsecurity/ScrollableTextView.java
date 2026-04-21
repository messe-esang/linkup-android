package net.esang.mlinkup.dreamsecurity;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ScrollableTextView extends AppCompatTextView {
    public ScrollableTextView(@NonNull Context context) {
        this(context, null);
    }

    public ScrollableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public ScrollableTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMovementMethod(ScrollingMovementMethod.getInstance());
        setVerticalScrollBarEnabled(true);
    }
}
