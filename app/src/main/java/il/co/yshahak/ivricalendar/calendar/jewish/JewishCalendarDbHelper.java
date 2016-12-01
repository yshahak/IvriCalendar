package il.co.yshahak.ivricalendar.calendar.jewish;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class JewishCalendarDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "IvriCalendarProvider.db";

    JewishCalendarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String CREATE_DATE_TABLE =
                " CREATE TABLE " + JewishCalendarContract.DateEntry.TABLE_NAME + " (" +
                        JewishCalendarContract.DateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        JewishCalendarContract.DateEntry.COLUMN_DATE_YEAR + " INTEGER NOT NULL, " +
                        JewishCalendarContract.DateEntry.COLUMN_DATE_MONTH + " INTEGER NOT NULL, " +
                        JewishCalendarContract.DateEntry.COLUMN_DATE_DAY_IN_MONTH + " INTEGER NOT NULL, " +
                        JewishCalendarContract.DateEntry.COLUMN_DATE_DAY_IN_WEEK + " INTEGER NOT NULL, " +
                        JewishCalendarContract.DateEntry.COLUMN_DATE_YEAR_LABEL + " TEXT NOT NULL, " +
                        JewishCalendarContract.DateEntry.COLUMN_DATE_MONTH_LABEL + " TEXT NOT NULL, " +
                        JewishCalendarContract.DateEntry.COLUMN_DATE_DAY_LABEL + " TEXT NOT NULL, " +
                        JewishCalendarContract.DateEntry.COLUMN_GOOGLE_EVENTS_IDS + " TEXT, " +
//                        JewishCalendarContract.DateEntry.COLUMN_ALOS + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_SUNRISE + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_SOf_ZMAN_SHMA_MGA + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_SOf_ZMAN_SHMA_GRA + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_SOf_ZMAN_TFILA_MGA + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_SOf_ZMAN_TFILA_GRA + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_MINCHA_GDOLA + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_MINCHA_KTANA + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_SUNSET + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_DUSK + " INTEGER  NOT NULL, " +
//                        JewishCalendarContract.DateEntry.COLUMN_KNISAT_SHABBAT + " INTEGER, " +
//                        JewishCalendarContract.DateEntry.COLUMN_YEZIAT_SHABBAT + " INTEGER, " +
//                        JewishCalendarContract.DateEntry.COLUMN_PARASHA + " TEXT" +
                        ");";

        sqLiteDatabase.execSQL(CREATE_DATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JewishCalendarContract.DateEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
