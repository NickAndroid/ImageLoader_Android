/*
 * Copyright (c) 2016 Nick Guo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nick.android.imageloader.worker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;

import java.util.concurrent.atomic.AtomicBoolean;

import dev.nick.android.imageloader.LoaderConfig;
import dev.nick.android.imageloader.worker.result.Cause;
import dev.nick.android.imageloader.worker.result.ErrorListener;
import dev.nick.logger.Logger;
import dev.nick.logger.LoggerManager;

public class BaseMediaFetcher<T> implements MediaFetcher<T> {

    protected static final int UNCONSTRAINED = -1;

    /* Maximum pixels size for created bitmap. */
    protected static final int MAX_NUM_PIXELS_THUMBNAIL = 512 * 512;

    protected PathSplitter<String> mSplitter;

    protected Context mContext;

    protected LoaderConfig mLoaderConfig;

    protected Logger mLogger;

    private AtomicBoolean mPrepared;

    public BaseMediaFetcher(PathSplitter<String> splitter) {
        this.mSplitter = splitter;
        this.mPrepared = new AtomicBoolean(Boolean.FALSE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    static Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    protected static Bitmap getBitmap(Context context, @DrawableRes int drawableResId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat) {
            return getBitmap((VectorDrawableCompat) drawable);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

    @Override
    public T fetchFromUrl(@NonNull String url,
                          @NonNull DecodeSpec decodeSpec,
                          @Nullable ProgressListener<T> progressListener,
                          @Nullable ErrorListener errorListener)
            throws Exception {
        if (!mPrepared.get()) throw new IllegalStateException("Fetcher not prepared.");
        mLogger.funcEnter();
        return null;
    }

    @Override
    public MediaFetcher<T> prepare(Context context, LoaderConfig config) {
        if (mPrepared.compareAndSet(false, true)) {
            this.mContext = context;
            this.mLoaderConfig = config;
            this.mLogger = LoggerManager.getLogger(getClass());
        }
        return this;
    }

    protected void callOnStart(ProgressListener<T> listener) {
        if (listener != null) listener.onStartLoading();
    }

    protected void callOnComplete(ProgressListener<T> listener, T result) {
        if (listener != null) listener.onComplete(result);
    }

    protected void callOnError(ErrorListener listener, @NonNull Cause cause) {
        if (listener != null) listener.onError(cause);
    }

    protected void callOnProgress(ProgressListener<T> listener, int progress) {
        if (listener != null) listener.onProgressUpdate(progress);
    }

    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    protected int computeSampleSize(BitmapFactory.Options options,
                                    int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private int computeInitialSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) &&
                (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    @Override
    public void terminate() {
        // PlaceHolder
    }
}
