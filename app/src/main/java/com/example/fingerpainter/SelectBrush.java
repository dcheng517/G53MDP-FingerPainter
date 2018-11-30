package com.example.fingerpainter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

public class SelectBrush extends AppCompatActivity {
    private EditText sizeInput;
    private SeekBar sizeBar;
    private ImageView brushPreview;

    private RadioGroup typeRadio;

    private String typeNew;

//    brush preview
    final Bitmap preview = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(preview);
    final Paint prePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_brush);

        brushPreview = (ImageView) findViewById(R.id.brushPreview);

        Bundle oldBrush = getIntent().getExtras();
        if (oldBrush == null)
                return;

        prePaint.setColor(oldBrush.getInt("nowColor"));

        sizeBar = (SeekBar) findViewById(R.id.sizeSlider);
        sizeBar.setProgress(oldBrush.getInt("nowSize")); // show current brush size

//        seekBar methods
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeInput.setText(""+progress);
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                prePaint.setStrokeWidth(progress/5);
                if (typeRadio.getCheckedRadioButtonId() == R.id.round)
                {
                    prePaint.setStrokeCap(Paint.Cap.ROUND);
                } else if(typeRadio.getCheckedRadioButtonId() == R.id.square)
                {
                    prePaint.setStrokeCap(Paint.Cap.SQUARE);
                }
                canvas.drawPoint(50, 50, prePaint);
                brushPreview.setImageBitmap(preview);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

//        editText
        sizeInput = (EditText)findViewById(R.id.editSize);
        sizeInput.setText(""+oldBrush.getInt("nowSize")); // show current brush size

//         editText methods
        sizeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                int size;
                try{
                    size = Integer.parseInt(s.toString());
                }
                catch(NumberFormatException ex) {
                    return;
                }
                sizeBar.setProgress(size);
            }
        });

//        Brush Type method
        typeRadio = (RadioGroup) findViewById(R.id.typeRadio);

//        current brush type
        if(oldBrush.getString("nowType").equals("ROUND"))
            typeRadio.check(R.id.round);
        else if(oldBrush.getString("nowType").equals("SQUARE"))
            typeRadio.check(R.id.square);

        typeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(group.getCheckedRadioButtonId() == R.id.round)
                    typeNew = "ROUND";

                if(group.getCheckedRadioButtonId() == R.id.square)
                    typeNew = "SQUARE";
            }
        });
    }

    public void onClick(View view)
    {
        Intent result = new Intent();
        Bundle newBrush = new Bundle();
        newBrush.putInt("newSize", sizeBar.getProgress());
        Log.d("progressD", "" + sizeBar.getProgress());
        newBrush.putString("newType", typeNew);
        result.putExtras(newBrush);
        setResult(RESULT_OK, result);
        finish();
    }

}
