package es.solofrikis.monbus;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.TextView;


@SuppressWarnings("javadoc")
public class DestinoAdapter extends CursorAdapter implements OnItemClickListener{
	
	private DBHelper db = null;
	private Activity activity;
	TextView textViewOrigenBuffer;


	public DestinoAdapter(final Context context, final Cursor c, final Activity activity) {
		super(context, c);
		this.db = new DBHelper(context);
		this.db.openDataBase();
		this.activity = activity;
		this.textViewOrigenBuffer = (TextView)this.activity.findViewById(R.id.textViewOrigenBuffer);
		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String item = createItem(cursor);       
        ((TextView) view).setText(item);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
        final TextView view = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line , parent, false);
        
        String item = createItem(cursor);
        view.setText(item);
        return view;
	}
	
	@Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint)
    {
        Cursor currentCursor = null;
        
        if (getFilterQueryProvider() != null)
            return getFilterQueryProvider().runQuery(constraint);
        
        String args = "";
        
        if (constraint != null)
            args = constraint.toString();
        currentCursor = this.db.getDestinoCursor(args,Integer.parseInt(this.textViewOrigenBuffer.getText().toString()));
 
        return currentCursor;
    }
	
	
	
	private String createItem(Cursor cursor)
    {
        String item = cursor.getString(1);
        return item;
    }
	    

	public void close()
    {
        this.db.close();
    }

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		// Obtenemos el cursor en la posisicion pulsada
        Cursor cursor = (Cursor) listView.getItemAtPosition(position);

        // Obtenemos los datos de el cursor
        int destinoId = cursor.getInt(cursor.getColumnIndex("_id"));
        String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
        
        

        // Update the parent class's TextView
        AutoCompleteTextView autocompletetextview = (AutoCompleteTextView)this.activity.findViewById(R.id.AutoCompleteTextViewDestino);
        autocompletetextview.setText(nombre);
        
        //Guardamos el id del destino
	    TextView textViewDestinoBuffer = (TextView)this.activity.findViewById(R.id.textViewDestinoBuffer);
	    textViewDestinoBuffer.setText(String.valueOf(destinoId));
	    
	    //Cambiar el foco al siguiente
        View next = autocompletetextview.focusSearch(View.FOCUS_DOWN);
        InputMethodManager imm = (InputMethodManager)this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(next.getWindowToken(),0);
        if(next!=null)
        	next.requestFocus();
		
        
	}

}
