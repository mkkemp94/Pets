package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kempm on 1/12/2017.
 */

public final class PetsContract {

    // Constructor
    private PetsContract() { }

    // Content Authority is used to help identify the Content Provider
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    // Base Content URI with scheme
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path for table to get from content URI
    public static final String PATH_PETS = "pets";

    // Pet entry table
    public static final class PetEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "pets";

        // Full URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        // Columns
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        // Possible genders
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;
    }
}
