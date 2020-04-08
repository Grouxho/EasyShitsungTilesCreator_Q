package com.android.systemui.plugins.qs;

import android.content.Context;
import android.graphics.drawable.Drawable;

public interface QSTile {

    public static class State {
        public boolean dualTarget=false; // this is new in samsung android Q. If we have a tile with adapter, we have to set it to true in TILES in handleUpdateState(BooleanState state, Object arg) method if we want to enable the adapter !!
        public int state;
        public Icon icon;
        public CharSequence label;
        public CharSequence contentDescription;
        public CharSequence dualLabelContentDescription;

        public boolean copyTo(State other) {
            return true;
        }
        protected StringBuilder toStringBuilder() {
            return null;
        }

        public State copy() {
            return null;
        }

    }


    public interface Callback {
        public static final int VERSION = 1;
        void onStateChanged(State state);
        void onShowDetail(boolean show);
        void onToggleStateChanged(boolean state);
        void onScanStateChanged(boolean state);
    }

    public static abstract class Icon {
        public static final int VERSION = 1;
        abstract public Drawable getDrawable(Context context);

        public Drawable getInvisibleDrawable(Context context) {
            return getDrawable(context);
        }

        @Override
        public int hashCode() {
            return Icon.class.hashCode();
        }

        public int getPadding() {
            return 0;
        }
    }

    public static class BooleanState extends State {
        public static final int VERSION = 1;
        public boolean value;

        @Override
        public boolean copyTo(State other) {
            final BooleanState o = (BooleanState) other;
            final boolean changed = super.copyTo(other) || o.value != value;
            o.value = value;
            return changed;
        }

        @Override
        protected StringBuilder toStringBuilder() {
            final StringBuilder rt = super.toStringBuilder();
            rt.insert(rt.length() - 1, ",value=" + value);
            return rt;
        }

        @Override
        public State copy() {
            BooleanState state = new BooleanState();
            copyTo(state);
            return state;
        }
    }
}
