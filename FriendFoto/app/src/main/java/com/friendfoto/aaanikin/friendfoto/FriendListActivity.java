package com.friendfoto.aaanikin.friendfoto;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static com.friendfoto.aaanikin.friendfoto.LoginActivity.dbHelper;

public class FriendListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_acivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SQLiteDatabase db= dbHelper.getReadableDatabase();
        Cursor c=db.query("friends",null,null,null,null,null,null);
        TableLayout table = (TableLayout)findViewById(R.id.friendListTable);
        TableRow rowColumnLabels = new TableRow(this);
        if(c.moveToFirst()){
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int imageColIndex = c.getColumnIndex("image");
            do{
                int id = c.getInt(idColIndex);
                String name = c.getString(nameColIndex);
                byte[] imageBytes = c.getBlob(imageColIndex);
                Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                TableRow row = new TableRow(this);
                row.setGravity(Gravity.LEFT);
                ImageView imageCell = new ImageView(this);
                imageCell.setImageBitmap(image);
                row.addView(imageCell);
                TextView cell = new TextView(this);
                cell = new TextView(this);
                cell.setText(name);
                row.addView(cell);
                table.addView(row);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend_list_acivity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                finish();
/*                Intent intent=new Intent(FriendListActivity.this,LoginActivity.class);
                startActivity(intent);*/
/*                RequestQueue queue = Volley.newRequestQueue(this);
                String url =getResources().getString(R.string.vkLogoutUrl);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String s=response;
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            String s="That didn't work!";
                            }
                        });
                queue.add(stringRequest);
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }
}
