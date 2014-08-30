package com.apperall.gabe.tvguide;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class TVGuideProvider extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("content://com.apperall.gabe.tvguide.tvguideprovider");
    public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.gabe.tvguide.programme";
    public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.gabe.tvguide.programmes";

    private TVGuideDbHelper dbHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int nDeleted = db.delete(dbHelper.TABLE_PROGRAMMES, selection, selectionArgs );
        getContext().getContentResolver().notifyChange(uri,null);
        return nDeleted;
    }

    @Override
    public String getType(Uri uri) {
        if (uri.getLastPathSegment()==null) {
            return SINGLE_RECORD_MIME_TYPE;
        } else {
            return MULTIPLE_RECORDS_MIME_TYPE;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id = db.insertOrThrow(dbHelper.TABLE_PROGRAMMES, null, values);

        if (id != -1) {
            uri = Uri.withAppendedPath(uri, String.valueOf(id));
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return uri;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(dbHelper.TABLE_PROGRAMMES, null, value);
                if (_id!=-1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnCount;

    }

    @Override
    public boolean onCreate() {
        dbHelper = new TVGuideDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor retCursor;
        long id = getId(uri);
        if (id<0) {
            retCursor = db.query(dbHelper.TABLE_PROGRAMMES, projection, selection, selectionArgs, null, null, sortOrder);

        } else {
            retCursor = db.query(dbHelper.TABLE_PROGRAMMES, projection, TVGuideDbHelper.C_PROGRAMME_ID + " = "+id, selectionArgs, null, null, sortOrder);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private long getId(Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment != null) {
            try {
                return Long.parseLong(lastPathSegment);
            } catch (NumberFormatException e) {

            }
        }
        return -1;

    }



    private class TVGuideDbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "tvguide.db";
        private static final int DATABASE_VERSION = 2;

        public static final String TABLE_PROGRAMMES = "programmes";
        public static final String C_PROGRAMME_ID = "_id";
        public static final String C_PROGRAMME_TITLE = "title";
        public static final String C_PROGRAMME_START = "start";
        public static final String C_PROGRAMME_STOP = "stop";
        public static final String C_PROGRAMME_DESC = "desc";
        public static final String C_PROGRAMME_CHANNEL_ID = "channelId";
        public static final String C_PROGRAMME_CHANNEL_NAME = "channel";
        public static final String C_PROGRAMME_CATEGORY = "category";
        public static final String C_PROGRAMME_LENGTH = "length";
        public static final String C_PROGRAMME_SHOW = "show";
        public static final String C_PROGRAMME_URI = "uri";


        private static final String TABLE_CHANNELS = "channels";

        public static final String C_CHANNEL_ID = "_id";
        public static final String C_CHANNEL_NAME = "name";
        private static final String C_CHANNEL_EXT_ID = "ext_id";


        // Database creation sql statement
        private static final String TABLE_PROGRAMMES_CREATE = "create table "
                + TABLE_PROGRAMMES + "(" + C_PROGRAMME_ID
                + " integer primary key autoincrement, " + C_PROGRAMME_TITLE
                + " text not null, "
                + C_PROGRAMME_START + " text, "
                + C_PROGRAMME_STOP + " text, "
                + C_PROGRAMME_DESC + " text, "
                + C_PROGRAMME_CHANNEL_ID + " text, "
                + C_PROGRAMME_CHANNEL_NAME + " text, "
                + C_PROGRAMME_CATEGORY + " text, "
                + C_PROGRAMME_LENGTH + " text, "
                + C_PROGRAMME_SHOW + " integer, "
                + C_PROGRAMME_URI + " text "

                +");";

        private static final String TABLE_CHANNELS_CREATE = "create table "
                + TABLE_CHANNELS + "(" + C_CHANNEL_ID
                + " integer primary key autoincrement, " + C_CHANNEL_NAME
                + " text not null, "
                + C_CHANNEL_EXT_ID + " text, "
                + C_PROGRAMME_STOP + " text, "
                + C_PROGRAMME_DESC + " text, "
                + C_PROGRAMME_CHANNEL_ID + " text "
                +");";

        public TVGuideDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_PROGRAMMES_CREATE);
            db.execSQL(TABLE_CHANNELS_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAMMES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNELS);
        }
    }



}
