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

        // Find what kind of uri we passed int
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);

            case PET_ID:
                // Extract out the ID from the URI so we know which row to update
                selection = PetEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                // Update pet at this uri
                return updatePet(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not suoorted for " + uri);
        }

    }

    /**
     * Updates pets as specified in the selection and selection arguments.
     * Return the number of rows successfully updated.
     */
    private int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        // Check that the name is not null
        if (contentValues.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // Check that the gender is 1, 2, or 0
        if (contentValues.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = contentValues.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // Check that the weight is valid
        if (contentValues.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = contentValues.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight <= 0) {
                throw new IllegalArgumentException("Pet requires a valid weight");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }

        // Access database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Update selected pets with the given ContentValues. Return the number of rows affected
        return database.update(PetEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     * Return number of rows deleted.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Find what kind of uri we passed int
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);

            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type (content type) of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        // Find what kind of uri we passed int
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
