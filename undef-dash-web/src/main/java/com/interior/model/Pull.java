package com.interior.model;

import java.util.Date;
import java.util.LinkedHashMap;

public class Pull {
	private Date date;
	private LinkedHashMap<String, String> mapFigures;

	public Pull() {
		super();
		mapFigures = new LinkedHashMap<String, String>();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public LinkedHashMap<String, String> getMapFigures() {
		return mapFigures;
	}

	public void setMapFigures(LinkedHashMap<String, String> mapFigures) {
		this.mapFigures = mapFigures;
	}

}
