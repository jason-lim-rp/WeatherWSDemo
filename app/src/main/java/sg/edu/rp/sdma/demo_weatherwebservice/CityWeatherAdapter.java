package sg.edu.rp.sdma.demo_weatherwebservice;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = (View) inflater.inflate(res, null);

        Weather entry = weatherList.get(position);

        TextView tvheader = (TextView)v.findViewById(R.id.tvrowheader);
        TextView tvcontent = (TextView) v.findViewById(R.id.tvrowcontent);
        ImageView ivicon = (ImageView) v.findViewById(R.id.imageView1);

        tvheader.setText(entry.getCity());

        switch (entry.getCondition()){
            case "PC": entry.setCondition("Partly Cloudy");
                    break;
            case "TL": entry.setCondition("Thundery Showers");
                    break;
			case "LS": entry.setCondition("Light Showers");
					break;
        }

	    tvcontent.setText(entry.getCondition()+"\nTemperature : Not Available");
	    if (entry.getImgurl().isEmpty()){
            Picasso.get().load(R.drawable.na).into(ivicon);
        } else {
            Picasso.get().load(entry.getImgurl()).into(ivicon);
        }

		return v;
	}

	
	
}
