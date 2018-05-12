package com.example.leo.movie;


import com.example.leo.movie.database.Movie;

import java.util.List;

/**
 * Created by Leo on 22/12/2017.
 */

public interface IFetchMovieListener {
    public void onDone(List<Movie> movies);

    void onFailure(String reason);
}
