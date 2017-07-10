package com.friendfoto.aaanikin.friendfoto;

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
import android.view.View;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FriendListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_acivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SQLiteDatabase db=LoginActivity.dbHelper.getReadableDatabase();
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
                Log.d(LoginActivity.LOG_TAG,"");

                TableRow row = new TableRow(this);
                row.setGravity(Gravity.CENTER_HORIZONTAL);

                ImageView imageCell = new ImageView(this);
                imageCell.setImageBitmap(image);
                row.addView(imageCell);

                TextView cell = new TextView(this);
//                cell.setText(Integer.toString(id));
//                row.addView(cell);
                cell = new TextView(this);
                cell.setText(name);
                row.addView(cell);

                table.addView(row);

            } while (c.moveToNext());
        } else
            Log.d(LoginActivity.LOG_TAG,"0 rows");
        c.close();
        db.close();
    }
}
