package com.golan.amit.magicfour;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String DEBUGTAG = "AMGO";
    public static final boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        insertRecordsToDbForDebugAndDev();

        redirect();
    }

    /**
     * for debug & dev phase
     */
    private void insertRecordsToDbForDebugAndDev() {
        MagicFourDbHelper mfdh = new MagicFourDbHelper(this);
        mfdh.open();
        mfdh.insert("130", "70", "Amit");
        mfdh.insert("58", "110", "Lior");
        mfdh.insert("220", "331", "Ariel");
        mfdh.close();
    }

    private void redirect() {
        Intent i = new Intent(this, MagicFourActivity.class);
        i.putExtra("general", "info");
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "requestcode: " + requestCode + ", resultcode: " + resultCode);
        }
        if(data != null && DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "data: " + data.toString());
        }
        if(resultCode == RESULT_CANCELED) {
            if (DEBUG) {
                Toast.makeText(this, "Redirecting back", Toast.LENGTH_SHORT).show();
            }
            redirect();
        }
    }
}
