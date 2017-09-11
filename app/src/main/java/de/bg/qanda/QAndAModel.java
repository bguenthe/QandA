package de.bg.qanda;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class QAndAModel extends SQLiteOpenHelper {

	private SQLiteDatabase myDB;
	final static String MY_DB_NAME = "qanda";
	final static String MY_DB_TABLE = "qanda";
	public static final String KEY_ROWID = "id";
	public static final String KEY_ORDER_NO = "order_no";
	public static final String KEY_QUESTION = "question";
	public static final String KEY_ANSWER = "answer";
	public static final String KEY_CATEGORY = "category"; // zu welchem block gehört die frage/antwort? (1 bis 5)
	private static final int DATABASE_VERSION = 4;
	Cursor c;
	LinkedHashMap<String, QAndAAide> qAndAs;
	int currentOrderNo = 0;
	Iterator<String> it;
	public String TAG = "QAAModel";

	public QAndAModel(Context ctx) {
		super(ctx, MY_DB_NAME, null, DATABASE_VERSION);
		qAndAs = new LinkedHashMap<String, QAndAAide>();
		// alle Datensätze in den Hauptspeicher lesen
		QAndAAide da = null;
		myDB = this.getWritableDatabase();
		Cursor c = myDB.query(MY_DB_TABLE, new String[] { KEY_ROWID, KEY_ORDER_NO,
				KEY_QUESTION, KEY_ANSWER, KEY_CATEGORY }, null, null, null, null, null);
		while (c.moveToNext() == true) {
			da = new QAndAAide(c);
			qAndAs.put(da.getId(), da);
			currentOrderNo = da.getOrder(); 
		}
		c.close();
	}

	// public QAndAAide getQAndQById(String id) {
	// QAndAAide da = null;
	// myDB = this.getWritableDatabase();
	// Cursor c = myDB.query(MY_DB_TABLE, new String[] { KEY_ROWID,
	// KEY_QUESTION, KEY_ANSWER }, "ID = " + id, null, null, null,
	// null);
	// if (c.moveToFirst() == true) {
	// da = new QAndAAide(c);
	// }
	// c.close();
	// return da;
	// }

	public QAndAAide getFirstQAndA() {
		String key = null;
		it = qAndAs.keySet().iterator();
		if (it.hasNext()) {
			key = it.next();
		}
		return qAndAs.get(key);
	}

	public QAndAAide getNextQAndA() {
		String key = null;
		if (it.hasNext()) {
			key = it.next();
		} else {
			it = qAndAs.keySet().iterator();
			key = it.next();
		}
		return qAndAs.get(key);
	}

	private ContentValues createContentValues(int order, String question, String answer, int category) {
		ContentValues values = new ContentValues();
		values.put(KEY_QUESTION, order);
		values.put(KEY_QUESTION, question);
		values.put(KEY_ANSWER, answer);
		values.put(KEY_CATEGORY, category);
		return values;
	}

	/* wird aufgerufen, wenn die Datenbank neu erzeugt werden muss (also, wenn sie nicht auf platte existiert) */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + MY_DB_TABLE
					+ " (id integer primary key autoincrement,"
					+ "order_no integer,"
					+ "question varchar(200)," 
					+ "answer varchar(200));");

			ContentValues initialValues = createContentValues(1, "awesome", "geil", 1);
			db.insert(MY_DB_TABLE, null, initialValues);
			initialValues = createContentValues(2, "stupid", "dumm", 1);
			db.insert(MY_DB_TABLE, null, initialValues);
			initialValues = createContentValues(3, "antics", "Possen", 1);
			db.insert(MY_DB_TABLE, null, initialValues);
		} catch (SQLiteException se) {
			Log.e(TAG, se.toString());
		}
	}

	/* wird aufgerufen, wenn sich die Versionsnummer der db ändert */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP Table "+ MY_DB_TABLE);
		onCreate(db);
	}

	public void delete(String id) {
		myDB = this.getWritableDatabase();
		myDB.delete(MY_DB_TABLE, KEY_ROWID + "=" + id, null);
	}

	public void insert(QAndAAide qaaa) {
		myDB = this.getWritableDatabase();
		ContentValues initialValues = createContentValues(currentOrderNo++, qaaa.getQuestion(),
				qaaa.getAnswer(), qaaa.getCategory());
		long id = myDB.insert(MY_DB_TABLE, null, initialValues);
		qaaa.setId(new Long(id).toString());
		qAndAs.put(qaaa.getId(), qaaa);
	}

	public void update(QAndAAide qaaa) {
		myDB = this.getWritableDatabase();
		ContentValues initialValues = createContentValues(qaaa.getOrder(), qaaa.getQuestion(),
				qaaa.getAnswer(), qaaa.getCategory());
        myDB.update(MY_DB_TABLE, initialValues,
				KEY_ROWID + "=" + qaaa.getId(), null);
		qAndAs.put(qaaa.getId(), qaaa);
	}

	public void exportAll() {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/qanda");
		dir.mkdirs();
		File file = new File(dir, "Qanda.txt");

		try {
			FileOutputStream f = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(f);
			QAndAAide da = null;
			myDB = this.getWritableDatabase();
			Cursor c = myDB.query(MY_DB_TABLE, new String[] { KEY_ROWID, KEY_ORDER_NO,
					KEY_QUESTION, KEY_ANSWER }, null, null, null, null, null);
			while (c.moveToNext() == true) {
				da = new QAndAAide(c);
				try {
					osw.write(da.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			c.close();
			osw.close();
			f.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException: " + e.getMessage());
		}

	}

	public void importAll() {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/qanda");
		File file = new File(dir, "Qanda.txt");

		try {
			FileInputStream f = new FileInputStream(file);
			DataInputStream in = new DataInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			QAndAAide da = null;
			myDB = this.getWritableDatabase();
			while ((strLine = br.readLine()) != null) {
				da = new QAndAAide(strLine);
				insert(da);
			}

			f.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException: " + e.getMessage());
		}

	}
}