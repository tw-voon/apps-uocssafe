package app.uocssafe.com.uocs_safe.Helper;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.uocssafe.com.uocs_safe.R;

public class StatusBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private String status, reason, action;

    public void setData(String status, String reason, String action){
        this.status = status;
        this.reason = reason;
        this.action = action;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_status, null);
        dialog.setContentView(contentView);
        TextView tvStatus = (TextView) contentView.findViewById(R.id.tvStatus);
        TextView tvActionTaken = (TextView) contentView.findViewById(R.id.tvActionTaken);
        TextView tvReason = (TextView) contentView.findViewById(R.id.tvReason);
        tvStatus.setText(status);
        tvReason.setText(reason);
        tvActionTaken.setText(action);
    }
}
