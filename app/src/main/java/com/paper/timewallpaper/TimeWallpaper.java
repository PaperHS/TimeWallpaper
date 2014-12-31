package com.paper.timewallpaper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.text.format.Time;
import android.view.SurfaceHolder;

/**
 * ,==.              |~~~
 * /  66\             |
 * \c  -_)         |~~~
 * `) (           |
 * /   \       |~~~
 * /   \ \      |
 * ((   /\ \_ |~~~
 * \\  \ `--`|
 * / / /  |~~~
 * ___ (_(___)_|
 * <p/>
 * Created by Paper on 14-12-31 2014.
 */
public class TimeWallpaper extends WallpaperService{
    private final Handler mHandler = new Handler();


    @Override
    public Engine onCreateEngine() {
        return new TimeEngine();
    }

    class TimeEngine extends Engine{

        private final Paint mPaint = new Paint();
        private long mStartTime;
        private boolean mVisible;
        private final Runnable mDrawCube = new Runnable() {
            public void run() {
                drawFrame();
            }
        };

        TimeEngine(){
            final Paint paint = mPaint;
            paint.setColor(0xffffffff);//画笔颜色
            paint.setAntiAlias(true);//抗锯齿
            paint.setStrokeWidth(2);//线条粗细，猜的，不知道对不对
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);
            //系统启动完之后，开始绘制壁纸的时间，这个时间里面包含有系统睡眠时间
            mStartTime = SystemClock.elapsedRealtime();
        }
        private void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    Time time = new Time();
                    time.setToNow();
                    c.save();
                    c.drawRGB(time.hour*10,time.minute*4,time.second*4);
                    c.restore();
                }
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }
            mHandler.removeCallbacks(mDrawCube);
            if (mVisible) {
                mHandler.postDelayed(mDrawCube, 1000);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDrawCube);
        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            /*下面这个判断好玩，就是说，如果屏幕壁纸状态转为显式时重新绘制壁纸，否则黑屏幕，隐藏就可以*/
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawCube);
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawCube);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }
    }
}
