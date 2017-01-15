package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.pets.data.PetsContract.PetEntry;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    // Object to access pet database
    private PetDBHelper mDbHelper;

    // Code for pets table
    private static final int PETS = 100;

    // Code for single pet in table
    private static final int PET_ID = 101;

    // Matches content uri to its corresponding code (table or single pet)
    // Constructor is code to return for the root uri
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Static initializer. This is run the first time anything is called from this class.
     */
    static {

        // Content uri patterns the provider should recognize, followed by codes to return when match is found
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS + "/#", PET_ID);
    }

    /**
     * Initialize the provider and the database helper object
     */
    @Override
    public boolean onCreate() {

        // Initialize PetDbHelper object to gain access to the pet database
        mDbHelper = new PetDBHelper(getContext());

        return true;
    }

    /**
     * Perform query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Access database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // What is returned from query
        Cursor cursor;

        // Find out what kind of input uri was passed in
        int match = sUriMatcher.match(uri);

        // Decide which path to go down
        switch (match) {
            case PETS:

                // "content://com.example.android.pets/pets/

                // Perform a query on whole table pets
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );

                break;
            case PET_ID:

                // "content://com.example.android.pets/pets/3

                // Extract out the ID from the uri ( SELECT ... FROM pets WHERE ID = "3" )
                selection = PetEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                // Perform query on pets table where _id equals 3 to return a cursor containing that row
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Find what kind of uri was passed in
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues contentValues) {

        // Check that the name is not null
        String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Check that the gender is 1, 2, or 0
        Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        // Check that the weight is valid
        Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight <= 0) {
            throw new IllegalArgumentException("Pet requires a valid weight");
        }

        // Access database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Insert a new pet into the pets database table with the given ContentValues
        long id = database.insert(PetsContract.PetEntry.TABLE_NAME, null, contentValues);

        // If insertion failed, log an error and return null
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Return the new URI with the ID of the new row inserted at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
