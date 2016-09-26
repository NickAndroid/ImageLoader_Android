package dev.nick.android.imageloader.worker.drawable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import dev.nick.android.imageloader.worker.ProgressListener;

public class OverlayProgressListener implements ProgressListener<Bitmap> {

    ProgressListener<Drawable> drawableProgressListener;

    public OverlayProgressListener(ProgressListener<Drawable> drawableProgressListener) {
        this.drawableProgressListener = drawableProgressListener;
    }

    public void onComplete(Drawable result) {
        drawableProgressListener.onComplete(result);
    }

    @Override
    public void onStartLoading() {
        drawableProgressListener.onStartLoading();
    }

    @Override
    public void onProgressUpdate(float progress) {
        drawableProgressListener.onProgressUpdate(progress);
    }

    @Override
    public void onCancel() {
        drawableProgressListener.onCancel();
    }

    @Override
    public void onComplete(Bitmap result) {
        result = null;
    }
}
