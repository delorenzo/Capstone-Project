package com.jdelorenzo.capstoneproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jdelorenzo.capstoneproject.EditDayFragment;
import com.jdelorenzo.capstoneproject.ItemChoiceManager;
import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;
import com.jdelorenzo.capstoneproject.service.DatabaseIntentService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private ItemChoiceManager mICM;
    final private DayAdapterOnClickHandler mClickHandler;
    private View mEmptyView;
    private String[] dayStrings;

    public DayAdapter(Context context, DayAdapterOnClickHandler clickHandler,
                           View emptyView) {
        mContext = context;
        mICM = new ItemChoiceManager(this);
        mClickHandler = clickHandler;
        mEmptyView = emptyView;
        dayStrings = context.getResources().getStringArray(R.array.days);
    }

    public interface DayAdapterOnClickHandler {
        void onClick(Long id, DayAdapterViewHolder vh);
        void onDelete(Long id, DayAdapterViewHolder vh);
    }

    public class DayAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.delete_day_button) ImageButton deleteButton;
        @BindView(R.id.day) Button dayButton;

        public DayAdapterViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.day)
        public void onClick() {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dayId = mCursor.getColumnIndex(WorkoutContract.DayEntry._ID);
            mClickHandler.onClick(mCursor.getLong(dayId), this);
            mICM.onClick(this);
        }

        @OnClick(R.id.delete_day_button)
        public void onDelete() {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int dayId = mCursor.getColumnIndex(WorkoutContract.DayEntry._ID);
            mClickHandler.onDelete(mCursor.getLong(dayId), this);
        }
    }

    @Override
    public DayAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_workout_day, parent, false);
            view.setFocusable(true);
            return new DayAdapterViewHolder(view);
        } else {
            throw new RuntimeException("DayAdapter not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(final DayAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int dayIndex = mCursor.getInt(EditDayFragment.COL_DAY_OF_WEEK);
        holder.dayButton.setText(dayStrings[dayIndex]);
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

    public boolean[] getChecked() {
        boolean[] checked = new boolean[7];
        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {
            checked[mCursor.getInt(EditDayFragment.COL_DAY_OF_WEEK)] = true;
            mCursor.moveToNext();
        }
        return checked;
    }
}
