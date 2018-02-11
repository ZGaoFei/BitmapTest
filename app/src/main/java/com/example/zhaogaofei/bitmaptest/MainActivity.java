package com.example.zhaogaofei.bitmaptest;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private ImageView ivChange;
    private TextView tvShow;

    private float[] eventXY = new float[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        TextView textView = (TextView) findViewById(R.id.tv_click);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Bitmap.Config config = bitmap.getConfig();

                byte[] bytes = bitmapToArray(bitmap);
                printArray(bytes, "default", false);

                Log.e("====", "===width==" + width + "==height==" + height + "==config==" + config);

                Bitmap toBitmap = arrayToBitmap(bytes, width, height, config);
                imageView.setImageBitmap(toBitmap);

//                changeArrayValues(bytes);
                changeArrayValue(bytes);
                printArray(bytes, "change", false);

                Bitmap toBitmap2 = arrayToBitmap(bytes, width, height, config);
                ivChange.setImageBitmap(toBitmap2);
            }
        });

        imageView = (ImageView) findViewById(R.id.iv_now);
        imageView.setOnTouchListener(imgSourceOnTouchListener);

        ivChange = (ImageView) findViewById(R.id.iv_change);

        tvShow = (TextView) findViewById(R.id.tv_show_RGB);
    }

    // 将bitmap转化为byte数组
    private byte[] bitmapToArray(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();

        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buf);
        byte[] byteArray = buf.array();
        return byteArray;
    }

    // 将byte数组转换为bitmap
    private Bitmap arrayToBitmap(byte[] byteArray, int width, int height, Bitmap.Config type) {
        Bitmap stitchBmp = Bitmap.createBitmap(width, height, type);
        stitchBmp.copyPixelsFromBuffer(ByteBuffer.wrap(byteArray));

        return stitchBmp;
    }

    // 修改数组里面的值
    private void changeArrayValue(byte[] byteArray) {
        int flag = 0;
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] != 0 && flag <= 440) {
                byteArray[i] = (byte) (byteArray[i] >> 1);
                flag++;
            }
        }
    }

    // 修改数组里面的值
    private void changeArrayValues(byte[] byteArray) {
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] != 0) {
                byteArray[i] = (byte) (byteArray[i] >> 1);
            }
        }
    }

    /**
     * 为0是否输出
     * isZero为true，则输出，否则不输出
     *
     * @param isZero is zero
     */
    private void printArray(byte[] byteArray, String tag, boolean isZero) {
        for (byte b : byteArray) {
            if (isZero || b != 0) {
//                Log.e("====" + tag + "===", "=====" + b);
            }
        }
    }

    // 获取坐标位置的像素值
    private int getPixel(View view, float[] eventXY) {
        Matrix invertMatrix = new Matrix();
        ((ImageView) view).getImageMatrix().invert(invertMatrix);

        invertMatrix.mapPoints(eventXY);
        int x = Integer.valueOf((int) eventXY[0]);
        int y = Integer.valueOf((int) eventXY[1]);

        Drawable imgDrawable = ((ImageView) view).getDrawable();
        Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();

        //Limit x, y range within bitmap
        if (x < 0) {
            x = 0;
        } else if (x > bitmap.getWidth() - 1) {
            x = bitmap.getWidth() - 1;
        }

        if (y < 0) {
            y = 0;
        } else if (y > bitmap.getHeight() - 1) {
            y = bitmap.getHeight() - 1;
        }

        int touchedRGB = bitmap.getPixel(x, y);
        return touchedRGB;
    }

    // 将像素值转为ARGB值
    private int getRGB(View view, float[] eventXY) {
        int touchedRGB = getPixel(view, eventXY);
        int red = Color.red(touchedRGB);
        int green = Color.green(touchedRGB);
        int blue = Color.blue(touchedRGB);
        int alpha = Color.alpha(touchedRGB);

        Log.e("====", "==red==" + red + "==green==" + green + "==blue==" + blue + "==alpha==" + alpha);

        return touchedRGB;
    }

    // 修改坐标位置的颜色
    private void setRGB(View view, float[] eventXY, int color) {
        Matrix invertMatrix = new Matrix();
        ((ImageView) view).getImageMatrix().invert(invertMatrix);

        invertMatrix.mapPoints(eventXY);
        int x = Integer.valueOf((int) eventXY[0]);
        int y = Integer.valueOf((int) eventXY[1]);

        Drawable imgDrawable = ((ImageView) view).getDrawable();
        Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();

        //Limit x, y range within bitmap
        if (x < 0) {
            x = 0;
        } else if (x > bitmap.getWidth() - 1) {
            x = bitmap.getWidth() - 1;
        }

        if (y < 0) {
            y = 0;
        } else if (y > bitmap.getHeight() - 1) {
            y = bitmap.getHeight() - 1;
        }

        bitmap.setPixel(x, y, color);
    }

    private void setRGB(View view, float[] eventXY, int red, int green, int blue, int alpha) {
        int argb = Color.argb(alpha, red, green, blue);
        setRGB(view, eventXY, argb);
    }

    View.OnTouchListener imgSourceOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            eventXY[0] = eventX;
            eventXY[1] = eventY;

            int rgb = getRGB(view, eventXY);
            tvShow.setText(String.valueOf(rgb));
//            setRGB(view, eventXY, 0, 0, 0, 0);
            setRGB(view, eventXY, Color.RED);

            return true;
        }
    };
}
