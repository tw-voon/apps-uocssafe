package app.uocssafe.com.uocs_safe.Personal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MyActivityFragment extends Fragment {

    RecyclerView my_activity_list;
    MyActivityAdapter myActivityAdapter;
    ArrayList<MyActivityModel> data = new ArrayList<>();
    Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_activity_fragment, container, false);
        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getView() != null){
            my_activity_list = (RecyclerView) getView().findViewById(R.id.my_activity_list);
            myActivityAdapter = new MyActivityAdapter(getView().getContext(), data);
            my_activity_list.setAdapter(myActivityAdapter);
            my_activity_list.setLayoutManager(new LinearLayoutManager(getActivity()));
            my_activity_list.setNestedScrollingEnabled(false);
        } else
            Toast.makeText(getActivity(), "No view found", Toast.LENGTH_SHORT).show();
        session = new Session(getActivity());

        getOwnActivity();
    }

    private void getOwnActivity(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_OWN_ACTIVITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Status75: ", response);
                        Toast.makeText(getActivity(), "success" + session.getUserID(), Toast.LENGTH_SHORT).show();
                        processActivity(response);
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

    private void processActivity(String response){
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MyActivityModel myActivityModel = new MyActivityModel(
                        jsonObject.getString("report_id"),
                        jsonObject.getString("action_name"),
                        jsonObject.getString("created_at"),
                        jsonObject.getString("avatar_link")
                );
                data.add(myActivityModel);
            }
            myActivityAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
