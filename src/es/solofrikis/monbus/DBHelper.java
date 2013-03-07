package es.solofrikis.monbus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


@SuppressWarnings("javadoc")
public class DBHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/es.solofrikis.monbus/databases/";
	 
    private static String DB_NAME = "monbus";
    
    private static int DB_VERSION = 1;
 
    private SQLiteDatabase db; 
 
    private final Context context;
    
    

    public DBHelper(final Context context) {
    	 
    	super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }
    

    public void createDataBase() throws IOException{
    	 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//no hace nada, la bd ya existe
    	}else{
    		//By calling this method and empty database will be created into the default system path
    		//of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
        	try {
    			copyDataBase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	}
    	}
    }
    
    private boolean checkDataBase(){
    	 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}catch(SQLiteException e){
    		//dtabase does't exist yet.
    	}
 
    	if(checkDB != null)
    		checkDB.close();
 
    	return checkDB != null ? true : false;
    }
    
    private void copyDataBase() throws IOException{
    	 
    	//Open your local db as the input stream
    	InputStream myInput = this.context.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
    

    public void openDataBase() throws SQLException{
    	 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	this.db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
 
    }
    

	public Cursor getOrigenCursor(final String args)
	{
    	StringBuilder sqlQuery = new StringBuilder();
		Cursor result = null;
		   
		sqlQuery.append(" SELECT _id, nombre ");
		sqlQuery.append(" FROM origenes");
		sqlQuery.append(" WHERE nombre LIKE '%" + args + "%' ");
		sqlQuery.append(" ORDER BY nombre");
		   
		
		if (this.db!=null)
		{           
		 result = this.db.rawQuery(sqlQuery.toString(), null);
		}
		return result;
	}
    
    

	public Cursor getDestinoCursor(final String nombre,final int origen)
	{       
    	StringBuilder sqlQuery = new StringBuilder();
		Cursor result = null;
		//SELECT origenes._id, nombre FROM origenes INNER JOIN destinos ON origenes._id = destinos.destino WHERE nombre like '%coru%' AND destinos.origen=10530;
		sqlQuery.append(" SELECT origenes._id, nombre");
		sqlQuery.append(" FROM origenes");
		sqlQuery.append(" INNER JOIN destinos");
		sqlQuery.append(" ON origenes._id = destinos.destino");
		sqlQuery.append(" WHERE nombre LIKE '%" + nombre + "%' ");
		sqlQuery.append(" AND destinos.origen=" + origen);
		sqlQuery.append(" ORDER BY nombre");
		   
		if (this.db!=null)
		{           
		 result = this.db.rawQuery(sqlQuery.toString(), null);
		}
		return result;
	}
	
    
    @Override
	public synchronized void close() {
    	 
	    if(this.db != null)
		    this.db.close();

	    super.close();

    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		return;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(checkDataBase()){
			File dbfile = new File(DB_PATH+DB_NAME);
			dbfile.delete();
			try {
				this.createDataBase();
			} catch (IOException ioe) {
				throw new Error("Imposible crear la Base de Datos");
			}
		}
	}

}

