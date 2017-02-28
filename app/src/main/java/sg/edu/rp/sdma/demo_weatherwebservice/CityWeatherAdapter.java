package sg.edu.rp.sdma.demo_weatherwebservice;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CityWeatherAdapter extends ArrayAdapter<Weather>{
	
	private Context context;
	private ArrayList<Weather> weatherList;
	private int res;
	
	public CityWeatherAdapter(Context context, int resource, ArrayList<Weather> objects) {
		super(context, resource, objects);
		this.context = context;
		this.weatherList = objects;
		this.res = resource;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		Weather entry = weatherList.get(position);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = (View) inflater.inflate(res, null);
		
		TextView tvheader = (TextView)v.findViewById(R.id.tvrowheader);
		tvheader.setText(entry.getCity());
		 
		   
	    TextView tvcontent = (TextView) v.findViewById(R.id.tvrowcontent);
	    tvcontent.setText(entry.getCondition()+"\nTemperature : "+entry.getTemp());
	       
	    ImageView ivicon = (ImageView) v.findViewById(R.id.imageView1);
	    ivicon.setImageDrawable(entry.getIcon());

		return v;
	}

	
	
}
