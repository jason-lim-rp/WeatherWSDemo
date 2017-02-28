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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class WeatherWebService extends AppCompatActivity {

	ArrayList<Weather> arrayWeather;
	ArrayList<String> cities;
	CityWeatherAdapter aa;
	EditText etAdd;
	Button btnAdd;
	ListView lv;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		etAdd = (EditText) this.findViewById(R.id.etadd);
		btnAdd = (Button) this.findViewById(R.id.button1);
		lv = (ListView) this.findViewById(R.id.list);

		arrayWeather = new ArrayList<Weather>();
		cities = new ArrayList<String>();

		aa = new CityWeatherAdapter(this, R.layout.row, arrayWeather);
		lv.setAdapter(aa);

		btnAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (etAdd.getText().length() != 0){
					String data = etAdd.getText().toString();
					cities.add(data);
					etAdd.setText("");

					WeatherInfoGrabber grabber = new WeatherInfoGrabber();
					grabber.execute(new String[] {data});
				}
			}
		});

		getCity();
		registerForContextMenu(lv);
	}

	private void getCity(){
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		boolean value = settings.getBoolean("hasrun", false);

		if (value == false){
			cities.add("Kuala Lumpur");
			cities.add("Tokyo");
			cities.add("Taipei");
			Editor editor = settings.edit();
			editor.putBoolean("hasrun", true);
			editor.commit();
		} else {
			int num = settings.getInt("num", 0);
			for (int i = 0; i < num; i++){
				String city = settings.getString("city" + i, "");
				cities.add(city);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		arrayWeather.clear();
		aa.notifyDataSetChanged();

		WeatherInfoGrabber grabber = new WeatherInfoGrabber();
		String [] city_array = new String[cities.size()];
		for (int i = 0; i < cities.size(); i++){
			city_array[i] = cities.get(i);
		}
		grabber.execute(city_array);

	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putInt("num", cities.size());
		editor.commit();
		for (int i = 0; i< cities.size(); i++){
			String city = cities.get(i);
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
				cities.remove(cities.get(index));
				aa.notifyDataSetChanged();
				return true;
			}
		}
		return false;
	}

	private Weather XMLParser(String cityname){
		String temp = null;
		String condition = null;
		Drawable icon = null;

		URL url;
		try{
			String StringUrl1 = "https://query.yahooapis.com/v1/public/yql?q=";
			String StringUrl2 = "&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
			String query1 = "select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22";
			String query2 = "%22)";
			String StringUrl = StringUrl1 + query1 + cityname.replace(" ", "%20") +  query2 + StringUrl2;
			Log.d("",StringUrl);
			url=new URL(StringUrl);

			URLConnection connection;
			connection=url.openConnection();

			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK){
				InputStream in = httpConnection.getInputStream();

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db=dbf.newDocumentBuilder();

				Document dom=db.parse(in);

				Element docEle=dom.getDocumentElement();
				NodeList nl=docEle.getElementsByTagName("item");

				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++){
						Element eleItem = (Element) nl.item(i);

						Element eleCondition=(Element) eleItem.getElementsByTagName("yweather:condition").item(0);
						String tempF = eleCondition.getAttributeNode("temp").getValue();
						temp = String.format("%.2f", ((Double.parseDouble(tempF) - 32 ) * 5/9.0));
						condition = eleCondition.getAttributeNode("text").getValue();

						Element eleDescription =(Element) eleItem.getElementsByTagName("description").item(0);
						String desc = eleDescription.getTextContent();
						int start = desc.indexOf("src");
						int end = desc.indexOf("gif");

						String img = desc.substring(start+5, end+3);
						InputStream is = (InputStream) new URL(img).getContent();
						icon = Drawable.createFromStream(is, img);

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

		if (cityname == null || temp == null || condition == null || icon == null)
			return null;
		else
			return new Weather(cityname, temp, condition, icon);
	}

	private class WeatherInfoGrabber extends AsyncTask<String, Integer, Long>{

		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(WeatherWebService.this, "Web Service Demo", "Retrieving data...");
			pd.setProgress(0);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Long result) {
			pd.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			pd.setProgress(values[0]);
			aa.notifyDataSetChanged();
			super.onProgressUpdate(values);
		}

		@Override
		protected Long doInBackground(String... params) {
			for (int i = 0; i < params.length; i ++){
				String city = params[i];
				Log.d("",city);
				Weather tmp = XMLParser(city);
				arrayWeather.add(tmp);
				publishProgress(10000/params.length*(i+1));
			}
			return null;
		}

	}

}
