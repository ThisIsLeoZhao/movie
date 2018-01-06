package com.example.leo.movie.network;

import com.example.leo.movie.IResponseCallback;
import com.google.gson.Gson;

/**
 * Created by Leo on 04/01/2018.
 */

public class ResponseHandler<T> {
    private IResponseCallback<T> mResponseCallback;
    private Class<T> mSuccessType;

    public ResponseHandler(Class<T> successType, IResponseCallback<T> responseCallback) {
        mResponseCallback = responseCallback;
        mSuccessType = successType;
    }

    public void success(String response) {
        T result = new Gson().fromJson(response, mSuccessType);
        mResponseCallback.success(result);
    }

    public void fail(String reason) {
        mResponseCallback.fail(reason);
    }
}
