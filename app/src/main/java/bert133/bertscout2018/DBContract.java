package bert133.bertscout2018;

import android.provider.BaseColumns;

public final class DBContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBContract() {
    }

    public static class TableTeamInfo implements BaseColumns {

        public static final String TABLE_NAME_TEAM = "team_info";

        public static final String COLNAME_TEAM_NUMBER = "team";
        public static final String COLNAME_TEAM_VERSION = "version";
        public static final String COLNAME_TEAM_RATING = "rating";
        public static final String COLNAME_TEAM_PICK_NUMBER = "pick_number";
        public static final String COLNAME_TEAM_PICKED = "picked";
        public static final String COLNAME_TEAM_COMMENT = "comment";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TEAM + " (" +

                        _ID + " INTEGER PRIMARY KEY" +
                        ", " + COLNAME_TEAM_NUMBER + " INTEGER UNIQUE" +
                        ", " + COLNAME_TEAM_VERSION + " INTEGER" +
                        ", " + COLNAME_TEAM_RATING + " INTEGER" +
                        ", " + COLNAME_TEAM_PICK_NUMBER + " INTEGER" +
                        ", " + COLNAME_TEAM_PICKED + " INTEGER" +
                        ", " + COLNAME_TEAM_COMMENT + " TEXT" +
                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_TEAM + ";";

    }

    public static class TableMatchInfo implements BaseColumns {

        public static final String TABLE_NAME_MATCH = "match_info";

        public static final String COLNAME_MATCH_TEAM = "team";
        public static final String COLNAME_MATCH_NUMBER = "match";
        public static final String COLNAME_MATCH_VERSION = "version";
        public static final String COLNAME_MATCH_AUTO_BASELINE = "auto_baseline";
        public static final String COLNAME_MATCH_AUTO_SWITCH = "auto_switch";
        public static final String COLNAME_MATCH_AUTO_SCALE = "auto_scale";
        public static final String COLNAME_MATCH_TELE_SWITCH = "tele_switch";
        public static final String COLNAME_MATCH_TELE_SCALE = "tele_scale";
        public static final String COLNAME_MATCH_TELE_EXCHANGE = "tele_exchange";
        public static final String COLNAME_MATCH_CYCLE_TIME = "cycle_time";
        public static final String COLNAME_MATCH_PARKED = "tele_parked";
        public static final String COLNAME_MATCH_CLIMBED = "tele_climbed";
        public static final String COLNAME_MATCH_PENALTIES = "tele_penalties";
        public static final String COLNAME_MATCH_RATING = "rating";
        public static final String COLNAME_MATCH_COMMENT = "comment";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MATCH + " (" +

                        _ID + " INTEGER PRIMARY KEY" +
                        ", " + COLNAME_MATCH_TEAM + " INTEGER" +
                        ", " + COLNAME_MATCH_NUMBER + " INTEGER" +
                        ", " + COLNAME_MATCH_VERSION + " INTEGER" +
                        ", " + COLNAME_MATCH_AUTO_BASELINE + " INTEGER" +
                        ", " + COLNAME_MATCH_AUTO_SWITCH + " INTEGER" +
                        ", " + COLNAME_MATCH_AUTO_SCALE + " INTEGER" +
                        ", " + COLNAME_MATCH_TELE_SWITCH + " INTEGER" +
                        ", " + COLNAME_MATCH_TELE_SCALE + " INTEGER" +
                        ", " + COLNAME_MATCH_TELE_EXCHANGE + " INTEGER" +
                        ", " + COLNAME_MATCH_CYCLE_TIME + " INTEGER" +
                        ", " + COLNAME_MATCH_PARKED + " INTEGER" +
                        ", " + COLNAME_MATCH_CLIMBED + " INTEGER" +
                        ", " + COLNAME_MATCH_PENALTIES + " INTEGER" +
                        ", " + COLNAME_MATCH_RATING + " INTEGER " +
                        ", " + COLNAME_MATCH_COMMENT + " TEXT " +
                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_MATCH + ";";

    }

}
