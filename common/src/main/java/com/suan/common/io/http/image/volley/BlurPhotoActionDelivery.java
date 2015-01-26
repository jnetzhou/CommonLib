package com.suan.common.io.http.image.volley;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.suan.common.component.BaseApplication;
import com.suan.common.io.http.image.Photo;
import com.suan.common.ui.blur.Blur;
import com.suan.common.util.BitmapUtil;

/**
 * Created by suanmiao on 15/1/24.
 */
public class BlurPhotoActionDelivery extends BaseCachePhotoActionDelivery {
  private Photo photo;
  protected static final String KEY_CONTENT_LENGTH = "Content-Length";
  public static final String BLUR_SUFFIX = "_blur";

  public BlurPhotoActionDelivery(Photo photo) {
    this.photo = photo;
  }

  @Override
  public Response<Photo> parseNetworkResponse(NetworkResponse response) {
    try {
      /**
       * get content length
       */
      String contentLength = response.headers.get(KEY_CONTENT_LENGTH);
      if (!TextUtils.isEmpty(contentLength)) {
        photo.setContentLength(Integer.parseInt(contentLength));
      }
      response.headers.get(KEY_CONTENT_LENGTH);
      Bitmap result = BitmapUtil.decodePhoto(response.data, photo);
      if (result != null) {
        getCacheManager().put(photo.getUrl(), result, true);
      }
      Bitmap blurBitmap = Blur.apply(BaseApplication.getAppContext(), result);
      if (blurBitmap != null) {
        getCacheManager().put(photo.getUrl() + BLUR_SUFFIX, blurBitmap, true);
      }
      photo.setContent(blurBitmap);
      return Response.success(photo, HttpHeaderParser.parseCacheHeaders(response));
    } catch (Exception e) {
      Log.e("SUAN", "photo parse error " + e);
      return Response.error(new ParseError());
    }

  }
}