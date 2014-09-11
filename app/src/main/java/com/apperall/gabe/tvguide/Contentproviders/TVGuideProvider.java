package com.apperall.gabe.tvguide.Contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class TVGuideProvider extends ContentProvider {
    public static final String CONTENT_AUTHORITY = "com.apperall.gabe.tvguide.tvguideprovider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_PROGRAMME = "programmes";
    public static final String PATH_CHANNEL = "channels";
    public static final String PATH_STOREDQUERY = "storedqueries";


    public static final Uri PROGRAMME_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROGRAMME).build();
    public static final Uri CHANNEL_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHANNEL).build();


//    public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.gabe.tvguide.programme";
//    public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.gabe.tvguide.programmes";
    private static final String TAG = TVGuideProvider.class.getSimpleName();

    public static final String PROGRAMMES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gabe.tvguide.programmes";
    public static final String PROGRAMME_CONTENT_TYPE = "vnd.android.cursor.item/vnd.gabe.tvguide.programme";

    public static final String CHANNELS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gabe.tvguide.channels";
    public static final String CHANNEL_CONTENT_TYPE = "vnd.android.cursor.item/vnd.gabe.tvguide.channel";


    private static final int PROGRAMMES = 100;
    private static final int PROGRAMMES_ID = 101;

    private static final int CHANNELS = 110;
    private static final int CHANNELS_ID = 111;

    private static final int STOREDQUERY = 120;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private TVGuideDbHelper dbHelper;




    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(CONTENT_AUTHORITY, "programmes", PROGRAMMES);
        uriMatcher.addURI(CONTENT_AUTHORITY, "programmes/#", PROGRAMMES_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, "channels", CHANNELS);
        uriMatcher.addURI(CONTENT_AUTHORITY, "channels/#", CHANNELS_ID);

        return uriMatcher;
    }
    @Override
    public String getType(Uri uri) {

        /*
        if (uri.getLastPathSegment()==null) {
            return SINGLE_RECORD_MIME_TYPE;
        } else {
            return MULTIPLE_RECORDS_MIME_TYPE;
        }*/
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROGRAMMES:
                return PROGRAMMES_CONTENT_TYPE;
            case PROGRAMMES_ID:
                return PROGRAMME_CONTENT_TYPE;
            case CHANNELS:
                return CHANNELS_CONTENT_TYPE;
            case CHANNELS_ID:
                return CHANNEL_CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        String targetTable = "";
        switch (match) {
            case PROGRAMMES:
                targetTable = dbHelper.TABLE_PROGRAMMES;
                break;
            case CHANNELS:
                targetTable = dbHelper.TABLE_CHANNELS;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        int nDeleted = db.delete(targetTable, selection, selectionArgs );
        Log.i(TAG, "deleted "+ nDeleted+" items from "+targetTable);
        getContext().getContentResolver().notifyChange(uri,null);
        return nDeleted;
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case PROGRAMMES: {
                long id = db.insertOrThrow(dbHelper.TABLE_PROGRAMMES, null, values);

                if (id > 0) {
                    uri = Uri.withAppendedPath(uri, String.valueOf(id));
                }  else
                    throw new SQLException("failed to insert row into "+uri);
                break;
            }
            case CHANNELS: {
                long id = db.insertOrThrow(dbHelper.TABLE_CHANNELS, null, values);

                if (id > 0) {
                    uri = Uri.withAppendedPath(uri, String.valueOf(id));
                }  else
                    throw new SQLException("failed to insert row into "+uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return uri;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        String targetTable = "";
        switch (match) {
            case PROGRAMMES:
                targetTable = dbHelper.TABLE_PROGRAMMES;
                break;
            case CHANNELS:
                targetTable = dbHelper.TABLE_CHANNELS;
            default:
                return super.bulkInsert(uri, values);

        }

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(targetTable, null, value);
                if (_id!=-1) {
                    returnCount++;
                }
                db.yieldIfContendedSafely();
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


        switch (sUriMatcher.match(uri)) {

            case PROGRAMMES: {
                retCursor = db.query(dbHelper.TABLE_PROGRAMMES, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case PROGRAMMES_ID: {
                retCursor = db.query(dbHelper.TABLE_PROGRAMMES, projection, TVGuideDbHelper.C_PROGRAMME_ID + " = " + id, selectionArgs, null, null, sortOrder);
                break;
            }

           case CHANNELS: {
                retCursor = db.query(dbHelper.TABLE_CHANNELS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case CHANNELS_ID: {
                retCursor = db.query(dbHelper.TABLE_CHANNELS, projection, TVGuideDbHelper.C_CHANNEL_ID + " = " + id, selectionArgs, null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException(("Unknown uri: " + uri));

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
        private static final int DATABASE_VERSION = 5;

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
        public static final String C_CHANNEL_OBJECTID = "objectId";
        public static final String C_CHANNEL_ICONURL = "iconURL";
        public static final String C_CHANNEL_NAME = "name";
        public static final String C_CHANNEL_SOURCE = "source";
        private static final String C_CHANNEL_EXT_ID = "ext_id";


        // Database creation sql statement
        private static final String TABLE_PROGRAMMES_CREATE = "create table "
                + TABLE_PROGRAMMES + "(" + C_PROGRAMME_ID
                + " integer primary key autoincrement, " + C_PROGRAMME_TITLE
                + " text not null collate nocase, "
                + C_PROGRAMME_START + " text, "
                + C_PROGRAMME_STOP + " text, "
                + C_PROGRAMME_DESC + " text collate nocase, "
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
                + " text not null collate nocase, "
                + C_CHANNEL_OBJECTID + " text, "
                + C_CHANNEL_ICONURL + " text, "
                + C_CHANNEL_EXT_ID + " text, "
                + C_CHANNEL_SOURCE + " text "


                +");";

        public TVGuideDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("dbHelper","onCreate");

            db.execSQL(TABLE_PROGRAMMES_CREATE);
            db.execSQL(TABLE_CHANNELS_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("dbHelper","onUpgrade");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAMMES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNELS);
            onCreate(db);
        }
    }



}
