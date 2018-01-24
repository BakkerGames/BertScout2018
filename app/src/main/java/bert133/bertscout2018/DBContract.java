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
        public static final String COLNAME_DEVICENAME = "version";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE IF NOT EXISTS " + TableVersionInfo.TABLE_NAME_VERSION + " (" +

                        TableVersionInfo._ID + " INTEGER PRIMARY KEY" +

                        ", " + TableVersionInfo.COLNAME_VERSION + " INTEGER UNIQUE" +
                        ", " + TableVersionInfo.COLNAME_DEVICENAME + " STRING" +

                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_VERSION + ";";

    }

    public static class TableTeamInfo implements BaseColumns {

        public static final String TABLE_NAME_TEAM = "team_info";

        public static final String COLNAME_TEAM = "team";
        public static final String COLNAME_RATING = "rating";
        public static final String COLNAME_PICKED = "picked";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE IF NOT EXISTS " + TableTeamInfo.TABLE_NAME_TEAM + " (" +

                        TableTeamInfo._ID + " INTEGER PRIMARY KEY" +

                        ", " + TableTeamInfo.COLNAME_TEAM + " INTEGER UNIQUE" +
                        ", " + TableTeamInfo.COLNAME_RATING + " INTEGER" +
                        ", " + TableTeamInfo.COLNAME_PICKED + " INTEGER" +

                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_TEAM + ";";

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

                "CREATE TABLE IF NOT EXISTS " + TableMatchInfo.TABLE_NAME_MATCH + " (" +

                        TableMatchInfo._ID + " INTEGER PRIMARY KEY" +

                        ", " + TableMatchInfo.COLNAME_MATCH_TEAM + " INTEGER" +
                        ", " + TableMatchInfo.COLNAME_MATCH_NUMBER + " INTEGER" +

                        ", " + TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE + " INTEGER" +
                        ", " + TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH + " INTEGER" +
                        ", " + TableMatchInfo.COLNAME_MATCH_AUTO_SCALE + " INTEGER" +

                        ", " + TableMatchInfo.COLNAME_MATCH_TELE_SWITCH + " INTEGER" +
                        ", " + TableMatchInfo.COLNAME_MATCH_TELE_SCALE + " INTEGER" +
                        ", " + TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE + " INTEGER" +
                        ", " + TableMatchInfo.COLNAME_MATCH_TELE_PARKED + " INTEGER" +
                        ", " + TableMatchInfo.COLNAME_MATCH_TELE_CLIMBED + " INTEGER" +
                        ", " + TableMatchInfo.COLNAME_MATCH_TELE_PENALTIES + " INTEGER" +

                        ", " + TableMatchInfo.COLNAME_MATCH_RATING + " INTEGER" +

                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_MATCH + ";";

    }

}
