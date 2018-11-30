/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.fingerpainter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/*
 *
 * Derived from android graphics API sample com.example.android.apis.graphics.Fingerpaint
 * android.googlesource.com/platform/development/+/master/samples/ApiDemos/src/com/example/android/apis/graphics/FingerPaint.java
 *
 */

public class FingerPainterView extends View {

    private Context context;
    private Canvas canvas;
    public Paint paint;
    private Bitmap bitmap;
    private Path path;
    private Uri uri;

    private Bitmap bitmapBackup;

    public FingerPainterView(Context context) {
        super(context); // application context
        init(context);
    }

    public FingerPainterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FingerPainterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        path = new Path();
        paint = new Paint();

        // default brush style and colour
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(20);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setARGB(255,0,0,0);
    }

    public void setBrush(Paint.Cap brush) {
        paint.setStrokeCap(brush);
    }

    public Paint.Cap getBrush() {
        return paint.getStrokeCap();
    }

    public void setBrushWidth(int width) {
        paint.setStrokeWidth(width);
    }

    public int getBrushWidth() {
        return (int) paint.getStrokeWidth();
    }

    public void setColour(int colour) {
        paint.setColor(colour);
    }

    public int getColour() {
        return paint.getColor();
    }

    public void load(Uri uri) {
        this.uri = uri;
    }

//    Extra feature: clear the canvas
    public void clearScreen()
    {
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        invalidate();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        // save superclass view state
        bundle.putParcelable("superState", super.onSaveInstanceState());

        try {
            // save bitmap to temporary cache file to overcome binder transaction size limit
            File f = File.createTempFile("fingerpaint", ".png", context.getCacheDir());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(f));
            // save temporary filename to bundle
            bundle.putString("tempfile", f.getAbsolutePath());
        } catch(IOException e) {
            Log.e("FingerPainterView", e.toString());
        } catch(Exception e) {
            Log.e("FingerPainterView", e.toString());
        }
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            try {
                // load cache file from bundle stored filename
                File f = new File(bundle.getString("tempfile"));
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                // need to copy the bitmap to create a mutable version
                bitmap = b.copy(b.getConfig(), true);
                b.recycle();
                f.delete();
            } catch(IOException e) {
                Log.e("FingerPainterView", e.toString());
            }

            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // canvas is white with a bitmap with alpha channel drawn over the top
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        // show current drawing path
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // called after the activity has been created when the view is inflated
        if(bitmap==null) {
            if(uri!=null) {
                try {
                    // attempt to load the uri provided, scale to fit our canvas
                    InputStream stream = context.getContentResolver().openInputStream(uri);
                    Bitmap bm = BitmapFactory.decodeStream(stream);
                    bitmap  = Bitmap.createScaledBitmap(bm, Math.max(w, h), Math.max(w, h), false);
                    stream.close();
                    bm.recycle();
                } catch(IOException e) {
                    Log.e("FingerPainterView", e.toString());
                }
            }
            else {
                // create a square bitmap so is drawable even after rotation to landscape
                bitmap = Bitmap.createBitmap(Math.max(w,h), Math.max(w,h), Bitmap.Config.ARGB_8888);
            }
        }
        canvas = new Canvas(bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.reset();
                path.moveTo(x, y);
                path.lineTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, paint);
                path.reset();
                invalidate();
                break;
        }
        return true;
    }
}
