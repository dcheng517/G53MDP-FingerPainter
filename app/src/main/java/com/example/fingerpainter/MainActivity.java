package com.example.fingerpainter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.SynchronousQueue;

public class MainActivity extends AppCompatActivity {

    private static final int BRUSH_REQUEST_CODE = 1;
    private static final int COLOR_REQUEST_CODE = 2;

    private FingerPainterView fingerPainterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fingerPainterView = new FingerPainterView(this);
        fingerPainterView.setId(R.id.fingerPainterView);
        fingerPainterView = (FingerPainterView)findViewById(R.id.fingerPainterView);

        // load image from other app
        if(getIntent().getData()!=null){
            Intent load=getIntent();
            fingerPainterView.load(load.getData());
        }

//        On activity resume update paint
        if(savedInstanceState != null)
        {
            fingerPainterView.setBrushWidth(savedInstanceState.getInt("nowSize"));
            fingerPainterView.setColour(savedInstanceState.getInt("nowColor"));

            if(savedInstanceState.getString("nowType").equals("ROUND"))
                fingerPainterView.setBrush(Paint.Cap.ROUND);
            else if(savedInstanceState.getString("nowType").equals("SQUARE"))
                fingerPainterView.setBrush(Paint.Cap.SQUARE);
        }
    }

    public void onClickColour(View view)
    {
        Intent color = new Intent(this, SelectColour.class);
        color.putExtra("nowColor", fingerPainterView.getColour());
        startActivityForResult(color, COLOR_REQUEST_CODE);
    }

    public void onClickBrush(View view)
    {
        Intent brush = new Intent(this, SelectBrush.class);
        Bundle oldBrush = new Bundle();
        oldBrush.putString("nowType", fingerPainterView.getBrush().toString());
        oldBrush.putInt("nowSize", fingerPainterView.getBrushWidth());
        oldBrush.putInt("nowColor", fingerPainterView.getColour());
        brush.putExtras(oldBrush);
        startActivityForResult(brush, BRUSH_REQUEST_CODE);
    }

//    Extra feature: Clear canvas
    public void onClickClear(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Clear screen");
        builder.setMessage("Are you sure you want to clear the canvas?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fingerPainterView.clearScreen();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == BRUSH_REQUEST_CODE) && (resultCode == RESULT_OK))
        {
            Bundle brushResult = data.getExtras();

            try {
                fingerPainterView.setBrushWidth(brushResult.getInt("newSize"));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Invalid size", Toast.LENGTH_SHORT).show();
            }

            try {
                if (brushResult.getString("newType").equals("ROUND"))
                    fingerPainterView.setBrush(Paint.Cap.ROUND);
                else if (brushResult.getString("newType").equals("SQUARE"))
                    fingerPainterView.setBrush(Paint.Cap.SQUARE);
            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "Invalid brush type", Toast.LENGTH_SHORT).show();
            }
        }

        if((requestCode == COLOR_REQUEST_CODE) && (resultCode == RESULT_OK))
        {
            Bundle colorResult = data.getExtras();

            try{
                String temp = colorResult.getString("newColor");
                Log.d("finalColor", temp);
                fingerPainterView.setColour(Color.parseColor(temp));
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Invalid color", Toast.LENGTH_SHORT).show();
            }
        }

    }

//    Save information in a bundle for when activity is resumed
    protected void onSaveInstanceState(Bundle storeInfo)
    {
        super.onSaveInstanceState(storeInfo);
        storeInfo.putInt("nowSize", fingerPainterView.getBrushWidth());
        storeInfo.putString("nowType", fingerPainterView.getBrush().toString());
        storeInfo.putInt("nowColor", fingerPainterView.getColour());
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            fingerPainterView.setBrushWidth(savedInstanceState.getInt("nowSize"));
            fingerPainterView.setColour(savedInstanceState.getInt("nowColor"));

            if(savedInstanceState.getString("nowType").equals("ROUND"))
                fingerPainterView.setBrush(Paint.Cap.ROUND);
            else if (savedInstanceState.getString("nowType").equals("SQUARE"))
                fingerPainterView.setBrush(Paint.Cap.SQUARE);

        }
    }
}



