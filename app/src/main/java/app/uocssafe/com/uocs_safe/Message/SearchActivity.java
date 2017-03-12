package app.uocssafe.com.uocs_safe.Message;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import app.uocssafe.com.uocs_safe.R;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
}
