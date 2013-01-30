package kn.app.goodsentence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GoodSentenceDatabase {
    public static final int TOTAL_LINE_TO_READ = 20;
    public static final int HALF_TOTAL_LINE_TO_READ = (TOTAL_LINE_TO_READ/2);
    public static final String KEY_ID = "_id";
    public static final String QUOTE = "my_quote";
    public static final String CURRENT_POSITION = "current_pos";
    public static final String TOTAL_LINE_READ = "line_read";
    public static final String TAG="GdSentenceDatabase";
    public int db_total_line_read = 0;
    public boolean is_file_read_finish = false;
    public Context myContext;
    public int my_current_pos = 0;

    private SQLiteDatabase db;
    private GoodSentenceSQLiteHelper dbHelper;

    public GoodSentenceDatabase(Context context) {
        myContext = context;
        dbHelper = new GoodSentenceSQLiteHelper(myContext);
        open();
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        //int current_pos = read_current_pos(CURRENT_POSITION);
        if (0 == my_current_pos)
        {
            my_current_pos = read_current_pos(CURRENT_POSITION);
        }
        if (!is_file_read_finish){
            Log.i(TAG, "log from file="+my_current_pos);
            int total_line_read = getFileContent(my_current_pos-1);
            update_total_read(total_line_read);
            db_total_line_read = total_line_read;
            if (total_line_read == 0) {
                is_file_read_finish = true;
            }
        }
    }

    public void close() {
        dbHelper.close();
    }

    public void insert_quote(String quote){
        ContentValues value = new ContentValues();
        value.put(QUOTE, quote);
        long insertId = db.insert(
                GoodSentenceSQLiteHelper.DATABASE_TABLE_QUOTE,null,value);
    }

    public String read_next_quote(){
        //int current_pos = read_current_pos(CURRENT_POSITION);
        Log.i(TAG, "b4 current_pos="+my_current_pos);
        String result = read_quote(KEY_ID+"="+my_current_pos);
        //update_current_pos(current_pos);

        if (my_current_pos >= (db_total_line_read-HALF_TOTAL_LINE_TO_READ)) {
            /// read more from the file if user is getting near to end
            Log.i(TAG, "read_next_quote, current_pos="+my_current_pos);
        	int skip = my_current_pos+HALF_TOTAL_LINE_TO_READ;
            int total_line_read = getFileContent(skip);
            db_total_line_read = db_total_line_read+total_line_read;
            if (!is_file_read_finish){
                update_total_read(db_total_line_read);
                if (total_line_read==0){
                    is_file_read_finish = true;
                }
            }
        }

        my_current_pos++;
        Log.i(TAG, "current_pos="+my_current_pos+"; sentence="+result);

        return result;
    }

    public String read_quote(String where){
        String quote = null;
        String[] result_columns = new String[] { KEY_ID, QUOTE};
        Cursor cursor = db.query(GoodSentenceSQLiteHelper.DATABASE_TABLE_QUOTE,
        		result_columns, where, null, null, null, null);
        /// The Cursor is inititalized at before first, so we can check only
        /// if there is a "next" row available.
        if (cursor.moveToNext()) {
        	int index = cursor.getColumnIndexOrThrow(QUOTE);
            quote = cursor.getString(index);
        }

        cursor.close();
        return quote;
    }

    public int read_current_pos(String column){
        int info = 0;
        String[] result_columns = new String[] { KEY_ID, CURRENT_POSITION };
        Cursor cursor = db.query(GoodSentenceSQLiteHelper.DATABASE_TABLE_INFO,
        		result_columns, null, null, null, null, "1");

        /// The Cursor is inititalized at before first, so we can check only
        /// if there is a "next" row available.
        if (cursor.moveToNext()) {
        	int index = cursor.getColumnIndexOrThrow(column);
            info = cursor.getInt(index);
            Log.i(TAG, "read_current_pos, inside pos="+info);
        } else {
            /// create the first entry
            ContentValues value = new ContentValues();
            value.put(CURRENT_POSITION, 1);
            info = 1;
            long insertId = db.insert(
                    GoodSentenceSQLiteHelper.DATABASE_TABLE_INFO,null,value);
        }

        Log.i(TAG, "read_current_pos, pos="+info);
        cursor.close();
        return info;
    }

    public void update_current_pos(int new_pos) {
        ContentValues value = new ContentValues();
        value.put(CURRENT_POSITION, new_pos);
        db.update(GoodSentenceSQLiteHelper.DATABASE_TABLE_INFO,
                value, KEY_ID+"="+1, null);
    }

    public void update_total_read(int new_total) {
        ContentValues value = new ContentValues();
        value.put(TOTAL_LINE_READ, new_total);
        db.update(GoodSentenceSQLiteHelper.DATABASE_TABLE_INFO,
                value, KEY_ID+"="+1, null);
    }

    public void delete_table(){
        /// future improvement, to allow user to save new file
    }

    public int getFileContent(int skip){
        int count = 0;
        int total_line_read = 0;
    	String line = null;
    	try {
            //InputStream instream = openFileInput("myfilename.txt");
            InputStream instream = myContext.getResources()
                .openRawResource(R.raw.goodsentence);
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader buffreader = new BufferedReader(inputreader);

            while (count < skip ) {
                buffreader.readLine();
                count++;
            }

            int read = count+TOTAL_LINE_TO_READ;
            db.beginTransaction();
            while (count < read ){
                line = buffreader.readLine();
                Log.i(TAG, "file: line="+line);
                insert_quote(line);
                count++;
                total_line_read++;
            }
            db.setTransactionSuccessful();
            db.endTransaction();

            instream.close();
            inputreader.close();
            buffreader.close();

        } catch (java.io.FileNotFoundException e) {	
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return total_line_read;
    }

}
