package com.jdelorenzo.capstoneproject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

/*
Extension of {@link android.util.SparseBooleanArray} that implements {@link android.os.Parcelable}
for easy saving and restoring from a Bundle.
 */

public class ParcelableSparseBooleanArray extends SparseBooleanArray implements Parcelable {
    public static Parcelable.Creator<ParcelableSparseBooleanArray> CREATOR =
            new Parcelable.Creator<ParcelableSparseBooleanArray>() {
        @Override
        public ParcelableSparseBooleanArray createFromParcel(Parcel source) {
            ParcelableSparseBooleanArray read = new ParcelableSparseBooleanArray();
            int size = source.readInt();

            int[] keys = new int[size];
            boolean[] values = new boolean[size];

            source.readIntArray(keys);
            source.readBooleanArray(values);

            for (int i = 0; i < size; i++) {
                read.put(keys[i], values[i]);
            }

            return read;
        }

        @Override
        public ParcelableSparseBooleanArray[] newArray(int size) {
            return new ParcelableSparseBooleanArray[size];
        }
    };

    public ParcelableSparseBooleanArray() {

    }

    public ParcelableSparseBooleanArray(SparseBooleanArray sparseBooleanArray) {
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            this.put(sparseBooleanArray.keyAt(i), sparseBooleanArray.valueAt(i));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int[] keys = new int[size()];
        boolean[] values = new boolean[size()];

        for (int i = 0; i < size(); i++) {
            keys[i] = keyAt(i);
            values[i] = valueAt(i);
        }

        dest.writeInt(size());
        dest.writeIntArray(keys);
        dest.writeBooleanArray(values);
    }
}
