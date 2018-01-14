package bert133.bertscout2018;

import android.provider.BaseColumns;

/**
 * Created by chimera343 on 01/14/2018.
 */

public final class DBContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBContract() {
    }

    public static class TableStandInfo implements BaseColumns {

        public static final String TABLE_NAME_STAND = "stand_scouting";

        public static final String COLNAME_STAND_MATCH = "match_no";
        public static final String COLNAME_STAND_TEAM = "team";
        public static final String COLNAME_STAND_ALLIANCE = "alliance_red_blue";

        public static final String COLNAME_STAND_AUTO_BASELINE = "auto_baseline";
        public static final String COLNAME_STAND_AUTO_SWITCH = "auto_switch";
        public static final String COLNAME_STAND_AUTO_SCALE = "auto_scale";

        public static final String COLNAME_STAND_TELEOP_SWITCH = "tele_switch";
        public static final String COLNAME_STAND_TELEOP_SCALE = "tele_scale";
        public static final String COLNAME_STAND_TELEOP_EXCHANGE = "tele_exchange";
        public static final String COLNAME_STAND_TELEOP_CLIMBED = "tele_climbed";
        public static final String COLNAME_STAND_TELEOP_PARKED = "tele_touchpad";
        public static final String COLNAME_STAND_TELEOP_PENALTIES = "tele_penalties";

        public static final String COLNAME_STAND_RATING = "rating";

        public static final String SQL_QUERY_CREATE_TABLE =

                "CREATE TABLE " + TableStandInfo.TABLE_NAME_STAND + " (" +

                        TableStandInfo._ID + " INTEGER PRIMARY KEY" +

                        ", " + TableStandInfo.COLNAME_STAND_MATCH + " TEXT" +
                        ", " + TableStandInfo.COLNAME_STAND_TEAM + " INTEGER" +
                        ", " + TableStandInfo.COLNAME_STAND_ALLIANCE + " TEXT" +

                        ", " + TableStandInfo.COLNAME_STAND_AUTO_BASELINE + " INTEGER" +
                        ", " + TableStandInfo.COLNAME_STAND_AUTO_SWITCH + " INTEGER" +
                        ", " + TableStandInfo.COLNAME_STAND_AUTO_SCALE + " INTEGER" +

                        ", " + TableStandInfo.COLNAME_STAND_TELEOP_SWITCH + " INTEGER" +
                        ", " + TableStandInfo.COLNAME_STAND_TELEOP_SCALE + " INTEGER" +
                        ", " + TableStandInfo.COLNAME_STAND_TELEOP_EXCHANGE + " INTEGER" +
                        ", " + TableStandInfo.COLNAME_STAND_TELEOP_CLIMBED + " INTEGER" +
                        ", " + TableStandInfo.COLNAME_STAND_TELEOP_PARKED + " INTEGER" +
                        ", " + TableStandInfo.COLNAME_STAND_TELEOP_PENALTIES + " INTEGER" +

                        ", " + TableStandInfo.COLNAME_STAND_RATING + " INTEGER" +

                        ");";

        public static final String SQL_QUERY_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME_STAND + ";";

    }

}
