package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.pets.data.PetsContract.SQL_DELETE_ENTRIES;

/**
 * Created by kempm on 1/12/2017.
 */

public class PetDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Pets.db";

    // Constructor
    public PetDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // On create, make a table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =
                "CREATE TABLE " + PetsContract.PetEntry.TABLE_NAME + " (" +
                        PetsContract.PetEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                        PetsContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL," +
                        PetsContract.PetEntry.COLUMN_PET_BREED + " TEXT," +
                        PetsContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL," +
                        PetsContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    // On upgrade, delete and remake table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
