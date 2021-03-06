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

package dev.nick.android.imageloader.ui;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.widget.ImageView;

import dev.nick.android.imageloader.utils.Preconditions;

/**
 * Wrapper class for a {@link ImageView}
 */
public class DrawableImageViewDelegate implements MediaHolder<Drawable> {

    private ImageView mImageView;

    public DrawableImageViewDelegate(ImageView imageView) {
        this.mImageView = Preconditions.checkNotNull(imageView);
    }

    @Override
    public void seat(@NonNull Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mImageView.setBackground(drawable);
        } else {
            mImageView.setBackgroundDrawable(drawable);
        }
    }

    @Override
    public int getWidth() {
        return mImageView.getWidth();
    }

    @Override
    public int getHeight() {
        return mImageView.getHeight();
    }

    @Override
    public void startAnimation(Animation animation) {
        mImageView.clearAnimation();
        mImageView.startAnimation(animation);
    }

    @Override
    public int hashCode() {
        return mImageView.hashCode();
    }
}
