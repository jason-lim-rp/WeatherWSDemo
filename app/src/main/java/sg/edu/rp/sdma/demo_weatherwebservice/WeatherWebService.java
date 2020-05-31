package sg.edu.rp.sdma.demo_weatherwebservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WeatherWebService extends AppCompatActivity {

	ArrayList<String> list4spinner;
	ArrayList<Weather> arrayWeather;
	ArrayList<String> selectedCities;
	CityWeatherAdapter aa;
    ArrayAdapter<String> aa4spinner;
	Spinner spinner;
	Button btnAdd, btnRefresh;
	TextView tv;
	ListView lv;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		spinner = (Spinner) this.findViewById(R.id.spinner);
		btnAdd = (Button) this.findViewById(R.id.button1);
		btnRefresh = (Button) this.findViewById(R.id.btnRefresh);
		tv = (TextView) findViewById(R.id.tv);
		lv = (ListView) this.findViewById(R.id.list);

		arrayWeather = new ArrayList<Weather>();
		selectedCities = new ArrayList<String>();
		list4spinner = new ArrayList<String>();

		aa4spinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list4spinner);
		spinner.setAdapter(aa4spinner);

		aa = new CityWeatherAdapter(this, R.layout.row, arrayWeather);
		lv.setAdapter(aa);

		btnAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String selected = (String) spinner.getSelectedItem();
				selectedCities.add(selected);
				arrayWeather.add(new Weather(selected, "Please Refresh", ""));
				aa.notifyDataSetChanged();
			}
		});

		getCity();
		registerForContextMenu(lv);

		btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				arrayWeather.clear();
				aa.notifyDataSetChanged();

				String [] city_array = new String[selectedCities.size()];
				for (int i = 0; i < selectedCities.size(); i++){
					city_array[i] = selectedCities.get(i);
				}
				WeatherInfoGrabber grabber = new WeatherInfoGrabber();
				grabber.execute(city_array);
			}
		});
	}

	private void getCity(){
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		boolean value = settings.getBoolean("hasrun", false);

		if (value == false){
			selectedCities.add("Woodlands");
			selectedCities.add("Pioneer");
			selectedCities.add("Punggol");
			Editor editor = settings.edit();
			editor.putBoolean("hasrun", true);
			editor.commit();
		} else {
			int num = settings.getInt("num", 0);
			for (int i = 0; i < num; i++){
				String city = settings.getString("city" + i, "Woodlands");
				selectedCities.add(city);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		arrayWeather.clear();
		aa.notifyDataSetChanged();

		String [] city_array = new String[selectedCities.size()];
		for (int i = 0; i < selectedCities.size(); i++){
			city_array[i] = selectedCities.get(i);
		}
		WeatherInfoGrabber grabber = new WeatherInfoGrabber();
		grabber.execute(city_array);
	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putInt("num", selectedCities.size());
		editor.commit();
		for (int i = 0; i< selectedCities.size(); i++){
			String city = selectedCities.get(i);
			editor = settings.edit();
			editor.putString("city"+i, city);
			editor.commit();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("What do you want to do?");
		getMenuInflater().inflate(R.menu.main, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		switch (item.getItemId()) {

			case (R.id.cmDelete): {
				AdapterView.AdapterContextMenuInfo menuInfo;
				menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
				int index = menuInfo.position;
				arrayWeather.remove(index);
				selectedCities.remove(selectedCities.get(index));
				aa.notifyDataSetChanged();
				return true;
			}
		}
		return false;
	}


	private class WeatherInfoGrabber extends AsyncTask<String, Integer, String>{

		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(WeatherWebService.this, "Web Service Demo", "Retrieving data...");
			pd.setProgress(0);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			tv.setText("Weather WS Demo - " + result);
			aa.notifyDataSetChanged();
			aa4spinner.notifyDataSetChanged();
			pd.dismiss();
			super.onPostExecute(result);
		}


		@Override
		protected String doInBackground(String... cities) {
			//Build forecast object for every city
			ArrayList<Weather> al = new ArrayList<>();
			String date = null;

			URL url;
			try{
				String StringUrl = "http://api.nea.gov.sg/api/WebAPI/?dataset=2hr_nowcast&keyref=781CF461BB6606AD4852D40C8C54E93C9193030D51B73AFB";
				url=new URL(StringUrl);

				HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
				int responseCode = httpConnection.getResponseCode();

				if (responseCode == HttpURLConnection.HTTP_OK){
					InputStream in = httpConnection.getInputStream();

					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db=dbf.newDocumentBuilder();

					Document dom=db.parse(in);
					Element docEle=dom.getDocumentElement();

					Element forecastDate = (Element) docEle.getElementsByTagName("forecastIssue").item(0);
					date = forecastDate.getAttribute("date") + " " + forecastDate.getAttribute("time") ;

					NodeList nl=docEle.getElementsByTagName("area");

					if (nl != null && nl.getLength() > 0) {
						for (int i = 0; i < nl.getLength(); i++){
							Element eleItem = (Element) nl.item(i);

							String city = eleItem.getAttribute("name");
							String condition = eleItem.getAttribute("forecast");
							String img = "https://www.nea.gov.sg/assets/images/icons/weather-bg/" + condition + ".PNG";

							al.add(new Weather(city, condition, img));
						}
					}
				}
			}catch (MalformedURLException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}catch (ParserConfigurationException e) {
				e.printStackTrace();
			}catch (SAXException e) {
				e.printStackTrace();
			}

			arrayWeather.clear();
			for (int i = 0; i < cities.length; i ++){
				String city = cities[i];
				for (Weather w: al){
					if (w.getCity().equals(city)){
						arrayWeather.add(w);
					}
				}
			}

			if (list4spinner.size() ==0){
				for (Weather w: al){
					list4spinner.add(w.getCity());
				}
			}

			return date;
		}

	}

}
