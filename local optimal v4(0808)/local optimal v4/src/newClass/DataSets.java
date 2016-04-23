/**
 * 
 */
package newClass;

import java.util.ArrayList;

import newClass.Tasks.Task;

/**
 * @author Admin
 *
 */
public class DataSets {
	private static DataSets dataset = new DataSets();
	ArrayList<DataSet> dataSets;
	private int distinctDataNum = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return dataSets.toString();
	}

	private DataSets() {
		dataSets = new ArrayList<DataSet>();
	}

	public static DataSets getInstanceofDataSets() {
		return dataset;
	}

	public static DataSets getNewInstanceofDataSets() {
		dataset = new DataSets();
		return dataset;
	}

	public DataSet getDataset(String name, int copyno) {
		for (DataSet dataSet : dataSets) {
			if (dataSet.getName().equals(name) && dataSet.getCopyNo() == copyno) {
				return dataSet;
			}
		}
		DataSet dataSet = new DataSet();
		dataSet.setName(name);
		dataSet.setCopyNo(copyno);
		dataSets.add(dataSet);
		return dataSet;
	}

	public DataSet getDataset(int index) {
		for (DataSet dataSet : dataSets) {
			if (dataSet.getID() == index)
				return dataSet;
		}
		return null;
	}

	/**
	 * @return the dataset
	 */
	public static DataSets getDataset() {
		return dataset;
	}

	public int gettheCopyNum(String name) {
		int count = 0;
		for (DataSet dataSet : dataSets) {
			if (dataSet.getName().equals(name))
				count++;
		}
		return count;
	}

	/**
	 * @return the distinctDataNum
	 */
	public int getDistinctDataNum() {
		if (distinctDataNum != 0)
			return distinctDataNum;
		for (DataSet dataSet : dataSets) {
			if (dataSet.getCopyNo() == 1)
				distinctDataNum++;
		}
		return distinctDataNum;
	}

	/**
	 * @param dataset
	 *            the dataset to set
	 */
	public static void setDataset(DataSets dataset) {
		DataSets.dataset = dataset;
	}

	/**
	 * @return the dataSets
	 */
	public ArrayList<DataSet> getDataSets() {
		return dataSets;
	}

	/**
	 * @param dataSets
	 *            the dataSets to set
	 */
	public void setDataSets(ArrayList<DataSet> dataSets) {
		this.dataSets = dataSets;
	}

	public class DataSet {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			String string = new String();
			string = "datasetname:" + name + "\tcopyno:" + CopyNo;
			string += "\tgt:" + gt + "\tdatasize:" + datasize + "\n";
			return string;

		}

		private String name;
		private int CopyNo;
		private ArrayList<Task> usedtasks;
		private Task createtask;
		private int gt = 0;
		private double datasize = 0;

		public int getID() {
			return Integer.parseInt(name.substring(1));
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the copyNo
		 */
		public int getCopyNo() {
			return CopyNo;
		}

		/**
		 * @param copyNo
		 *            the copyNo to set
		 */
		public void setCopyNo(int copyNo) {
			CopyNo = copyNo;
		}

		/**
		 * @return the usedtasks
		 */
		public ArrayList<Task> getUsedtasks() {
			return usedtasks;
		}

		/**
		 * @param usedtasks
		 *            the usedtasks to set
		 */
		public void setUsedtasks(ArrayList<Task> usedtasks) {
			for (Task task : usedtasks) {
				task.getInputDataSets().add(this);
			}
		}

		/**
		 * @return the createtasks
		 */
		public Task getCreatetask() {
			return createtask;
		}

		/**
		 * @param createtask
		 *            the createtasks to set
		 */
		public void setCreatetask(Task createtask) {
			if (createtask == null)
				return;
			this.createtask=createtask;
			this.createtask.getOutputDataSets().add(this);
		}

		/**
		 * @return the gt
		 */
		public int getGt() {
			return gt;
		}

		/**
		 * @param gt
		 *            the gt to set
		 */
		public void setGt(int gt) {
			this.gt = gt;
		}

		/**
		 * @return the datasize
		 */
		public double getDatasize() {
			return datasize;
		}

		/**
		 * @param datasize
		 *            the datasize to set
		 */
		public void setDatasize(double datasize) {
			this.datasize = datasize;
		}
	}
}
