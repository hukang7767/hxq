package com.alpha.hxq;


public class LeDevice {
	private final String mac;
	private String name;
	private String rxData = "No data";
	private int rssi;
	private boolean oadSupported = false;
	private boolean isConnect;

	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean connect) {
		isConnect = connect;
	}

	public LeDevice(String name, String mac) {
		this.name = name;
		this.mac = mac;
	}

	public LeDevice(String name, String mac, int rssi, byte[] scanRecord,boolean isConnect) {
		this.name = name;
		this.mac = mac;
		this.rssi = rssi;
		this.isConnect = isConnect;
	}

	public boolean isOadSupported() {
		return oadSupported;
	}

	public void setOadSupported(boolean oadSupported) {
		this.oadSupported = oadSupported;
	}


	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMac() {
		return mac;
	}

	public String getRxData() {
		return rxData;
	}

	public void setRxData(String rxData) {
		this.rxData = rxData;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof LeDevice) {
			return ((LeDevice) o).getMac().equals(mac);
		}
		return false;
	}
}
