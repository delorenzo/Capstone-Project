package com.jdelorenzo.capstoneproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.ItemChoiceManager;
import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.Utility;
import com.jdelorenzo.capstoneproject.WorkoutFragment;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private ItemChoiceManager mICM;
    final private ExerciseAdapterOnClickHandler mClickHandler;
    private View mEmptyView;

    public ExerciseAdapter(Context context, ExerciseAdapterOnClickHandler clickHandler,
                           View emptyView, int choiceMode) {
        mContext = context;
        mICM = new ItemChoiceManager(this);
        mClickHandler = clickHandler;
        mEmptyView = emptyView;
    }

    public interface ExerciseAdapterOnClickHandler {
        void onClick(Long id, ExerciseAdapterViewHolder vh);
    }

    public class ExerciseAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @Bind(R.id.complete_checkbox) CheckBox completeCheckbox;
        @Bind(R.id.exercise_name) TextView exerciseName;
        @Bind(R.id.repetitions) TextView repetitions;
        @Bind(R.id.weight) TextView weight;
        @Bind(R.id.sets) TextView sets;

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
            int exerciseId = mCursor.getColumnIndex(WorkoutContract.ExerciseEntry._ID);
            mClickHandler.onClick(mCursor.getLong(exerciseId), this);
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
        holder.repetitions.setText(String.format(Locale.getDefault(), "%d",
                mCursor.getLong(WorkoutFragment.COL_REPS)));
        holder.sets.setText(String.format(Locale.getDefault(), "%d",
                mCursor.getLong(WorkoutFragment.COL_SETS)));
        holder.weight.setText(Utility.getFormattedWeightString(mContext,
                mCursor.getLong(WorkoutFragment.COL_WEIGHT)));
        holder.completeCheckbox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                long sets = Long.parseLong(holder.sets.toString());
                if (isChecked && sets > 0) {
                    sets--;
                    holder.sets.setText(String.format(Locale.getDefault(), "%d", sets));
                }
                if (sets > 0) {
                    buttonView.setChecked(false);
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
