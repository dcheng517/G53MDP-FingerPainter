package com.example.fingerpainter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class SelectColour extends AppCompatActivity {

    private SeekBar redBar;
    private SeekBar greenBar;
    private SeekBar blueBar;

    private EditText editRed;
    private EditText editGreen;
    private EditText editBlue;

    private ImageView colorPreview;

    private int redProgress;
    private int greenProgress;
    private int blueProgress;

    private int finalRGB[] = new int[3]; // final colour to be sent back

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_colour);

        Bundle oldColor = getIntent().getExtras();
        if (oldColor == null)
            return;

//        set color preview to current color
        colorPreview = (ImageView) findViewById(R.id.colorPreview);
        colorPreview.setColorFilter(oldColor.getInt("nowColor"));

//        Get current color
        String nowColor = Integer.toHexString(oldColor.getInt("nowColor")).substring(2);
        int colorCurr[] = hexToInt(nowColor);

//        SeekBar methods
        redBar = (SeekBar) findViewById(R.id.redBar);
        redBar.setProgress(colorCurr[0]);
        greenBar = (SeekBar) findViewById(R.id.greenBar);
        greenBar.setProgress(colorCurr[1]);
        blueBar = (SeekBar) findViewById(R.id.blueBar);
        blueBar.setProgress(colorCurr[2]);

        redBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                redProgress = progress;
                editRed.setText(""+redProgress);
                colorPreview.setColorFilter(previewColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        greenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                greenProgress = progress;
                editGreen.setText(""+greenProgress);
                colorPreview.setColorFilter(previewColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        blueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blueProgress = progress;
                editBlue.setText(""+blueProgress);
                colorPreview.setColorFilter(previewColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

//        EditText methods
        editRed = (EditText) findViewById(R.id.editRed);
        editRed.setText(""+colorCurr[0]);
        editGreen = (EditText) findViewById(R.id.editGreen);
        editGreen.setText(""+colorCurr[1]);
        editBlue = (EditText) findViewById(R.id.editBlue);
        editBlue.setText(""+colorCurr[2]);

        editRed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int red;
                try{
                    red = Integer.parseInt(s.toString());
                }
                catch(NumberFormatException ex) {
                    return;
                }
                redBar.setProgress(red);
            }
        });

        editGreen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int green;
                try{
                    green = Integer.parseInt(s.toString());
                }
                catch(NumberFormatException ex) {
                    return;
                }
                greenBar.setProgress(green);
            }
        });

        editBlue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int blue;
                try{
                    blue = Integer.parseInt(s.toString());
                }
                catch(NumberFormatException ex) {
                    return;
                }
                blueBar.setProgress(blue);
            }
        });

    }

//    Preset color buttons
    public void onClickColour(View view)
    {
        int id = view.getId(); // id of button pressed

//        get color of button pressed
        Button colorButton = (Button) findViewById(id);
        ColorDrawable buttonColor = (ColorDrawable) colorButton.getBackground();
        String color = Integer.toHexString(buttonColor.getColor()).substring(2);
        colorPreview.setColorFilter(buttonColor.getColor()); // set preview to color of button pressed

        int rgb[] = hexToInt(color);

//        Set rgb values for editText views
        editRed.setText("" + rgb[0]);
        editGreen.setText("" + rgb[1]);
        editBlue.setText("" + rgb[2]);
    }

    public int previewColor()
    {
        int tempColor = Color.rgb(0, 0, 0);
        try
        {
            tempColor = Color.rgb(redBar.getProgress(), greenBar.getProgress(), blueBar.getProgress());
        } catch(NullPointerException e) {
            return tempColor;
        }

        return tempColor;
    }

//    convert rgb values to hex values
    public static String intToHex(int[] rgb)
    {
        String ret = "";
        for (int i = 0; i < 3 ; i++)
        {
            String temp = String.format("%02X", rgb[i]);
            ret += temp;
        }

        return ret;
    }

//    convert rgb hex values to integer values
    public static int[] hexToInt(String hexColor)
    {
        String redVal = hexColor.substring(0, 2);
        int red = Integer.parseInt(redVal, 16);
        String greenVal = hexColor.substring(2, 4);
        int green = Integer.parseInt(greenVal, 16);
        String blueVal = hexColor.substring(4);
        int blue = Integer.parseInt(blueVal, 16);

        int[] ret = {red, green, blue};
        return ret;
    }

    public void onClickSubmit(View view)
    {
        String tempRed = editRed.getText().toString();
        finalRGB[0] = Integer.parseInt(tempRed);
        String tempGreen = editGreen.getText().toString();
        finalRGB[1] = Integer.parseInt(tempGreen);
        String tempBlue = editBlue.getText().toString();
        finalRGB[2] = Integer.parseInt(tempBlue);

        Intent result = new Intent();
        Bundle colorBundle = new Bundle();

        String newColor = "#FF" + intToHex(finalRGB);
        colorBundle.putString("newColor", newColor);
        result.putExtras(colorBundle);

        setResult(RESULT_OK, result);
        finish();
    }

}
