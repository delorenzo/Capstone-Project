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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private int lastPosition = -1;

    public EditExerciseAdapter(Context context, ExerciseAdapterOnClickHandler clickHandler,
                               View emptyView, int choiceMode) {
        mContext = context;
        mICM = new ItemChoiceManager(this);
        mClickHandler = clickHandler;
        mEmptyView = emptyView;
    }

    public interface ExerciseAdapterOnClickHandler {
        void onClick(Long id, String name, int reps, int sets, double weight,
                     ExerciseAdapterViewHolder vh);
        void onDelete(Long id, ExerciseAdapterViewHolder vh);
    }

    public class ExerciseAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @BindView(R.id.delete_exercise_button) ImageButton deleteExerciseButton;
        @BindView(R.id.exercise_name) TextView exerciseName;
        @BindView(R.id.repetitions) TextView repetitions;
        @BindView(R.id.weight) TextView weight;
        @BindView(R.id.sets) TextView sets;
        @BindView(R.id.list_item_add_exercise) View rootView;

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
            int exerciseIdIndex = mCursor.getColumnIndex(WorkoutContract.ExerciseEntry._ID);
            int descriptionIndex = mCursor.getColumnIndex(WorkoutContract.ExerciseEntry.COLUMN_DESCRIPTION);
            int setsIndex = mCursor.getColumnIndex(WorkoutContract.ExerciseEntry.COLUMN_SETS);
            int repsIndex = mCursor.getColumnIndex(WorkoutContract.ExerciseEntry.COLUMN_REPS);
            int weightIndex = mCursor.getColumnIndex(WorkoutContract.ExerciseEntry.COLUMN_WEIGHT);
            mClickHandler.onClick(
                    mCursor.getLong(exerciseIdIndex),
                    mCursor.getString(descriptionIndex),
                    mCursor.getInt(repsIndex),
                    mCursor.getInt(setsIndex),
                    mCursor.getDouble(weightIndex),
                    this);
            mICM.onClick(this);
        }

        @OnClick(R.id.delete_exercise_button)
        public void onDelete() {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);
            deleteExerciseButton.startAnimation(animation);
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            notifyItemRemoved(adapterPosition);
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
        holder.repetitions.setText(String.format(Locale.getDefault(),
                mContext.getString(R.string.format_reps),
                mCursor.getInt(EditWorkoutFragment.COL_REPS)));
        holder.sets.setText(String.format(Locale.getDefault(),
                mContext.getString(R.string.format_sets),
                mCursor.getInt(EditWorkoutFragment.COL_SETS)));
        holder.weight.setText(Utility.getFormattedWeightString(mContext,
                mCursor.getDouble(EditWorkoutFragment.COL_WEIGHT)));
        mICM.onBindViewHolder(holder, position);
        setAnimation(holder.rootView, position);
    }
    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            view.startAnimation(animation);
            lastPosition = position;
        }
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
