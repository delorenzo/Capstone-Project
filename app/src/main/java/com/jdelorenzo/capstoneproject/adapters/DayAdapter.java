package com.jdelorenzo.capstoneproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.ItemChoiceManager;
import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.SelectWorkoutDialogFragment;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;
import com.jdelorenzo.capstoneproject.service.DatabaseIntentService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private ItemChoiceManager mICM;
    final private DayAdapterOnClickHandler mClickHandler;
    private View mEmptyView;

    public DayAdapter(Context context, DayAdapterOnClickHandler clickHandler,
                           View emptyView, int choiceMode) {
        mContext = context;
        mICM = new ItemChoiceManager(this);
        mClickHandler = clickHandler;
        mEmptyView = emptyView;
    }

    public interface DayAdapterOnClickHandler {
        void onClick(Long id, DayAdapterViewHolder vh);
    }

    public class DayAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @Bind(R.id.delete_day_button) Button deleteButton;
        @Bind(R.id.day) TextView dayTextView;

        public DayAdapterViewHolder(View view)
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
    public DayAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_exercise, parent, false);
            view.setFocusable(true);
            return new DayAdapterViewHolder(view);
        } else {
            throw new RuntimeException("ExerciseAdapter not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(final DayAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
//        holder.dayTextView.setText(mCursor.getString(SelectWorkoutDialogFragment.COL_DAY_OF_WEEK));
//        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatabaseIntentService.startActionDeleteDay(mContext, mCursor.getLong(SelectWorkoutDialogFragment.COL_ID));
//            }
//        });
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
