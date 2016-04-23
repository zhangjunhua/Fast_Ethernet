/**
 * 
 */
package newClass;

import java.util.ArrayList;

/**
 * @author Admin
 *
 */
public class Cloud {
	private static Cloud cloud = new Cloud();
	private ArrayList<DataCenter> dataCenters;
	private ArrayList<BandWidth> bandWidths;

	private Cloud() {
		dataCenters = new ArrayList<Cloud.DataCenter>();
		bandWidths = new ArrayList<BandWidth>();
	}

	public void addBandWidth(String dc1, String dc2, double bandwidth) {
		bandWidths.add(new BandWidth(dc1, dc2, bandwidth));
	}

	public static Cloud getInstanceofCloud() {
		return cloud;
	}

	public static Cloud getNewInstanceofCloud() {
		cloud = new Cloud();
		return cloud;
	}

	public String toString() {
		// TODO Auto-generated method stub
		return dataCenters.toString() + "\n" + bandWidths.toString();
	}

	/**
	 * @return the cloud
	 */
	public static Cloud getCloud() {
		return cloud;
	}

	/**
	 * @param cloud
	 *            the cloud to set
	 */
	public static void setCloud(Cloud cloud) {
		Cloud.cloud = cloud;
	}

	/**
	 * @return the dataCenters
	 */
	public ArrayList<DataCenter> getDataCenters() {
		return dataCenters;
	}

	public DataCenter getDataCenter(String name) {
		if (name == null)
			return null;
		for (DataCenter dataCenter : dataCenters) {
			if (dataCenter.getName().equals(name)) {
				return dataCenter;
			}
		}
		DataCenter dataCenter = new DataCenter();
		dataCenter.setName(name);
		dataCenters.add(dataCenter);
		return dataCenter;
	}

	public DataCenter getDataCenter(int index) {
		for (DataCenter dataCenter : dataCenters)
			if (dataCenter.getID() == index)
				return dataCenter;
		return null;
	}

	/**
	 * @param dataCenters
	 *            the dataCenters to set
	 */
	public void setDataCenters(ArrayList<DataCenter> dataCenters) {
		this.dataCenters = dataCenters;
	}

	public double getBandWidth(String dc1, String dc2) {
		for (BandWidth bandWidth : bandWidths)
			if ((bandWidth.dc1.equals(dc1) && bandWidth.dc2.equals(dc2))
					|| (bandWidth.dc1.equals(dc2) && bandWidth.dc2.equals(dc1)))
				return bandWidth.bandwidth;
		return 0;
	}

	/**
	 * @return the bandWidths
	 */
	public ArrayList<BandWidth> getBandWidths() {
		return bandWidths;
	}

	/**
	 * @param bandWidths
	 *            the bandWidths to set
	 */
	public void setBandWidths(ArrayList<BandWidth> bandWidths) {
		this.bandWidths = bandWidths;
	}

	private class BandWidth {
		BandWidth(String dc1, String dc2, double bandwidth) {
			this.dc1 = dc1;
			this.dc2 = dc2;
			this.bandwidth = bandwidth;
		}

		String dc1;
		String dc2;
		double bandwidth;

		public String toString() {
			// TODO Auto-generated method stub
			return "\ndc1: " + dc1 + "\tdc2: " + dc2 + "\tbandwidth: "
					+ bandwidth;
		}
	}

	public class DataCenter {
		private String name;
		private double cs;// 数据中心可用存储空间

		public int getID() {
			return Integer.parseInt(name.substring(1));
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		public String toString() {
			// TODO Auto-generated method stub
			return "\nname: " + name + "\tcs: " + cs;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the cs
		 */
		public double getCs() {
			return cs;
		}

		/**
		 * @param cs
		 *            the cs to set
		 */
		public void setCs(double cs) {
			this.cs = cs;
		}

	}
}