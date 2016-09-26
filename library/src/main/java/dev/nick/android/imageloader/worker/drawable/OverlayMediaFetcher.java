package dev.nick.android.imageloader.worker.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import dev.nick.android.imageloader.worker.BaseMediaFetcher;
import dev.nick.android.imageloader.worker.DecodeSpec;
import dev.nick.android.imageloader.worker.ProgressListener;
import dev.nick.android.imageloader.worker.bitmap.BitmapSource;
import dev.nick.android.imageloader.worker.result.ErrorListener;

public class OverlayMediaFetcher extends BaseMediaFetcher<Drawable> {

    private BitmapSource bitmapSource;

    public OverlayMediaFetcher(@NonNull BitmapSource bitmapSource) {
        super(null);
        this.bitmapSource = bitmapSource;
    }

    static Drawable bitmapToDrawable(@NonNull Bitmap bitmap, @NonNull Resources resources) {
        return new BitmapDrawable(resources, bitmap);
    }

    @Override
    public Drawable fetchFromUrl(@NonNull String url, @NonNull DecodeSpec decodeSpec,
                                 @Nullable ProgressListener<Drawable> progressListener,
                                 @Nullable ErrorListener errorListener) throws Exception {

        Bitmap out = bitmapSource.getFetcher(mContext, mLoaderConfig).fetchFromUrl(url, decodeSpec,
                new OverlayProgressListener(progressListener), errorListener);
        if (out == null) return null;
        return bitmapToDrawable(out, mContext.getResources());
    }
}
