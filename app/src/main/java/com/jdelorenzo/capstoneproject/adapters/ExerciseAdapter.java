package com.jdelorenzo.capstoneproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jdelorenzo.capstoneproject.EditWorkoutFragment;
import com.jdelorenzo.capstoneproject.ItemChoiceManager;
import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.Utility;
import com.jdelorenzo.capstoneproject.WorkoutFragment;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private ItemChoiceManager mICM;
    final private ExerciseAdapterOnClickHandler mClickHandler;
    private View mEmptyView;
    private int itemsChecked;

    public ExerciseAdapter(Context context, ExerciseAdapterOnClickHandler clickHandler,
                           View emptyView, int choiceMode) {
        mContext = context;
        mICM = new ItemChoiceManager(this);
        mClickHandler = clickHandler;
        mEmptyView = emptyView;
    }

    public interface ExerciseAdapterOnClickHandler {
        void onClick(long id, double weight, ExerciseAdapterViewHolder vh);
        void allItemsChecked();
    }

    public class ExerciseAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @BindView(R.id.complete_checkbox) CheckBox completeCheckbox;
        @BindView(R.id.exercise_name) TextView exerciseName;
        @BindView(R.id.repetitions) TextView repetitions;
        @BindView(R.id.weight) TextView weight;
        @BindView(R.id.sets) TextView sets;
        private int setCount;

        public ExerciseAdapterViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int exerciseIndex = mCursor.getColumnIndex(WorkoutContract.ExerciseEntry._ID);
            int weightIndex = mCursor.getColumnIndex(WorkoutContract.ExerciseEntry.COLUMN_WEIGHT);
            mClickHandler.onClick(mCursor.getLong(exerciseIndex), mCursor.getDouble(weightIndex),
                    this);
            mICM.onClick(this);
        }
    }

    @Override
    public ExerciseAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_exercise, parent, false);
            view.setFocusable(true);
            return new ExerciseAdapterViewHolder(view);
        } else {
            throw new RuntimeException("ExerciseAdapter not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(final ExerciseAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.exerciseName.setText(mCursor.getString(WorkoutFragment.COL_DESCRIPTION));
        holder.repetitions.setText(String.format(Locale.getDefault(), mContext.getString(R.string.format_reps),
                mCursor.getInt(WorkoutFragment.COL_REPS)));
        holder.setCount = mCursor.getInt(WorkoutFragment.COL_SETS);
        holder.sets.setText(String.format(Locale.getDefault(), mContext.getString(R.string.format_sets), holder.setCount));
        holder.weight.setText(Utility.getFormattedWeightString(mContext,
                mCursor.getDouble(WorkoutFragment.COL_WEIGHT)));
        holder.completeCheckbox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && holder.setCount > 0) {
                    holder.setCount--;
                    holder.sets.setText(String.format(Locale.getDefault(),
                            mContext.getString(R.string.format_sets),
                            holder.setCount));
                }
                if (holder.setCount > 0) {
                    buttonView.setChecked(false);
                }
                else {
                    itemsChecked++;
                    Log.e("ExerciseAdapter", itemsChecked + ":  " + mCursor.getCount());
                    if (itemsChecked >= mCursor.getCount()) {
                        mClickHandler.allItemsChecked();
                    }
                }
            }
        });
        mICM.onBindViewHolder(holder, position);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
