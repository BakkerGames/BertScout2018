package bert133.bertscout2018;

/**
 * Created by chime on 01/14/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bert_scout.db";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(DBContract.TableStandInfo.SQL_QUERY_CREATE_TABLE);
        db.execSQL(DBContract.TablePitInfo.SQL_QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if (oldVersion < newVersion) {
            db.execSQL(DBContract.TableStandInfo.SQL_QUERY_DELETE_TABLE);
            db.execSQL(DBContract.TableStandInfo.SQL_QUERY_CREATE_TABLE);
            db.execSQL(DBContract.TablePitInfo.SQL_QUERY_DELETE_TABLE);
            db.execSQL(DBContract.TablePitInfo.SQL_QUERY_CREATE_TABLE);
            return;
        }
        return;
    }

    public JSONArray getDataAllStand(int teamNumber) {

        JSONArray resultSet = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results;

        String query;

        if (teamNumber == 0) {
            query = "SELECT * FROM " + DBContract.TableStandInfo.TABLE_NAME_STAND +
                    " ORDER BY CAST(match_no AS INTEGER)";
        } else {
            query = "SELECT * FROM " + DBContract.TableStandInfo.TABLE_NAME_STAND +
                    " WHERE team = " + teamNumber +
                    " ORDER BY CAST(match_no AS INTEGER)";
        }

        results = db.rawQuery(query, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int totalColumn = results.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (results.getColumnName(i) != null) {
                    try {
                        switch (results.getColumnName(i)) {
                            case DBContract.TableStandInfo._ID:
                            case DBContract.TableStandInfo.COLNAME_STAND_MATCH:
                            case DBContract.TableStandInfo.COLNAME_STAND_TEAM:
                            case DBContract.TableStandInfo.COLNAME_STAND_AUTO_SCORE_HIGH:
                            case DBContract.TableStandInfo.COLNAME_STAND_AUTO_SCORE_LOW:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELEOP_SCORE_HIGH:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELEOP_SCORE_LOW:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELEOP_GEARS_PLACED:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELEOP_PENALTIES:
                                rowObject.put(results.getColumnName(i), results.getInt(i));
                                break;
                            case DBContract.TableStandInfo.COLNAME_STAND_ALLIANCE:
                            case DBContract.TableStandInfo.COLNAME_STAND_COMMENT:
                            case DBContract.TableStandInfo.COLNAME_STAND_SCOUT_NAME:
                                rowObject.put(results.getColumnName(i), results.getString(i));
                                break;
                            case DBContract.TableStandInfo.COLNAME_STAND_AUTO_BASE_LINE:
                            case DBContract.TableStandInfo.COLNAME_STAND_AUTO_PLACE_GEAR:
                            case DBContract.TableStandInfo.COLNAME_STAND_AUTO_OPEN_HOPPER:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELEOP_CLIMBED:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELEOP_TOUCHPAD:
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
            resultSet.put(rowObject);
            results.moveToNext();
        }

        results.close();
        return resultSet;
    }

    public boolean updateStandInfo(JSONObject standInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {

            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_MATCH, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_MATCH));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TEAM, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TEAM));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_ALLIANCE, standInfo.getString(DBContract.TableStandInfo.COLNAME_STAND_ALLIANCE));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_SCOUT_NAME, standInfo.getString(DBContract.TableStandInfo.COLNAME_STAND_SCOUT_NAME));

            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_AUTO_SCORE_HIGH, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_AUTO_SCORE_HIGH));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_AUTO_SCORE_LOW, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_AUTO_SCORE_LOW));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_AUTO_BASE_LINE, standInfo.getBoolean(DBContract.TableStandInfo.COLNAME_STAND_AUTO_BASE_LINE));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_AUTO_PLACE_GEAR, standInfo.getBoolean(DBContract.TableStandInfo.COLNAME_STAND_AUTO_PLACE_GEAR));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_AUTO_OPEN_HOPPER, standInfo.getBoolean(DBContract.TableStandInfo.COLNAME_STAND_AUTO_OPEN_HOPPER));

            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_SCORE_HIGH, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_SCORE_HIGH));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_SCORE_LOW, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_SCORE_LOW));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_GEARS_PLACED, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_GEARS_PLACED));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_PENALTIES, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_PENALTIES));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_CLIMBED, standInfo.getBoolean(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_CLIMBED));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_TOUCHPAD, standInfo.getBoolean(DBContract.TableStandInfo.COLNAME_STAND_TELEOP_TOUCHPAD));

            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_COMMENT, standInfo.getString(DBContract.TableStandInfo.COLNAME_STAND_COMMENT));

            if (standInfo.has(DBContract.TableStandInfo._ID)) {

                db.update(
                        DBContract.TableStandInfo.TABLE_NAME_STAND,
                        contentValues,
                        "_id = ?",
                        new String[]{String.valueOf(standInfo.getInt(DBContract.TableStandInfo._ID))}
                );
                return true;

            } else {

                // see if there might already be a record like this
                Cursor results;
                String query = "SELECT * FROM " + DBContract.TableStandInfo.TABLE_NAME_STAND +
                        " WHERE " + DBContract.TableStandInfo.COLNAME_STAND_MATCH + " = " + standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_MATCH) +
                        " AND " + DBContract.TableStandInfo.COLNAME_STAND_TEAM + " = " + standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TEAM);

                results = db.rawQuery(query, null);

                if (!results.moveToFirst()) {

                    // it is a new record
                    long newID = db.insert(
                            DBContract.TableStandInfo.TABLE_NAME_STAND,
                            null,
                            contentValues
                    );
                    if (newID > 0) {
                        standInfo.put(DBContract.TableStandInfo._ID, newID);
                        return true;
                    }

                } else {

                    // it does already exist
                    db.update(DBContract.TableStandInfo.TABLE_NAME_STAND,
                            contentValues,
                            DBContract.TableStandInfo.COLNAME_STAND_MATCH + " = ? " +
                                    "AND " + DBContract.TableStandInfo.COLNAME_STAND_TEAM + " = ?",
                            new String[]{
                                    String.valueOf(standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_MATCH)),
                                    String.valueOf(standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TEAM))}
                    );

                }
            }

        } catch (JSONException e) {
            return false;
        }

        return true;

    }

    public Integer deleteStandScouting() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result;
        try {
            db.execSQL(DBContract.TableStandInfo.SQL_QUERY_DELETE_TABLE);
            try {
                db.execSQL(DBContract.TableStandInfo.SQL_QUERY_CREATE_TABLE);
                result = 0;
            } catch (Exception e) {
                result = 2; // error during create
            }
        } catch (Exception e) {
            result = 1; // error during delete
        }
        return result;
    }

    //
    // Pit Scouting
    //

    public JSONArray getDataAllPit() {

        JSONArray resultSet = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results;

        results = db.rawQuery(
                "SELECT * FROM " + DBContract.TablePitInfo.TABLE_NAME_PIT, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int totalColumn = results.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (results.getColumnName(i) != null) {
                    try {
                        switch (results.getColumnName(i)) {
                            case DBContract.TablePitInfo._ID:
                            case DBContract.TablePitInfo.COLNAME_PIT_TEAM:
                            case DBContract.TablePitInfo.COLNAME_PIT_TEAM_YEARS:
                            case DBContract.TablePitInfo.COLNAME_PIT_TEAM_MEMBERS:
                            case DBContract.TablePitInfo.COLNAME_PIT_HEIGHT:
                            case DBContract.TablePitInfo.COLNAME_PIT_WEIGHT:
                            case DBContract.TablePitInfo.COLNAME_PIT_NUM_CIMS:
                            case DBContract.TablePitInfo.COLNAME_PIT_MAX_SPEED:
                            case DBContract.TablePitInfo.COLNAME_PIT_MAX_FUEL:
                            case DBContract.TablePitInfo.COLNAME_PIT_SHOOT_SPEED:
                            case DBContract.TablePitInfo.COLNAME_PIT_SHOOT_PERCENTAGE:
                            case DBContract.TablePitInfo.COLNAME_PIT_AUTO_NUM_MODES:
                            case DBContract.TablePitInfo.COLNAME_PIT_TEAM_RATING:
                                rowObject.put(results.getColumnName(i), results.getInt(i));
                                break;
                            case DBContract.TablePitInfo.COLNAME_PIT_SCOUT_NAME:
                            case DBContract.TablePitInfo.COLNAME_PIT_TEAM_NAME:
                            case DBContract.TablePitInfo.COLNAME_PIT_WHEEL_TYPE:
                            case DBContract.TablePitInfo.COLNAME_PIT_WHEEL_LAYOUT:
                            case DBContract.TablePitInfo.COLNAME_PIT_SHOOTER_TYPE:
                            case DBContract.TablePitInfo.COLNAME_PIT_SHOOT_LOCATION:
                            case DBContract.TablePitInfo.COLNAME_PIT_STRATEGY:
                            case DBContract.TablePitInfo.COLNAME_PIT_COMMENT:
                                rowObject.put(results.getColumnName(i), results.getString(i));
                                break;
                            case DBContract.TablePitInfo.COLNAME_PIT_CAN_SHOOT_HIGH:
                            case DBContract.TablePitInfo.COLNAME_PIT_CAN_SHOOT_LOW:
                            case DBContract.TablePitInfo.COLNAME_PIT_FLOOR_PICKUP_FUEL:
                            case DBContract.TablePitInfo.COLNAME_PIT_TOP_LOADER:
                            case DBContract.TablePitInfo.COLNAME_PIT_AUTO_AIM:
                            case DBContract.TablePitInfo.COLNAME_PIT_CAN_CARRY_GEAR:
                            case DBContract.TablePitInfo.COLNAME_PIT_FLOOR_PICKUP_GEAR:
                            case DBContract.TablePitInfo.COLNAME_PIT_CAN_CLIMB:
                            case DBContract.TablePitInfo.COLNAME_PIT_OWN_ROPE:
                            case DBContract.TablePitInfo.COLNAME_PIT_START_LEFT:
                            case DBContract.TablePitInfo.COLNAME_PIT_START_CENTER:
                            case DBContract.TablePitInfo.COLNAME_PIT_START_RIGHT:
                            case DBContract.TablePitInfo.COLNAME_PIT_AUTO_BASE_LINE:
                            case DBContract.TablePitInfo.COLNAME_PIT_AUTO_PLACE_GEAR_LEFT:
                            case DBContract.TablePitInfo.COLNAME_PIT_AUTO_PLACE_GEAR_CENTER:
                            case DBContract.TablePitInfo.COLNAME_PIT_AUTO_PLACE_GEAR_RIGHT:
                            case DBContract.TablePitInfo.COLNAME_PIT_AUTO_HIGH_GOAL:
                            case DBContract.TablePitInfo.COLNAME_PIT_AUTO_LOW_GOAL:
                            case DBContract.TablePitInfo.COLNAME_PIT_AUTO_HOPPER:
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
            resultSet.put(rowObject);
            results.moveToNext();
        }

        results.close();
        return resultSet;
    }

    public boolean updatePitInfo(JSONObject pitInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {

            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_TEAM);
            SetStringValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_SCOUT_NAME);

            SetStringValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_TEAM_NAME);
            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_TEAM_YEARS);
            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_TEAM_MEMBERS);

            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_HEIGHT);
            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_WEIGHT);
            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_NUM_CIMS);
            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_MAX_SPEED);
            SetStringValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_WHEEL_TYPE);
            SetStringValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_WHEEL_LAYOUT);
            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_MAX_FUEL);

            SetStringValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_SHOOTER_TYPE);
            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_SHOOT_SPEED);
            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_SHOOT_PERCENTAGE);
            SetStringValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_SHOOT_LOCATION);

            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_CAN_SHOOT_HIGH);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_CAN_SHOOT_LOW);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_FLOOR_PICKUP_FUEL);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_TOP_LOADER);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_AUTO_AIM);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_CAN_CARRY_GEAR);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_FLOOR_PICKUP_GEAR);

            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_CAN_CLIMB);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_OWN_ROPE);

            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_START_LEFT);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_START_CENTER);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_START_RIGHT);

            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_AUTO_NUM_MODES);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_AUTO_BASE_LINE);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_AUTO_PLACE_GEAR_LEFT);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_AUTO_PLACE_GEAR_CENTER);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_AUTO_PLACE_GEAR_RIGHT);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_AUTO_HIGH_GOAL);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_AUTO_LOW_GOAL);
            SetBooleanValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_AUTO_HOPPER);

            SetStringValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_STRATEGY);

            SetIntegerValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_TEAM_RATING);

            SetStringValue(pitInfo, contentValues, DBContract.TablePitInfo.COLNAME_PIT_COMMENT);

//            if (pitInfo.has(DBContract.TablePitInfo._ID)) {
//
//                db.update(
//                        DBContract.TablePitInfo.TABLE_NAME_PIT,
//                        contentValues,
//                        "_id = ?",
//                        new String[]{String.valueOf(pitInfo.getInt(DBContract.TablePitInfo._ID))}
//                );
//                return true;
//
//            } else {

            // see if there might already be a record like this
            Cursor results;
            String query = "SELECT * FROM " + DBContract.TablePitInfo.TABLE_NAME_PIT +
                    " WHERE " + DBContract.TablePitInfo.COLNAME_PIT_TEAM + " = " + pitInfo.getInt(DBContract.TablePitInfo.COLNAME_PIT_TEAM);

            results = db.rawQuery(query, null);

            if (!results.moveToFirst()) {

                // it is a new record
                long newID = db.insert(
                        DBContract.TablePitInfo.TABLE_NAME_PIT,
                        null,
                        contentValues
                );
                if (newID > 0) {
                    pitInfo.put(DBContract.TablePitInfo._ID, newID);
                    return true;
                }

            } else {

                // it does already exist
                db.update(DBContract.TablePitInfo.TABLE_NAME_PIT,
                        contentValues,
                        DBContract.TablePitInfo.COLNAME_PIT_TEAM + " = ?",
                        new String[]{
                                String.valueOf(pitInfo.getInt(DBContract.TablePitInfo.COLNAME_PIT_TEAM))}
                );

            }
//            }

        } catch (JSONException e) {
            return false;
        }

        return true;

    }

    public Integer deletePitInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result;
        try {
            db.execSQL(DBContract.TablePitInfo.SQL_QUERY_DELETE_TABLE);
            try {
                db.execSQL(DBContract.TablePitInfo.SQL_QUERY_CREATE_TABLE);
                result = 0;
            } catch (Exception e) {
                result = 2; // error during create
            }
        } catch (Exception e) {
            result = 1; // error during delete
        }
        return result;
    }

    public void SetBooleanValue(JSONObject obj, ContentValues contentValues, String fieldName) {
        try {
            if (obj.getBoolean(fieldName)) {
                contentValues.put(fieldName, 1);
            } else {
                contentValues.put(fieldName, 0);
            }
        } catch (JSONException e) {
        }
    }

    public void SetIntegerValue(JSONObject obj, ContentValues contentValues, String fieldName) {
        try {
            contentValues.put(fieldName, obj.getInt(fieldName));
        } catch (JSONException e) {
        }
    }

    public void SetStringValue(JSONObject obj, ContentValues contentValues, String fieldName) {
        try {
            contentValues.put(fieldName, obj.getString(fieldName));
        } catch (JSONException e) {
        }
    }
}