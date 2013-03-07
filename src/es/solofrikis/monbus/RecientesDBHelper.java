package es.solofrikis.monbus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


@SuppressWarnings("javadoc")
public class RecientesDBHelper extends SQLiteOpenHelper {
	 
    private static String DB_NAME = "usuario";
    
    private static int DB_VERSION = 1;
 
    private SQLiteDatabase db;
    
    

    public RecientesDBHelper(final Context context) { 
    	super(context, DB_NAME, null, DB_VERSION);
    	this.db = this.getReadableDatabase();
    }
    
    @Override
	public void onCreate(SQLiteDatabase db) {
    	onUpgrade(db,0,DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch(oldVersion){
		case 0:
		case 1:
			db.execSQL("CREATE TABLE recientes ( _id INTEGER PRIMARY KEY AUTOINCREMENT, idorigen      INTEGER, iddestino     INTEGER, nombreorigen  TEXT, nombredestino TEXT)");
		}
	}
    

	
	public Cursor getRecientes(){
		String sqlQuery = "SELECT * FROM recientes ORDER BY _id DESC";
		Cursor result = null;
		if (this.db!=null)
		{           
		 result = this.db.rawQuery(sqlQuery, null);
		}
		return result;
	}
	
	public void addReciente(String idorigen, String iddestino, String nombreorigen, String nombredestino){
		String sqlQuery = "SELECT count(*) AS total FROM recientes";
		Cursor result = null;
		if (this.db!=null)
		{
			result = this.db.rawQuery(sqlQuery, null);
			result.moveToFirst();
			
			if(!checkRecienteDuplicado(idorigen, iddestino)){
				if(result.getInt(result.getColumnIndex("total"))<4){
					ContentValues cv = new ContentValues();
					cv.put("idorigen",idorigen);
					cv.put("iddestino",iddestino);
					cv.put("nombreorigen",nombreorigen);
					cv.put("nombredestino",nombredestino);
					this.db.insert("recientes", null, cv);
				}else{

					ContentValues cv = new ContentValues();
					cv.put("idorigen",idorigen);
					cv.put("iddestino",iddestino);
					cv.put("nombreorigen",nombreorigen);
					cv.put("nombredestino",nombredestino);
					this.db.insert("recientes", null, cv);
					
					String sqlQuery2 = "delete from recientes where _id = (select _id from recientes order by _id ASC limit 1)";
					this.db.execSQL(sqlQuery2);
				}
			}
		}
	}
	
	public boolean checkRecienteDuplicado(String idorigen, String iddestino){
		String sqlQuery = "SELECT count(*) AS total FROM recientes WHERE idorigen="+idorigen+" AND iddestino="+iddestino;
		Cursor result = null;
		if (this.db!=null)
		{
			result = this.db.rawQuery(sqlQuery, null);
			result.moveToFirst();
			if(result.getInt(result.getColumnIndex("total"))<1){
				return false;
			}
		}
		return true;
	}
    
    @Override
	public synchronized void close() {
    	 
	    if(this.db != null)
		    this.db.close();

	    super.close();

    }

	

}

