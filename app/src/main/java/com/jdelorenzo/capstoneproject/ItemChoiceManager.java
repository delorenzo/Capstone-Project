package com.jdelorenzo.capstoneproject;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.widget.AbsListView;
import android.widget.Checkable;

public class ItemChoiceManager {
    private static final String EXTRA_SELECTED_ITEMS = "selectedItems";
    private RecyclerView.Adapter mAdapter;
    private int mChoiceMode;
    ParcelableSparseBooleanArray mSelected = new ParcelableSparseBooleanArray();

    public ItemChoiceManager(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    public void onClick(RecyclerView.ViewHolder viewHolder) {

        int position = viewHolder.getAdapterPosition();

        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        switch (mChoiceMode) {
            case AbsListView.CHOICE_MODE_NONE:
                break;
            case AbsListView.CHOICE_MODE_SINGLE:
                boolean selected = mSelected.get(position, false);
                if (!selected) {
                    for (int i = 0; i < mSelected.size(); i++) {
                        mAdapter.notifyItemChanged(mSelected.keyAt(i));
                    }
                    mSelected.clear();
                    mSelected.put(position, true);
                }
                mAdapter.onBindViewHolder(viewHolder, position);
                break;
            case AbsListView.CHOICE_MODE_MULTIPLE:
                selected = mSelected.get(position, false);
                mSelected.put(position, !selected);
                mAdapter.onBindViewHolder(viewHolder, position);
                break;
        }
    }

    public void setChoiceMode(int choiceMode) {
        if (mChoiceMode != choiceMode) {
            mChoiceMode = choiceMode;
            mSelected.clear();
        }
    }

    public boolean isItemChecked(int position) {
        return mSelected.get(position);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSelected = savedInstanceState.getParcelable(EXTRA_SELECTED_ITEMS);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_SELECTED_ITEMS, mSelected);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        boolean selected = isItemChecked(position);
        if (vh.itemView instanceof Checkable) {
            ((Checkable) vh.itemView).setChecked(selected);
        }
        ViewCompat.setActivated(vh.itemView, selected);
    }
}
