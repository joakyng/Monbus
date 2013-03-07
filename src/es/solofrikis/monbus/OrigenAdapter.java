package es.solofrikis.monbus;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.TextView;


@SuppressWarnings("javadoc")
public class OrigenAdapter extends CursorAdapter implements OnItemClickListener{
	
	private DBHelper db = null;
	private Activity activity;

	public OrigenAdapter(Context context, Cursor c, Activity activity) {
		super(context, c);
		this.db = new DBHelper(context);
		this.db.openDataBase();
		this.activity = activity;
		
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
 
        currentCursor = this.db.getOrigenCursor(args);
 
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
		// Get the cursor, positioned to the corresponding row in the result set
        Cursor cursor = (Cursor) listView.getItemAtPosition(position);

        // Get the Item Number from this row in the database.
        String origenId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));

        // Update the parent class's TextView
        AutoCompleteTextView autocompletetextview = (AutoCompleteTextView)this.activity.findViewById(R.id.AutoCompleteTextViewOrigen);
        
        autocompletetextview.setText(nombre);
        View next = autocompletetextview.focusSearch(View.FOCUS_DOWN);
        if(next!=null)
        	next.requestFocus();
        
        //Guardamos el id del origen
        TextView textViewOrigenBuffer = (TextView)this.activity.findViewById(R.id.textViewOrigenBuffer);
        textViewOrigenBuffer.setText(origenId);
	}

}
