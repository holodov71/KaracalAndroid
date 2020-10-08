package app.karacal.helpers;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import app.karacal.R;

public class ImageHelper {

    public static void setImage(ImageView imageView, String url, int imageRes, boolean isCircle) {
        try {
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(imageView.getContext());
            circularProgressDrawable.setStrokeWidth(4f);
            circularProgressDrawable.setCenterRadius(20f);

            DrawableCrossFadeFactory factory =
                    new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

            int color = ContextCompat.getColor(imageView.getContext(), R.color.colorAccent);
            setColorFilter(circularProgressDrawable, color);

            circularProgressDrawable.start();

            RequestOptions options = new RequestOptions()
                    .placeholder(circularProgressDrawable);

            RequestManager manager = Glide.with(imageView.getContext());

            RequestBuilder builder;
            if (url != null){
                builder = manager
                        .load(url);
            } else {
                builder = manager
                        .load(imageRes);
            }

            builder = builder.transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options);

//            builder = builder.transition(DrawableTransitionOptions.withCrossFade())
//                    .apply(options);

            if (isCircle) {
                builder = builder.apply(RequestOptions.circleCropTransform());
            }

            builder.listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    Log.v("builder.listener", "onLoadFailed");
                    imageView.setImageResource(R.drawable.fade);
                    return true;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(imageView);

        }catch (Exception ex){
            ex.printStackTrace();
            imageView.setImageResource(R.drawable.fade);
        }
    }

    public static void setColorFilter(Drawable drawable, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_IN));
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
}
