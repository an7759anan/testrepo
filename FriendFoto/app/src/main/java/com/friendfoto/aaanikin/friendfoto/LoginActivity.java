package com.friendfoto.aaanikin.friendfoto;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.ByteArrayOutputStream;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity{

    // класс для того, чтобы у Android'а не было желания запускать какой-либо иной браузер ...
    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }

    public String accessToken;
    public static DBHelper dbHelper;
    public static int count;
    public static int ident;
    public WebView vklogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = new DBHelper(this);
        vklogin = (WebView) findViewById(R.id.webviewid);
        accessToken=null;
        vklogin.setWebViewClient(new MyWebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                int tokenStartIdx = url.indexOf("access_token=");
                if (tokenStartIdx>0 && accessToken==null) {
                    int startIdx=tokenStartIdx+"access_token=".length();
                    int finishIdx=url.indexOf("&",startIdx);
                    accessToken = url.substring(startIdx,finishIdx);
                    attemptLogin();
                }
            }
        });
        vklogin.getSettings().setJavaScriptEnabled(true);
        vklogin.loadUrl("https://oauth.vk.com/authorize?client_id=6099193&display=mobile&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=token&v=5.67");
    }

    private void attemptLogin() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final RequestQueue queueForImages = Volley.newRequestQueue(this);
        queueForImages.stop();
// список друзей БЕЗ фото и информации о них
        String url ="https://api.vk.com/method/friends.get?order=name&fields=nickname,photo_200_orig&access_token="+accessToken;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String s=response;
                        SQLiteDatabase db=dbHelper.getWritableDatabase();
                        int clearCount=db.delete("friends",null,null);
                        ContentValues cv = new ContentValues();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            JSONArray items = jObj.getJSONArray("response");
                            count = items.length();
                            for (int i=0; i < count; i++){
                                JSONObject item = items.getJSONObject(i);
                                ident = item.getInt("uid");
                                String name = item.getString("first_name")+" "+item.getString("last_name");
                                String image_url = item.getString("photo_200_orig").replace("\\","");
                                cv.put("id",ident);
                                cv.put("name",name);
                                cv.put("image_url",image_url);
                                try {
                                    db.insertOrThrow("friends", null, cv);
                                } catch (android.database.sqlite.SQLiteException e){
                                    s=e.getMessage();
                                }
                                // планируем http запрос на получение картинок ...
                                ImageRequest imageRequest = new ImageRequest(
                                        image_url,
                                        new Response.Listener<Bitmap>() {
                                            int id=ident;
                                            @Override
                                            public void onResponse(Bitmap response) {
                                                Bitmap bitmap = response;
                                                int idd=id;
                                                SQLiteDatabase db=dbHelper.getWritableDatabase();
                                                ContentValues cv = new ContentValues();
                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                                                cv.put("image",stream.toByteArray());
                                                int u = db.update("friends",cv,"id = " + Integer.toString(id),null);
                                                db.close();
                                                if (--count==0){
                                                    Intent intent=new Intent(LoginActivity.this,FriendListActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        },
                                        200,200,
                                        Bitmap.Config.ARGB_8888,
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                String s="That didn't work!";
                                            }});
                                queueForImages.add(imageRequest);
                            }
                            queueForImages.start();
                        } catch (JSONException e){
                            s = e.getMessage();
                        }
                        db.close();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String s="That didn't work!";
            }
        });
        queue.add(stringRequest);
        return;
    }
}

