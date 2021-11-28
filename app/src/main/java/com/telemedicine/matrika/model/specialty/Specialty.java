package com.telemedicine.matrika.model.specialty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Specialty implements Serializable {

	public static String ID = "id";
	public static String TITLE = "title";
	public static String SPECIALTY = "specialty";

	@SerializedName("id")
	private String id;

	@SerializedName("icon")
	private String icon;

	@SerializedName("specialty")
	private String specialty;

	@SerializedName("description")
	private String description;

	@SerializedName("title")
	private String title;

	@SerializedName("fields")
	private HashMap<String, Field> fields;

	private List<String> specialists;

	public Specialty() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public HashMap<String, Field> getFields() {
		return fields;
	}

	public void setFields(HashMap<String, Field> fields) {
		this.fields = fields;
	}

	public List<String> getSpecialists() {
		return specialists;
	}

	public void setSpecialists(List<String> specialists) {
		this.specialists = specialists;
	}
}