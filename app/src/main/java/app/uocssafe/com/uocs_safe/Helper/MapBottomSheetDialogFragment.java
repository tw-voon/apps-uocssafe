package app.uocssafe.com.uocs_safe.Helper;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import app.uocssafe.com.uocs_safe.News.SinglePost;
import app.uocssafe.com.uocs_safe.R;

public class MapBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private String title, desc;
    private int report_id;

    public void setData(String title, String desc, int report_id){
        this.title = title;
        this.desc = desc;
        this.report_id = report_id;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_map, null);
        dialog.setContentView(contentView);
        TextView tvTitle = (TextView) contentView.findViewById(R.id.post_title);
        TextView tvDesc = (TextView) contentView.findViewById(R.id.post_description);
        TextView readMore = (TextView) contentView.findViewById(R.id.readmore);
        tvTitle.setText(title);
        tvDesc.setText(desc);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SinglePost.class);
                intent.putExtra("report_ID", String.valueOf(report_id));
                startActivity(intent);
            }
        });
    }
}
