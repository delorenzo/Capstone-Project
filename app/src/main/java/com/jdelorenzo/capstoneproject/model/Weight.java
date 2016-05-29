package com.jdelorenzo.capstoneproject.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Weight implements Parcelable {
    private long exerciseId;
    private Double weight;

    public Weight(long i, Double w) {
        exerciseId = i;
        weight = w;
    }

    public long getExerciseId() {
        return exerciseId;
    }

    public Double getWeight() {
        return weight;
    }

    protected Weight(Parcel in) {
        exerciseId = in.readLong();
        weight = in.readByte() == 0x00 ? null : in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(exerciseId);
        if (weight == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(weight);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Weight> CREATOR = new Parcelable.Creator<Weight>() {
        @Override
        public Weight createFromParcel(Parcel in) {
            return new Weight(in);
        }

        @Override
        public Weight[] newArray(int size) {
            return new Weight[size];
        }
    };
}