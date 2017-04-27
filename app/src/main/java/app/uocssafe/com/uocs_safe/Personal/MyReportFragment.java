package app.uocssafe.com.uocs_safe.Personal;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.R;


public class MyReportFragment extends Fragment{

    RecyclerView my_report_list;
    MyReportAdapter myReportAdapter;
    ArrayList<MyReportModel> data = new ArrayList<>();
    Session session;
//    private PersonalActivity.OpenBottomSheet openBottomSheet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_report_fragment, container, false);
        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if(getView() != null) {
            my_report_list = (RecyclerView) getView().findViewById(R.id.my_report_list);
            myReportAdapter = new MyReportAdapter(getView().getContext(), data);
            my_report_list.setAdapter(myReportAdapter);
            my_report_list.setLayoutManager(new LinearLayoutManager(getActivity()));
            my_report_list.setNestedScrollingEnabled(false);
        } else
            Toast.makeText(getActivity(), "No view found", Toast.LENGTH_SHORT).show();
        session = new Session(getActivity());

        getOwnReport();
    }

    private void getOwnReport(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_OWN_REPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Status: ", response);
                        Toast.makeText(getActivity(), "success" + session.getUserID(), Toast.LENGTH_SHORT).show();
                        processReport(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("user_id",session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void processReport(String response){

        try {
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MyReportModel myReportModel = new MyReportModel(
                        session.getUsername(),
                        jsonObject.getString("image"),
                        jsonObject.getString("created_at"),
                        jsonObject.getString("report_Title"),
                        jsonObject.getString("reason"),
                        jsonObject.getString("action_taken"),
                        jsonObject.getString("report_ID"),
                        jsonObject.getString("name"));
                data.add(myReportModel);
            }
            myReportAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
