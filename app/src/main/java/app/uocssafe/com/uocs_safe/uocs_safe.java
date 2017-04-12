package app.uocssafe.com.uocs_safe;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

import app.uocssafe.com.uocs_safe.Helper.Session;

/**
 * Created by User on 9/3/2017.
 */

public class uocs_safe extends Application{

    private static uocs_safe instances;
    private static Session pref;
    private RequestQueue mRequestQueue;
    ImageLoader imageLoader;
    public static final String TAG = uocs_safe.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

//    uocs_safe(){
//        imageLoader = new ImageLoader(mRequestQueue,
//                new ImageLoader.ImageCache() {
//                    private final LruCache<String, Bitmap>
//                            cache = new LruCache<String, Bitmap>(20);
//
//                    @Override
//                    public Bitmap getBitmap(String url) {
//                        return cache.get(url);
//                    }
//
//                    @Override
//                    public void putBitmap(String url, Bitmap bitmap) {
//                        cache.put(url, bitmap);
//                    }
//                });
//    }

    public static synchronized uocs_safe getInstance() {
        return instances;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public Session getPrefManager() {
        if (pref == null) {
            pref = new Session(this);
        }

        return pref;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
