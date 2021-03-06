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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Optional;

import dev.nick.android.imageloader.queue.Priority;
import dev.nick.android.imageloader.ui.DisplayOption;
import dev.nick.android.imageloader.ui.MediaHolder;
import dev.nick.android.imageloader.utils.Preconditions;
import dev.nick.android.imageloader.worker.MediaData;
import dev.nick.android.imageloader.worker.MediaSource;
import dev.nick.android.imageloader.worker.ProgressListener;
import dev.nick.android.imageloader.worker.result.ErrorListener;

public abstract class Transaction<T> {

    protected MediaData<T> mediaData;
    protected Optional<DisplayOption<T>> option = Optional.absent();
    protected ProgressListener<T> progressListener;
    protected ErrorListener errorListener;
    protected Priority priority;
    protected MediaHolder<T> settable;

    protected ImageLoader accessory;

    Transaction(@NonNull ImageLoader accessory) {
        this.accessory = accessory;
    }

    /**
     * @param url Image source from, one of {@link MediaSource}
     * @return Instance of Transaction.
     */
    public Transaction<T> from(@NonNull String url) {
        MediaSource<T> type = onCreateSource(url);
        this.mediaData = new MediaData<>(Preconditions.checkNotNull(type, "Unknown image source"), url);
        return Transaction.this;
    }

    abstract MediaSource<T> onCreateSource(String url);

    /**
     * @param option {@link DisplayOption} is options using when display the image.
     *               * @param settable Target {@link MediaHolder} to display the image.
     * @return Instance of Transaction.
     */
    public Transaction<T> option(@NonNull DisplayOption<T> option) {
        this.option = Optional.of(option);
        return Transaction.this;
    }

    /**
     * @param listener The {@link ProgressListener} using to watch the progress of the loading.
     * @return Instance of Transaction.
     */
    public Transaction<T> progressListener(@NonNull ProgressListener<T> listener) {
        this.progressListener = Preconditions.checkNotNull(listener);
        return Transaction.this;
    }

    /**
     * @param listener The {@link ErrorListener} using to watch the progress of the loading.
     * @return Instance of Transaction.
     */
    public Transaction<T> errorListener(@NonNull ErrorListener listener) {
        this.errorListener = Preconditions.checkNotNull(listener);
        return Transaction.this;
    }

    /**
     * @param priority Priority for this task.
     * @return Instance of Transaction.
     */
    public Transaction<T> priority(@NonNull Priority priority) {
        this.priority = Preconditions.checkNotNull(priority);
        return Transaction.this;
    }

    /**
     * @param settable The View to display the image.
     * @return Instance of Transaction.
     */
    public Transaction<T> into(@NonNull MediaHolder<T> settable) {
        this.settable = Preconditions.checkNotNull(settable);
        return Transaction.this;
    }

    /**
     * Call this to start this transaction.
     */
    public void start() {
        this.accessory.mTransactionService.push(this);
    }

    /**
     * Call this to start this transaction synchronously.
     *
     * @return Result for this transaction.
     */
    public
    @Nullable
    abstract T startSynchronously();

    abstract void startAsync();
}
