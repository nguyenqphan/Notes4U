package com.nguyenqphan.notes4u;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.channels.DatagramChannel;

import javax.sql.RowSetReader;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;

    private String noteFilter;
    private String oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if(uri == null){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        }else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver()
                    .query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            editor.setText(oldText);
            editor.requestFocus();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(action.equals(Intent.ACTION_EDIT)){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_editor, menu);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }

        return true;
    }

    private void finishEditing(){

        String newText = editor.getText().toString().trim();

        switch (action){
            case Intent.ACTION_INSERT:
                if(newText.length() == 0){
                    setResult(RESULT_CANCELED);
                }else {
                    insertNote(newText);
                }
                break;
            case  Intent.ACTION_EDIT:
                if(newText.length() == 0){
                    deleteNote();
                }else if(oldText.equals(newText)){
                    setResult(RESULT_CANCELED);

                }else {
                    updateNote(newText);
                }
        }
        finish();
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);

        Toast.makeText(this, R.string.note_deleted,
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK); //update something in the list because something is changed

    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        //Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);

        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
