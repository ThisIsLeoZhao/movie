package com.example.leo.movie;

/**
 * Created by Leo on 16/12/2017.
 */

public interface IResponseCallback<T> {
    public void success(T response);

    void fail(String reason);
}
