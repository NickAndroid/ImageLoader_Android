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

package dev.nick.android.imageloader;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import dev.nick.android.imageloader.queue.Priority;
import dev.nick.android.imageloader.ui.DisplayOption;
import dev.nick.android.imageloader.ui.DrawableImageViewDelegate;
import dev.nick.android.imageloader.ui.MediaHolder;
import dev.nick.android.imageloader.worker.MediaSource;
import dev.nick.android.imageloader.worker.ProgressListener;
import dev.nick.android.imageloader.worker.drawable.DrawableSource;
import dev.nick.android.imageloader.worker.result.ErrorListener;

@Deprecated
public class DrawableTransaction extends Transaction<Drawable> {

    DrawableTransaction(@NonNull ImageLoader loader) {
        super(loader);
    }

    @Override
    public DrawableTransaction from(@NonNull String url) {
        super.from(url);
        return this;
    }

    @Override
    MediaSource<Drawable> onCreateSource(String url) {
        return DrawableSource.from(url);
    }

    @Override
    public DrawableTransaction option(@NonNull DisplayOption<Drawable> option) {
        super.option(option);
        return this;
    }

    @Override
    public DrawableTransaction progressListener(@NonNull ProgressListener<Drawable> listener) {
        super.progressListener(listener);
        return this;
    }

    @Override
    public DrawableTransaction errorListener(@NonNull ErrorListener listener) {
        super.errorListener(listener);
        return this;
    }

    @Override
    public DrawableTransaction priority(@NonNull Priority priority) {
        super.priority(priority);
        return this;
    }

    @Override
    public DrawableTransaction into(@NonNull MediaHolder<Drawable> settable) {
        super.into(settable);
        return this;
    }

    /**
     * @param view The View to display the image.
     * @return Instance of Transaction.
     */
    public DrawableTransaction into(@NonNull ImageView view) {
        this.settable = new DrawableImageViewDelegate(view);
        return DrawableTransaction.this;
    }


    @Nullable
    @Override
    public Drawable startSynchronously() {
        throw new UnsupportedOperationException();
    }

    @Override
    void startAsync() {
        throw new UnsupportedOperationException();
    }

}
