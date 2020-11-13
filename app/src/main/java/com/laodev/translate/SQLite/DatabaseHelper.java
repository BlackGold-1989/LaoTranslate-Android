package com.laodev.translate.SQLite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "tb_translate_dic";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DIC_ID = "dic_id";
    public static final String COLUMN_LANG_ID = "lang_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_REGDATE = "regdate";
    public static final String COLUMN_OTHER = "other";

    static final String DB_NAME = "daxiaoit_translate.db";
    static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DIC_ID + " TEXT NOT NULL, " +
            COLUMN_LANG_ID + " TEXT NOT NULL, " +
            COLUMN_CONTENT + " TEXT NOT NULL, " +
            COLUMN_LINK + " TEXT NOT NULL, " +
            COLUMN_REGDATE + " TEXT NOT NULL, " +
            COLUMN_OTHER + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}