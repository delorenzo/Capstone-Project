package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.jdelorenzo.capstoneproject.R;

import java.io.Serializable;
import java.util.ArrayList;

public class SelectDaysDialogFragment extends DialogFragment {
    ArrayList<Integer> mSelectedItems = new ArrayList<>();
    private SelectDaysListener mCallback;
    private boolean[] mCheckedItems;
    private static final String ARG_CHECKED_ITEMS = "checkedItems";

    public interface SelectDaysListener extends Serializable {
        void onDaysSelected(ArrayList<Integer> days);
    }

    public static SelectDaysDialogFragment newInstance(boolean[] checked) {
        Bundle b = new Bundle();
        b.putBooleanArray(ARG_CHECKED_ITEMS, checked);
        SelectDaysDialogFragment fragment = new SelectDaysDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            mCheckedItems = getArguments().getBooleanArray(ARG_CHECKED_ITEMS);
        }
        if (mCheckedItems != null) {
            for (int i = 0; i < mCheckedItems.length; i++) {
                if (mCheckedItems[i]) {
                    mSelectedItems.add(i);
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_select_days_title)
                .setMultiChoiceItems(R.array.days, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            mSelectedItems.add(which);
                        } else if (mSelectedItems.contains(which)) {
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton(R.string.action_submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.onDaysSelected(mSelectedItems);
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            if (null == mCallback) {
                mCallback = (SelectDaysListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement SelectDaysListener");

        }
        super.onAttach(activity);
    }
}
