package com.example.sample;

public class CustomBean {
	/** 名称 */
	private String name;
	/**
	 * 跳转
	 */
	private String activityUri;

	public CustomBean() {

	}

	public CustomBean(String name, String activityUri) {
		super();
		this.name = name;
		this.activityUri = activityUri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActivityUri() {
		return activityUri;
	}

	public void setActivityUri(String activityUri) {
		this.activityUri = activityUri;
	}

	@Override
	public String toString() {
		return "CustomBean [name=" + name + ", activityUri=" + activityUri + "]";
	}
}
