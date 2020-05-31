package sg.edu.rp.sdma.demo_weatherwebservice;

import android.graphics.drawable.Drawable;

public class Weather {

	private String city;
	private String condition;
	private String imgurl;
	
	public Weather(String city, String condition, String imgurl) {
		this.city = city;
		this.condition = condition;
		this.imgurl = imgurl;
	}
	
	public String getCity() {
		return city;
	}
    public String getImgurl() { return imgurl; }
    public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
}
