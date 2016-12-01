package il.co.yshahak.ivricalendar.calendar.jewish;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by B.E.L on 15/05/2016.
 */
@SuppressWarnings("ConstantConditions")
public class JewishCalendarContentProvider extends ContentProvider {

    static final int DATES = 100; //whole dates records
    static final int DATES_WITH_ID = 101; //single date record
    static final int EVENTS = 200;

    static final UriMatcher uriMatcher;
    final static String authority = JewishCalendarContract.CONTENT_AUTHORITY;

    static {
        uriMatcher = buildUriMatcher();
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(authority, JewishCalendarContract.PATH_DATES, DATES);
        uriMatcher.addURI(authority, JewishCalendarContract.PATH_DATES + "/#", DATES_WITH_ID);
        uriMatcher.addURI(authority, JewishCalendarContract.PATH_EVENTS, EVENTS);
        return uriMatcher;
    }


    private JewishCalendarDbHelper dbHelper;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new JewishCalendarDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        long rowID;
        switch (match) {
            case DATES:
                rowID = db.insert(JewishCalendarContract.DateEntry.TABLE_NAME, null, values);
                if (rowID > 0) {
                    returnUri = JewishCalendarContract.DateEntry.buildDateUri(rowID);
                } else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            case EVENTS:
                rowID = db.insert(JewishCalendarContract.EventsEntry.TABLE_NAME, null, values);
                if (rowID > 0) {
                    returnUri = JewishCalendarContract.EventsEntry.buildEventsUri(rowID);
                } else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case DATES:
                retCursor = dbHelper.getReadableDatabase().query(
                        JewishCalendarContract.DateEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            break;
            case EVENTS:
                retCursor = dbHelper.getReadableDatabase().query(
                        JewishCalendarContract.EventsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case DATES:
                count = db.delete(JewishCalendarContract.DateEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case DATES:
                count = db.update(JewishCalendarContract.DateEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case DATES:
                return JewishCalendarContract.DateEntry.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}