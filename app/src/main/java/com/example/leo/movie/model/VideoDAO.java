package com.example.leo.movie.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.leo.movie.database.MovieContract;
import com.example.leo.movie.model.generated.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 02/01/2018.
 */

public class VideoDAO extends DAO {
    private static final String[] VIDEO_COLUMNS = {
            MovieContract.VideoEntry._ID,
            MovieContract.VideoEntry.VIDEO_ID_COLUMN,
            MovieContract.VideoEntry.KEY_COLUMN,
            MovieContract.VideoEntry.NAME_COLUMN,
            MovieContract.VideoEntry.TYPE_COLUMN,
            MovieContract.VideoEntry.MOVIE_ID_KEY_COLUMN
    };

    private static final int COL_VIDEO_ID = 1;
    private static final int COL_VIDEO_KEY = 2;
    private static final int COL_VIDEO_NAME = 3;
    private static final int COL_VIDEO_TYPE = 4;
    private static final int COL_VIDEO_MOVIE_ID = 5;

    public VideoDAO(Context context) {
        super(context);
    }

    public List<Video> getVideos(long movieId) {
        List<Video> videos = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(MovieContract.VideoEntry.buildVideoUriWithMovieId(movieId),
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Video video = new Video();
                video.id = cursor.getString(cursor.getColumnIndex(VIDEO_COLUMNS[COL_VIDEO_ID]));
                video.key = cursor.getString(cursor.getColumnIndex(VIDEO_COLUMNS[COL_VIDEO_KEY]));
                video.name = cursor.getString(cursor.getColumnIndex(VIDEO_COLUMNS[COL_VIDEO_NAME]));
                video.type = cursor.getString(cursor.getColumnIndex(VIDEO_COLUMNS[COL_VIDEO_TYPE]));

                video.movieId = movieId;

                videos.add(video);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return videos;
    }

    public void saveVideos(List<Video> videos, long movieId) {
        ContentValues[] values = new ContentValues[videos.size()];
        for (int i = 0; i < videos.size(); i++) {
            Video video = videos.get(i);

            ContentValues value = new ContentValues();
            value.put(VIDEO_COLUMNS[COL_VIDEO_MOVIE_ID], movieId);
            value.put(VIDEO_COLUMNS[COL_VIDEO_ID], video.id);
            value.put(VIDEO_COLUMNS[COL_VIDEO_KEY], video.key);
            value.put(VIDEO_COLUMNS[COL_VIDEO_NAME], video.name);
            value.put(VIDEO_COLUMNS[COL_VIDEO_TYPE], video.type);

            values[i] = value;
        }

        mContext.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, values);
    }
}
