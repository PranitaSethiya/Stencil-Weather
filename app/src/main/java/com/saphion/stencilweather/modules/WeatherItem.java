package com.saphion.stencilweather.modules;

public class WeatherItem {

	private String uniqueID;
	private String name;
	private Integer temp;
	private String condition;
	private Integer position;
	private Integer condID;
	private String unit;

	public WeatherItem(String uniqueID, String name, Integer temp, String condition,
					   Integer position, Integer condID, String unit) {
		this.uniqueID = uniqueID;
		this.name = name;
		this.temp = temp;
		this.condition = condition;
		this.position = position;
		this.condID = condID;
		this.unit = unit;
	}

	public WeatherItem(WeatherItem i) {
		this.name = i.getName();
		this.temp = i.getTemp();
		this.condition = i.getCondition();
		this.position = i.getPosition();
		this.condID = i.getCondID();
		this.unit = i.getUnit();
		this.uniqueID = i.getUniqueID();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTemp(Integer temp) {
		this.temp = temp;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void setCondID(Integer condID) {
		this.condID = condID;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return this.unit;
	}

	public String getName() {
		return this.name;
	}

	public Integer getTemp() {
		return this.temp;
	}

	public String getBuildTemp() {
		return temp + "°" + unit;
	}

	public String getCondition() {
		return this.condition;
	}

	public Integer getPosition() {
		return this.position;
	}

	public Integer getCondID() {
		return this.condID;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
}
