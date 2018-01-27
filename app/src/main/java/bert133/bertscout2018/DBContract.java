package bert133.bertscout2018;

import android.provider.BaseColumns;

public final class DBContract {

    public static final String DATABASE_NAME = "bert_scout_2018.db";
    public static final int DATABASE_VERSION = 1;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBContract() {
    }

    public static class TableVersionInfo implements BaseColumns {

        public static final String TABLE_NAME_VERSION = "version_info";

        public static final String COLNAME_VERSION = "version";
        public static final String COLNAME_DEVICENAME = "device_name";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_VERSION + " (" +

                        _ID + " INTEGER PRIMARY KEY" +

                        ", " + COLNAME_VERSION + " INTEGER" +
                        ", " + COLNAME_DEVICENAME + " TEXT" +

                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_VERSION + ";";

    }

    public static class TableTeamInfo implements BaseColumns {

        public static final String TABLE_NAME_TEAM = "team_info";

        public static final String COLNAME_TEAM = "team";
        public static final String COLNAME_RATING = "rating";
        public static final String COLNAME_PICK_NUMBER = "pick_number";
        public static final String COLNAME_PICKED = "picked";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TEAM + " (" +

                        _ID + " INTEGER PRIMARY KEY" +

                        ", " + COLNAME_TEAM + " INTEGER UNIQUE" +
                        ", " + COLNAME_RATING + " INTEGER" +

                        ", " + COLNAME_PICK_NUMBER + " INTEGER" +
                        ", " + COLNAME_PICKED + " INTEGER" +

                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_TEAM + ";";

    }

    public static class TableTeamComments implements BaseColumns {

        public static final String TABLE_NAME_TEAMCOMMENTS = "team_comments";

        public static final String COLNAME_TEAMCOMMENTS_TEAM = "team";
        public static final String COLNAME_MATCHCOMMENTS_UUID = "uuid";
        public static final String COLNAME_TEAMCOMMENTS_COMMENT = "comment";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TEAMCOMMENTS + " (" +

                        _ID + " INTEGER PRIMARY KEY" +

                        ", " + COLNAME_TEAMCOMMENTS_TEAM + " INTEGER UNIQUE" +
                        ", " + COLNAME_MATCHCOMMENTS_UUID + " TEXT" +
                        ", " + COLNAME_TEAMCOMMENTS_COMMENT + " TEXT" +

                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_TEAMCOMMENTS + ";";

    }

    public static class TableMatchInfo implements BaseColumns {

        public static final String TABLE_NAME_MATCH = "match_info";

        public static final String COLNAME_MATCH_TEAM = "team";
        public static final String COLNAME_MATCH_NUMBER = "match";

        public static final String COLNAME_MATCH_AUTO_BASELINE = "auto_baseline";
        public static final String COLNAME_MATCH_AUTO_SWITCH = "auto_switch";
        public static final String COLNAME_MATCH_AUTO_SCALE = "auto_scale";

        public static final String COLNAME_MATCH_TELE_SWITCH = "tele_switch";
        public static final String COLNAME_MATCH_TELE_SCALE = "tele_scale";
        public static final String COLNAME_MATCH_TELE_EXCHANGE = "tele_exchange";
        public static final String COLNAME_MATCH_TELE_PARKED = "tele_parked";
        public static final String COLNAME_MATCH_TELE_CLIMBED = "tele_climbed";
        public static final String COLNAME_MATCH_TELE_PENALTIES = "tele_penalties";

        public static final String COLNAME_MATCH_RATING = "rating";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MATCH + " (" +

                        _ID + " INTEGER PRIMARY KEY" +

                        ", " + COLNAME_MATCH_TEAM + " INTEGER" +
                        ", " + COLNAME_MATCH_NUMBER + " INTEGER" +

                        ", " + COLNAME_MATCH_AUTO_BASELINE + " INTEGER" +
                        ", " + COLNAME_MATCH_AUTO_SWITCH + " INTEGER" +
                        ", " + COLNAME_MATCH_AUTO_SCALE + " INTEGER" +

                        ", " + COLNAME_MATCH_TELE_SWITCH + " INTEGER" +
                        ", " + COLNAME_MATCH_TELE_SCALE + " INTEGER" +
                        ", " + COLNAME_MATCH_TELE_EXCHANGE + " INTEGER" +
                        ", " + COLNAME_MATCH_TELE_PARKED + " INTEGER" +
                        ", " + COLNAME_MATCH_TELE_CLIMBED + " INTEGER" +
                        ", " + COLNAME_MATCH_TELE_PENALTIES + " INTEGER" +

                        ", " + COLNAME_MATCH_RATING + " INTEGER " +

                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_MATCH + ";";

    }

    public static class TableMatchComments implements BaseColumns {

        public static final String TABLE_NAME_MATCHCOMMENTS = "match_comments";

        public static final String COLNAME_MATCHCOMMENTS_TEAM = "team";
        public static final String COLNAME_MATCHCOMMENTS_MATCH = "match";
        public static final String COLNAME_MATCHCOMMENTS_UUID = "uuid";
        public static final String COLNAME_MATCHCOMMENTS_COMMENT = "comment";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MATCHCOMMENTS + " (" +

                        _ID + " INTEGER PRIMARY KEY" +

                        ", " + COLNAME_MATCHCOMMENTS_TEAM + " INTEGER" +
                        ", " + COLNAME_MATCHCOMMENTS_MATCH + " INTEGER" +
                        ", " + COLNAME_MATCHCOMMENTS_UUID + " TEXT" +
                        ", " + COLNAME_MATCHCOMMENTS_COMMENT + " TEXT" +

                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_MATCHCOMMENTS + ";";

    }
}
