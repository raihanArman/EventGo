package com.example.eventgoapps.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.eventgoapps.R;


public class GlideBindingAdapters {
    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView view, String url){
        Context context = view.getContext();
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background);
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .asBitmap()
                .load(url)
                .into(view);
    }


    @BindingAdapter("photoUserUrl")
    public static void setPhotoUSerUrl(ImageView view, String url){
        Context context = view.getContext();
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.person_male)
                .error(R.drawable.person_male);
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .asBitmap()
                .load(url)
                .into(view);
    }

    @BindingAdapter("imageRes")
    public static void setImageResource(ImageView view, int res){
        Context context = view.getContext();
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .override(200, 200);
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .asBitmap()
                .load(res)
                .into(view);
    }

    @BindingAdapter("imageBitmap")
    public static void setImageBitmap(ImageView imageView, String image){
        byte[] decodeString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodeByte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        imageView.setImageBitmap(decodeByte);
    }
}
