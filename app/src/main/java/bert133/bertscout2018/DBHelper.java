package bert133.bertscout2018;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bert_scout_2018.db";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.TableVersionInfo.SQL_QUERY_CREATE_TABLE);
        db.execSQL(DBContract.TableTeamInfo.SQL_QUERY_CREATE_TABLE);
        db.execSQL(DBContract.TableMatchInfo.SQL_QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            // just drop and re-create tables
            db.execSQL(DBContract.TableTeamInfo.SQL_QUERY_DELETE_TABLE);
            db.execSQL(DBContract.TableTeamInfo.SQL_QUERY_CREATE_TABLE);
            db.execSQL(DBContract.TableMatchInfo.SQL_QUERY_DELETE_TABLE);
            db.execSQL(DBContract.TableMatchInfo.SQL_QUERY_CREATE_TABLE);
            return;
        }
        return;
    }

    public JSONObject getTeamInfo(int teamNumber) {

        JSONObject rowObject = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results;

        String query;
        query = "SELECT * FROM " + DBContract.TableTeamInfo.TABLE_NAME_TEAM +
                " WHERE " + DBContract.TableTeamInfo.COLNAME_TEAM + " = " + teamNumber;

        results = db.rawQuery(query, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int totalColumn = results.getColumnCount();
            rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (results.getColumnName(i) != null) {
                    try {
                        switch (results.getColumnName(i)) {
                            case DBContract.TableTeamInfo._ID:
                            case DBContract.TableTeamInfo.COLNAME_TEAM:
                            case DBContract.TableTeamInfo.COLNAME_PICK_NUMBER:
                            case DBContract.TableTeamInfo.COLNAME_RATING:
                                rowObject.put(results.getColumnName(i), results.getInt(i));
                                break;
                            case DBContract.TableTeamInfo.COLNAME_PICKED:
                                if (results.getInt(i) == 0) {
                                    rowObject.put(results.getColumnName(i), false);
                                } else {
                                    rowObject.put(results.getColumnName(i), true);
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        return null;
                    }
                }
            }

            results.moveToNext();
        }

        results.close();

        return rowObject;
    }

    public JSONArray getTeamInfoList(boolean headerInfo) {

        JSONArray teamList = new JSONArray();
        if (headerInfo){
            teamList.put("team");
        }
        JSONObject rowObject = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results;

        String query;
        query = "SELECT * FROM " + DBContract.TableTeamInfo.TABLE_NAME_TEAM +
                " ORDER BY " + DBContract.TableTeamInfo.COLNAME_TEAM;

        results = db.rawQuery(query, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int totalColumn = results.getColumnCount();
            rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (results.getColumnName(i) != null) {
                    try {
                        switch (results.getColumnName(i)) {
                            case DBContract.TableTeamInfo._ID:
                            case DBContract.TableTeamInfo.COLNAME_TEAM:
                            case DBContract.TableTeamInfo.COLNAME_PICK_NUMBER:
                            case DBContract.TableTeamInfo.COLNAME_RATING:
                                rowObject.put(results.getColumnName(i), results.getInt(i));
                                break;
                            case DBContract.TableTeamInfo.COLNAME_PICKED:
                                if (results.getInt(i) == 0) {
                                    rowObject.put(results.getColumnName(i), false);
                                } else {
                                    rowObject.put(results.getColumnName(i), true);
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        return null;
                    }
                }
            }
            teamList.put(rowObject);
            results.moveToNext();
        }

        results.close();

        return teamList;
    }

    public boolean updateTeamInfo(JSONObject teamInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {

            contentValues.put(DBContract.TableTeamInfo.COLNAME_TEAM, teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM));
            contentValues.put(DBContract.TableTeamInfo.COLNAME_RATING, teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_RATING));
            contentValues.put(DBContract.TableTeamInfo.COLNAME_PICK_NUMBER, teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_PICK_NUMBER));
            contentValues.put(DBContract.TableTeamInfo.COLNAME_PICKED, teamInfo.getBoolean(DBContract.TableTeamInfo.COLNAME_PICKED));

            if (teamInfo.has(DBContract.TableTeamInfo._ID)) {

                db.update(
                        DBContract.TableTeamInfo.TABLE_NAME_TEAM,
                        contentValues,
                        "_id = ?",
                        new String[]{String.valueOf(teamInfo.getInt(DBContract.TableTeamInfo._ID))}
                );
                return true;

            } else {

                // see if there might already be a record like this
                Cursor results;
                String query = "SELECT * FROM " + DBContract.TableTeamInfo.TABLE_NAME_TEAM +
                        " WHERE " + DBContract.TableTeamInfo.COLNAME_TEAM + " = " + teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM);

                results = db.rawQuery(query, null);

                if (!results.moveToFirst()) {

                    // it is a new record
                    long newID = db.insert(
                            DBContract.TableTeamInfo.TABLE_NAME_TEAM,
                            null,
                            contentValues
                    );
                    if (newID > 0) {
                        teamInfo.put(DBContract.TableTeamInfo._ID, newID);
                        return true;
                    }

                } else {

                    // it does already exist
                    db.update(DBContract.TableTeamInfo.TABLE_NAME_TEAM,
                            contentValues,
                            DBContract.TableTeamInfo.COLNAME_TEAM + " = ? ",
                            new String[]{
                                    String.valueOf(teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM))}
                    );

                }
            }

        } catch (JSONException e) {
            return false;
        }

        return true;

    }

    public JSONObject getMatchInfo(int teamNumber, int matchNumber) {
        JSONObject rowObject = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results;

        String query;
        query = "SELECT * FROM " + DBContract.TableMatchInfo.TABLE_NAME_MATCH +
                " WHERE " + DBContract.TableMatchInfo.COLNAME_MATCH_TEAM + " = " + teamNumber +
                " AND " + DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER + " = " + matchNumber;

        results = db.rawQuery(query, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int totalColumn = results.getColumnCount();
            rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (results.getColumnName(i) != null) {
                    try {
                        switch (results.getColumnName(i)) {
                            case DBContract.TableMatchInfo._ID:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_TEAM:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_RATING:
                                rowObject.put(results.getColumnName(i), results.getInt(i));
                                break;
                            case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_PARKED:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED:
                                if (results.getInt(i) == 0) {
                                    rowObject.put(results.getColumnName(i), false);
                                } else {
                                    rowObject.put(results.getColumnName(i), true);
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        return null;
                    }
                }
            }
            results.moveToNext();
        }

        results.close();

        return rowObject;
    }

    public JSONArray getMatchInfoList(boolean headerInfo){

        JSONArray matchList = new JSONArray();
        if (headerInfo){
            matchList.put("match");
        }

        JSONObject rowObject = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results;

        String query;
        query = "SELECT * FROM " + DBContract.TableMatchInfo.TABLE_NAME_MATCH;

        results = db.rawQuery(query, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int totalColumn = results.getColumnCount();
            rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (results.getColumnName(i) != null) {
                    try {
                        switch (results.getColumnName(i)) {
                            case DBContract.TableMatchInfo._ID:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_TEAM:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_RATING:
                                rowObject.put(results.getColumnName(i), results.getInt(i));
                                break;
                            case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_PARKED:
                            case DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED:
                                if (results.getInt(i) == 0) {
                                    rowObject.put(results.getColumnName(i), false);
                                } else {
                                    rowObject.put(results.getColumnName(i), true);
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        return null;
                    }
                }
            }
            matchList.put(rowObject);
            results.moveToNext();
        }

        results.close();

        return matchList;

    }

    public boolean updateMatchInfo(JSONObject matchInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {

            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_RATING, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_RATING));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE, matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH, matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE, matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED, matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED, matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED));

            if (matchInfo.has(DBContract.TableMatchInfo._ID)) {

                db.update(
                        DBContract.TableMatchInfo.TABLE_NAME_MATCH,
                        contentValues,
                        "_id = ?",
                        new String[]{String.valueOf(matchInfo.getInt(DBContract.TableMatchInfo._ID))}
                );
                return true;

            } else {

                // see if there might already be a record like this
                Cursor results;
                String query =
                        "SELECT * FROM " + DBContract.TableMatchInfo.TABLE_NAME_MATCH +
                                " WHERE " + DBContract.TableMatchInfo.COLNAME_MATCH_TEAM + " = " + matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM) +
                                " AND " + DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER + " = " + matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER);

                results = db.rawQuery(query, null);

                if (!results.moveToFirst()) {

                    // it is a new record
                    long newID = db.insert(
                            DBContract.TableMatchInfo.TABLE_NAME_MATCH,
                            null,
                            contentValues
                    );
                    if (newID > 0) {
                        matchInfo.put(DBContract.TableMatchInfo._ID, newID);
                        return true;
                    }

                } else {

                    // it does already exist
                    db.update(DBContract.TableMatchInfo.TABLE_NAME_MATCH,
                            contentValues,
                            DBContract.TableMatchInfo.COLNAME_MATCH_TEAM + " = ? " +
                                    "AND " + DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER + " = ?",
                            new String[]{
                                    String.valueOf(matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM)),
                                    String.valueOf(matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER))}
                    );
                }
            }

        } catch (JSONException e) {
            return false;
        }

        return true;

    }

//    public JSONArray getDataAllStand(int teamNumber) {
//
//        JSONArray resultSet = new JSONArray();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor results;
//
//        String query;
//
//        if (teamNumber == 0) {
//            query = "SELECT * FROM " + DBContract.TableMatchInfo.TABLE_NAME_MATCH +
//                    " ORDER BY CAST(match_no AS INTEGER)";
//        } else {
//            query = "SELECT * FROM " + DBContract.TableMatchInfo.TABLE_NAME_MATCH +
//                    " WHERE team = " + teamNumber +
//                    " ORDER BY CAST(match_no AS INTEGER)";
//        }
//
//        results = db.rawQuery(query, null);
//        results.moveToFirst();
//
//        while (!results.isAfterLast()) {
//            int totalColumn = results.getColumnCount();
//            JSONObject rowObject = new JSONObject();
//
//            for (int i = 0; i < totalColumn; i++) {
//                if (results.getColumnName(i) != null) {
//                    try {
//                        switch (results.getColumnName(i)) {
//                            case DBContract.TableMatchInfo._ID:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_TEAM:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_RATING:
//                                rowObject.put(results.getColumnName(i), results.getInt(i));
//                                break;
///*
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_ALLIANCE:
//                                rowObject.put(results.getColumnName(i), results.getString(i));
//                                break;
//*/
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_PARKED:
//                            case DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED:
//                                if (results.getInt(i) == 0) {
//                                    rowObject.put(results.getColumnName(i), false);
//                                } else {
//                                    rowObject.put(results.getColumnName(i), true);
//                                }
//                                break;
//                        }
//                    } catch (JSONException e) {
//                        return null;
//                    }
//                }
//            }
//            resultSet.put(rowObject);
//            results.moveToNext();
//        }
//
//        results.close();
//        return resultSet;
//    }
//
//    public boolean updateStandInfo(JSONObject standInfo) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        try {
//
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM, standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM));
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER, standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER));
//
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE, standInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE));
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH, standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH));
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE, standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE));
//
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH, standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH));
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE, standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE));
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE, standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE));
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED, standInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED));
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED, standInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED));
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES, standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES));
//
//            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_RATING, standInfo.getString(DBContract.TableMatchInfo.COLNAME_MATCH_RATING));
//
//            if (standInfo.has(DBContract.TableMatchInfo._ID)) {
//
//                db.update(
//                        DBContract.TableMatchInfo.TABLE_NAME_MATCH,
//                        contentValues,
//                        "_id = ?",
//                        new String[]{String.valueOf(standInfo.getInt(DBContract.TableMatchInfo._ID))}
//                );
//                return true;
//
//            } else {
//
//                // see if there might already be a record like this
//                Cursor results;
//                String query = "SELECT * FROM " + DBContract.TableMatchInfo.TABLE_NAME_MATCH +
//                        " WHERE " + DBContract.TableMatchInfo.COLNAME_MATCH_TEAM + " = " + standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM) +
//                        " AND " + DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER + " = " + standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER);
//
//                results = db.rawQuery(query, null);
//
//                if (!results.moveToFirst()) {
//
//                    // it is a new record
//                    long newID = db.insert(
//                            DBContract.TableMatchInfo.TABLE_NAME_MATCH,
//                            null,
//                            contentValues
//                    );
//                    if (newID > 0) {
//                        standInfo.put(DBContract.TableMatchInfo._ID, newID);
//                        return true;
//                    }
//
//                } else {
//
//                    // it does already exist
//                    db.update(DBContract.TableMatchInfo.TABLE_NAME_MATCH,
//                            contentValues,
//                            DBContract.TableMatchInfo.COLNAME_MATCH_TEAM + " = ? " +
//                                    "AND " + DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER + " = ?",
//                            new String[]{
//                                    String.valueOf(standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM)),
//                                    String.valueOf(standInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER))}
//                    );
//
//                }
//            }
//
//        } catch (JSONException e) {
//            return false;
//        }
//
//        return true;
//
//    }
//
//    public Integer deleteStandScouting() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int result;
//        try {
//            db.execSQL(DBContract.TableMatchInfo.SQL_QUERY_DELETE_TABLE);
//            try {
//                db.execSQL(DBContract.TableMatchInfo.SQL_QUERY_CREATE_TABLE);
//                result = 0;
//            } catch (Exception e) {
//                result = 2; // error during create
//            }
//        } catch (Exception e) {
//            result = 1; // error during delete
//        }
//        return result;
//    }
//
//    public void SetBooleanValue(JSONObject obj, ContentValues contentValues, String fieldName) {
//        try {
//            if (obj.getBoolean(fieldName)) {
//                contentValues.put(fieldName, 1);
//            } else {
//                contentValues.put(fieldName, 0);
//            }
//        } catch (JSONException e) {
//        }
//    }
//
//    public void SetIntegerValue(JSONObject obj, ContentValues contentValues, String fieldName) {
//        try {
//            contentValues.put(fieldName, obj.getInt(fieldName));
//        } catch (JSONException e) {
//        }
//    }
//
//    public void SetStringValue(JSONObject obj, ContentValues contentValues, String fieldName) {
//        try {
//            contentValues.put(fieldName, obj.getString(fieldName));
//        } catch (JSONException e) {
//        }
//    }
}