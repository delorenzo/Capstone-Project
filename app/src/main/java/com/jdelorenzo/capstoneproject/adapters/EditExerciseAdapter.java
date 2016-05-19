package com.jdelorenzo.capstoneproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.EditWorkoutFragment;
import com.jdelorenzo.capstoneproject.ItemChoiceManager;
import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.Utility;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditExerciseAdapter extends RecyclerView.Adapter<EditExerciseAdapter.ExerciseAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private ItemChoiceManager mICM;
    final private ExerciseAdapterOnClickHandler mClickHandler;
    private View mEmptyView;

    public EditExerciseAdapter(Context context, ExerciseAdapterOnClickHandler clickHandler,
                               View emptyView, int choiceMode) {
        mContext = context;
        mICM = new ItemChoiceManager(this);
        mClickHandler = clickHandler;
        mEmptyView = emptyView;
    }

    public interface ExerciseAdapterOnClickHandler {
        void onClick(Long id, ExerciseAdapterViewHolder vh);
        void onDelete(Long id, ExerciseAdapterViewHolder vh);
    }

    public class ExerciseAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @BindView(R.id.delete_exercise_button) ImageButton deleteExerciseButton;
        @BindView(R.id.exercise) AppCompatAutoCompleteTextView exerciseName;
        @BindView(R.id.repetitions) AppCompatEditText repetitions;
        @BindView(R.id.weight) AppCompatEditText weight;
        @BindView(R.id.sets) AppCompatEditText sets;
        @BindView(R.id.weight_units) TextView weightUnits;

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

        @OnClick(R.id.delete_exercise_button)
        public void onDelete() {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int exerciseId = mCursor.getColumnIndex(WorkoutContract.ExerciseEntry._ID);
            mClickHandler.onDelete(mCursor.getLong(exerciseId), this);
        }
    }

    @Override
    public ExerciseAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_add_exercise, parent, false);
            view.setFocusable(true);
            return new ExerciseAdapterViewHolder(view);
        } else {
            throw new RuntimeException("ExerciseAdapter not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(final ExerciseAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.exerciseName.setText(mCursor.getString(EditWorkoutFragment.COL_DESCRIPTION));
        holder.repetitions.setText(String.format(Locale.getDefault(), "%d",
                mCursor.getInt(EditWorkoutFragment.COL_REPS)));
        holder.sets.setText(String.format(Locale.getDefault(), "%d",
                mCursor.getInt(EditWorkoutFragment.COL_SETS)));
        holder.weight.setText(Utility.getFormattedWeightStringWithoutUnits(mContext,
                mCursor.getDouble(EditWorkoutFragment.COL_WEIGHT)));
        holder.weightUnits.setText(Utility.getWeightUnits(mContext));
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
