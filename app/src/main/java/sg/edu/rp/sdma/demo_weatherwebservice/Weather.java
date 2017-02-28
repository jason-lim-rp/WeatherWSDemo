package sg.edu.rp.sdma.demo_weatherwebservice;

import android.graphics.drawable.Drawable;

public class Weather {

	String city;
	String temp;
	String condition;
	Drawable icon;
	
	public Weather(String city, String temp, String condition, Drawable icon) {
		this.city = city;
		this.temp = temp;
		this.condition = condition;
		this.icon = icon;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	

}
