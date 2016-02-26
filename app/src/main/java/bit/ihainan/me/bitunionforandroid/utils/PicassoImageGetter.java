package bit.ihainan.me.bitunionforandroid.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Picasso Image Getter
 */
public class PicassoImageGetter implements Html.ImageGetter {
    public final static String TAG = PicassoImageGetter.class.getSimpleName();
    private final Resources resources;
    private Context mContext;
    private final TextView textView;

    public PicassoImageGetter(Context context, TextView textView) {
        mContext = context;
        resources = context.getResources();
        this.textView = textView;
    }

    private void loadImage(final String source, final BitmapDrawablePlaceHolder result) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                // 获取图片
                try {
                    // Wifi 条件或者非省流量条件下加载图片
                    if (CommonUtils.isWifi(mContext) || !Global.saveDataMode) {
                        Log.d(TAG, "loadImage >> 非节省流量模式或者 Wi-Fi 环境，正常下载图片 " + source);
                        Picasso picasso = Picasso.with(mContext);
                        return picasso.load(source).get();
                    } else {
                        Log.d(TAG, "loadImage >> 节省流量模式且非 Wi-Fi 环境，不下载图片 " + source);
                        return null;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "getDrawable >> failed to load image for IOException " + source);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                final BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                result.setDrawable(drawable);
                result.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                textView.setText(textView.getText());
            }
        }.execute((Void) null);
    }

    @Override
    public Drawable getDrawable(String source) {
        final BitmapDrawablePlaceHolder result = new BitmapDrawablePlaceHolder();

        source = CommonUtils.getRealImageURL(source);

        loadImage(source, result);

        return result;
    }

    public static class BitmapDrawablePlaceHolder extends BitmapDrawable {
        protected Drawable mDrawable;

        @Override
        public void draw(Canvas canvas) {
            if (mDrawable != null) {
                mDrawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.mDrawable = drawable;
        }
    }
}
