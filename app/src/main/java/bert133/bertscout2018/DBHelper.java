package bert133.bertscout2018;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bert_scout_2018.db";
    public static final int DATABASE_VERSION = 3;

    public static final String SYNC_HEADER_TEAM = "team";
    public static final String SYNC_HEADER_MATCH = "match";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
                " WHERE " + DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER + " = " + teamNumber;

        results = db.rawQuery(query, null);
        results.moveToFirst();

        if (!results.isAfterLast()) {
            rowObject = getTeam_Internal(results);
        }

        results.close();

        return rowObject;
    }

    public JSONArray getTeamInfoList(boolean header) {

        JSONArray teamList = new JSONArray();
        if (header){
            teamList.put(SYNC_HEADER_TEAM);
        }

        JSONObject rowObject = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results;

        String query;
        query = "SELECT * FROM " + DBContract.TableTeamInfo.TABLE_NAME_TEAM +
                " ORDER BY " + DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER;

        results = db.rawQuery(query, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            rowObject = getTeam_Internal(results);
            teamList.put(rowObject);
            results.moveToNext();
        }

        results.close();

        return teamList;
    }

    @Nullable
    private JSONObject getTeam_Internal(Cursor results){
        int totalColumn = results.getColumnCount();
        JSONObject rowObject = new JSONObject();
        for (int i = 0; i < totalColumn; i++) {
            if (results.getColumnName(i) != null) {
                try {
                    switch (results.getColumnName(i)) {
                        case DBContract.TableTeamInfo._ID:
                        case DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER:
                        case DBContract.TableTeamInfo.COLNAME_TEAM_VERSION:
                        case DBContract.TableTeamInfo.COLNAME_TEAM_PICK_NUMBER:
                        case DBContract.TableTeamInfo.COLNAME_TEAM_RATING:
                            rowObject.put(results.getColumnName(i), results.getInt(i));
                            break;
                        case DBContract.TableTeamInfo.COLNAME_TEAM_PICKED:
                            if (results.getInt(i) == 0) {
                                rowObject.put(results.getColumnName(i), false);
                            } else {
                                rowObject.put(results.getColumnName(i), true);
                            }
                            break;
                        case DBContract.TableTeamInfo.COLNAME_TEAM_COMMENT:
                            rowObject.put(results.getColumnName(i), results.getString(i));
                    }
                } catch (JSONException e) {
                    return null;
                }
            }
        }
        return rowObject;
    }

    public boolean updateTeamInfo(JSONObject teamInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {

            contentValues.put(DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER, teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER));
            contentValues.put(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION, teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION) + 1);
            contentValues.put(DBContract.TableTeamInfo.COLNAME_TEAM_RATING, teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_RATING));
            contentValues.put(DBContract.TableTeamInfo.COLNAME_TEAM_PICK_NUMBER, teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_PICK_NUMBER));
            contentValues.put(DBContract.TableTeamInfo.COLNAME_TEAM_PICKED, teamInfo.getBoolean(DBContract.TableTeamInfo.COLNAME_TEAM_PICKED));
            contentValues.put(DBContract.TableTeamInfo.COLNAME_TEAM_COMMENT, teamInfo.getString(DBContract.TableTeamInfo.COLNAME_TEAM_COMMENT));

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
                        " WHERE " + DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER + " = " + teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER);

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
                            DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER + " = ? ",
                            new String[]{
                                    String.valueOf(teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER))}
                    );

                }
            }

        } catch (JSONException e) {
            return false;
        }

        return true;

    }

    public void deleteTeamInfo(int teamNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        query = "DELETE FROM " + DBContract.TableTeamInfo.TABLE_NAME_TEAM +
                " WHERE " + DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER + " = " + teamNumber;
        db.execSQL(query);
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

        if (!results.isAfterLast()) {
            rowObject = getMatch_Internal(results);
        }

        results.close();

        return rowObject;
    }

    public JSONArray getMatchInfoByTeam(int teamNumber) {

        JSONArray matchList = new JSONArray();
        JSONObject rowObject = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results;

        String query;
        query = "SELECT * FROM " + DBContract.TableMatchInfo.TABLE_NAME_MATCH +
                " WHERE " + DBContract.TableMatchInfo.COLNAME_MATCH_TEAM + " = " + teamNumber +
                " ORDER BY " + DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER;

        results = db.rawQuery(query, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            rowObject = getMatch_Internal(results);
            matchList.put(rowObject);
            results.moveToNext();
        }

        results.close();

        return matchList;
    }

    public JSONArray getMatchInfoList(boolean header) {

        JSONArray matchList = new JSONArray();
        if (header){
            matchList.put(SYNC_HEADER_MATCH);
        }

        JSONObject rowObject = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results;

        String query;
        query = "SELECT * FROM " + DBContract.TableMatchInfo.TABLE_NAME_MATCH;

        results = db.rawQuery(query, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            rowObject = getMatch_Internal(results);
            matchList.put(rowObject);
            results.moveToNext();
        }

        results.close();

        return matchList;
    }

    @Nullable
    private JSONObject getMatch_Internal(Cursor results) {
        JSONObject rowObject = new JSONObject();
        int totalColumn = results.getColumnCount();
        for (int i = 0; i < totalColumn; i++) {
            if (results.getColumnName(i) != null) {
                try {
                    switch (results.getColumnName(i)) {
                        case DBContract.TableMatchInfo._ID:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_TEAM:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_VERSION:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_RATING:
                            rowObject.put(results.getColumnName(i), results.getInt(i));
                            break;
                        case DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_PARKED:
                        case DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED:
                            if (results.getInt(i) == 0) {
                                rowObject.put(results.getColumnName(i), false);
                            } else {
                                rowObject.put(results.getColumnName(i), true);
                            }
                            break;
                        case DBContract.TableMatchInfo.COLNAME_MATCH_COMMENT:
                            rowObject.put(results.getColumnName(i), results.getString(i));
                    }
                } catch (JSONException e) {
                    return null;
                }
            }
        }
        return rowObject;
    }

    public boolean updateMatchInfo(JSONObject matchInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {

            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION) + 1);
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_RATING, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_RATING));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE, matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE, matchInfo.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED, matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED, matchInfo.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED));
            contentValues.put(DBContract.TableMatchInfo.COLNAME_MATCH_COMMENT, matchInfo.getString(DBContract.TableMatchInfo.COLNAME_MATCH_COMMENT));

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
}