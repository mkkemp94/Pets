package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract;

/**
 * Created by kempm on 1/15/2017.
 */

public class PetCursorAdapter extends CursorAdapter {

    // Constructor
    public PetCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // Create and return new blank list item
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    // Populate list item view with pet dats
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find views
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView summary = (TextView) view.findViewById(R.id.summary);

        // Get correct column indices
        int nameColumnIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_BREED);

        // Extract pet attributes from cursor at above column indices
        String petName = cursor.getString(nameColumnIndex);
        String petBreed = cursor.getString(breedColumnIndex);

        // If breed field is blank, show unknown
        if (TextUtils.isEmpty(petBreed)) {
            petBreed = context.getString(R.string.unknown_breed);
        }

        // Populate views
        name.setText(petName);
        summary.setText(petBreed);
    }
}
