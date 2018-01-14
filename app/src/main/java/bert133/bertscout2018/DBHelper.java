package bert133.bertscout2018;

/**
 * Created by chimera343 on 01/14/2018.
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

    public static final String DATABASE_NAME = "bert_scout_2018.db";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(DBContract.TableStandInfo.SQL_QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if (oldVersion < newVersion) {
            db.execSQL(DBContract.TableStandInfo.SQL_QUERY_DELETE_TABLE);
            db.execSQL(DBContract.TableStandInfo.SQL_QUERY_CREATE_TABLE);
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
                            case DBContract.TableStandInfo.COLNAME_STAND_TEAM:
                            case DBContract.TableStandInfo.COLNAME_STAND_MATCH:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELE_SWITCH:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELE_SCALE:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELE_EXCHANGE:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELE_PENALTIES:
                            case DBContract.TableStandInfo.COLNAME_STAND_RATING:
                                rowObject.put(results.getColumnName(i), results.getInt(i));
                                break;
/*
                            case DBContract.TableStandInfo.COLNAME_STAND_ALLIANCE:
                                rowObject.put(results.getColumnName(i), results.getString(i));
                                break;
*/
                            case DBContract.TableStandInfo.COLNAME_STAND_AUTO_BASELINE:
                            case DBContract.TableStandInfo.COLNAME_STAND_AUTO_SWITCH:
                            case DBContract.TableStandInfo.COLNAME_STAND_AUTO_SCALE:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELE_PARKED:
                            case DBContract.TableStandInfo.COLNAME_STAND_TELE_CLIMBED:
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

            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TEAM, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TEAM));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_MATCH, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_MATCH));

            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_AUTO_BASELINE, standInfo.getBoolean(DBContract.TableStandInfo.COLNAME_STAND_AUTO_BASELINE));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_AUTO_SWITCH, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_AUTO_SWITCH));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_AUTO_SCALE, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_AUTO_SCALE));

            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELE_SWITCH, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TELE_SWITCH));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELE_SCALE, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TELE_SCALE));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELE_EXCHANGE, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TELE_EXCHANGE));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELE_PARKED, standInfo.getBoolean(DBContract.TableStandInfo.COLNAME_STAND_TELE_PARKED));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELE_CLIMBED, standInfo.getBoolean(DBContract.TableStandInfo.COLNAME_STAND_TELE_CLIMBED));
            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_TELE_PENALTIES, standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TELE_PENALTIES));

            contentValues.put(DBContract.TableStandInfo.COLNAME_STAND_RATING, standInfo.getString(DBContract.TableStandInfo.COLNAME_STAND_RATING));

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
                        " WHERE " + DBContract.TableStandInfo.COLNAME_STAND_TEAM + " = " + standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TEAM) +
                        " AND " + DBContract.TableStandInfo.COLNAME_STAND_MATCH + " = " + standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_MATCH);

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
                            DBContract.TableStandInfo.COLNAME_STAND_TEAM + " = ? " +
                                    "AND " + DBContract.TableStandInfo.COLNAME_STAND_MATCH + " = ?",
                            new String[]{
                                    String.valueOf(standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_TEAM)),
                                    String.valueOf(standInfo.getInt(DBContract.TableStandInfo.COLNAME_STAND_MATCH))}
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