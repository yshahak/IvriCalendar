package il.co.yshahak.ivricalendar.calendar.jewish;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class JewishCalendarContract {

    public static final String CONTENT_AUTHORITY = "IvriCalendarProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DATES = "dates";

    public static final String PATH_EVENTS = "events";

    /* Inner class that defines the table contents of the location table */
    public static final class DateEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DATES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATES;

        // Table name
        public static final String TABLE_NAME = "dates";
        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";

        static final String _ID = "_id";
        public static final String COLUMN_DATE_YEAR = "dateYear";
        public static final String COLUMN_DATE_MONTH = "dateMonth";
        static final String COLUMN_DATE_DAY_IN_MONTH = "dayInMonth";
        static final String COLUMN_DATE_DAY_IN_WEEK = "dayInWeek";
        static final String COLUMN_DATE_YEAR_LABEL = "yearLable";
        static final String COLUMN_DATE_MONTH_LABEL = "monthLabel";
        static final String COLUMN_DATE_DAY_LABEL = "dayLabel";
        static final String COLUMN_ALOS = "alos";
        static final String COLUMN_SUNRISE = "sunRise";
        static final String COLUMN_SOf_ZMAN_SHMA_MGA = "sofZmanSHmaMGA";
        static final String COLUMN_SOf_ZMAN_TFILA_MGA = "sofZmanMGA";
        static final String COLUMN_SOf_ZMAN_SHMA_GRA = "sofZmanSHmaGRA";
        static final String COLUMN_SOf_ZMAN_TFILA_GRA = "sofZmanGRA";

        static final String COLUMN_MINCHA_GDOLA = "minchaGdola";
        static final String COLUMN_MINCHA_KTANA = "mincha";
        static final String COLUMN_SUNSET = "sunset";
        static final String COLUMN_DUSK = "dusk";
        static final String COLUMN_KNISAT_SHABBAT = "knisatShabbat";
        static final String COLUMN_YEZIAT_SHABBAT = "yeziatShabbat";
        static final String COLUMN_PARASHA = "parasha";


        static final String COLUMN_GOOGLE_EVENTS_IDS = "eventIds";

        public static final int COLUMN_INDEX_ID = 0;
        public static final int COLUMN_INDEX_DATE_YEAR = 1;
        public static final int COLUMN_INDEX_DATE_MONTH = 2;
        public static final int COLUMN_INDEX_DATE_DAY_IN_MONTH = 3;
        public static final int COLUMN_INDEX_DATE_DAY_IN_WEEK = 4;
        public static final int COLUMN_INDEX_YEAR_LABEL = 5;
        public static final int COLUMN_INDEX_MONTH_LABEL = 6;
        public static final int COLUMN_INDEX_DAY_LABEL = 7;
        public static final int COLUMN_INDEX_GOOGLE_EVENTS_IDS = 8;

//        public static final int COLUMN_INDEX_ALOS = 8;
//        public static final int COLUMN_INDEX_SUNRISE = 9;
//        public static final int COLUMN_INDEX_SOF_SHMA_MGA = 10;
//        public static final int COLUMN_INDEX_SOF_SHMA_GRA = 11;
//        public static final int COLUMN_INDEX_SOF_TFILA_MGA = 12;
//        public static final int COLUMN_INDEX_SOF_TFILA_GRA = 13;
//        public static final int COLUMN_INDEX_MINCHA_GDOLA = 14;
//        public static final int COLUMN_INDEX_MINCHA_KTANA = 15;
//        public static final int COLUMN_INDEX_SUNSET = 16;
//        public static final int COLUMN_INDEX_DUSK = 17;
//        public static final int COLUMN_INDEX_SHABBAT_KNISAT = 18;
//        public static final int COLUMN_INDEX_SHABBAT_END = 19;
//        public static final int COLUMN_INDEX_PARASHA = 20;

        public static Uri buildDateUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /* Inner class that defines the table contents of the location table */
    public static final class EventsEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;
        // Table name
        public static final String TABLE_NAME = "events";

        public static Uri buildEventsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
