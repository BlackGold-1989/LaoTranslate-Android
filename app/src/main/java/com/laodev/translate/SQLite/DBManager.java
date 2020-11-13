package com.laodev.translate.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.laodev.translate.classes.GeneralClasses.ColumnCls;
import com.laodev.translate.utils.Constants;

public class DBManager {

    private Context context;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(ColumnCls col) {
        database.insert(DatabaseHelper.TABLE_NAME, null, getValues(col));
    }

    public Cursor fetch() {
        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_DIC_ID,
                DatabaseHelper.COLUMN_LANG_ID,
                DatabaseHelper.COLUMN_CONTENT,
                DatabaseHelper.COLUMN_LINK,
                DatabaseHelper.COLUMN_REGDATE,
                DatabaseHelper.COLUMN_OTHER
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        return cursor;
    }

    public ColumnCls getColumnWithInputText(String str_input) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COLUMN_CONTENT + "='" + str_input + "'";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();

        ColumnCls column = new ColumnCls();
        if (cursor.moveToFirst()) {
            do {
                column.column_id = cursor.getInt(0);
                column.dic_id = cursor.getInt(1);
                column.lang_id = cursor.getInt(2);
                column.content = cursor.getString(3);
                column.link = cursor.getString(4);
                column.regdate = cursor.getString(5);
                column.other = cursor.getString(6);
            } while (cursor.moveToNext());
        }
        return column;
    }

    public ColumnCls getColumnIndexWithDicAndLangID(int int_dic_id, int int_lang_id) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COLUMN_DIC_ID + "='" + int_dic_id + "' AND " + DatabaseHelper.COLUMN_LANG_ID + "='" + int_lang_id + "'";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();

        ColumnCls column = new ColumnCls();
        if (cursor.moveToFirst()) {
            do {
                column.column_id = cursor.getInt(0);
                column.dic_id = cursor.getInt(1);
                column.lang_id = cursor.getInt(2);
                column.content = cursor.getString(3);
                column.link = cursor.getString(4);
                column.regdate = cursor.getString(5);
                column.other = cursor.getString(6);
            } while (cursor.moveToNext());
        }
        return column;
    }

    public int update(int _id, ColumnCls col) {
        int i = database.update(DatabaseHelper.TABLE_NAME, getValues(col), DatabaseHelper.COLUMN_ID + " = " + _id, null);
        return i;
    }

    public void delete(int _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + "=" + _id, null);
    }

    private ContentValues getValues(ColumnCls col) {
        ContentValues contentValue = new ContentValues();

        contentValue.put(DatabaseHelper.COLUMN_ID, col.column_id);
        contentValue.put(DatabaseHelper.COLUMN_DIC_ID, col.dic_id);
        contentValue.put(DatabaseHelper.COLUMN_LANG_ID, col.lang_id);
        contentValue.put(DatabaseHelper.COLUMN_CONTENT, col.content);
        contentValue.put(DatabaseHelper.COLUMN_LINK, col.link);
        contentValue.put(DatabaseHelper.COLUMN_REGDATE, col.regdate);
        contentValue.put(DatabaseHelper.COLUMN_OTHER, col.other);

        return contentValue;
    }

    public void removeAll()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        database.delete(DatabaseHelper.TABLE_NAME, null, null);
    }

}
