/**
 * 
 */
package newClass;

import java.util.ArrayList;

import newClass.DataSets.DataSet;

/**
 * @author Admin
 *
 */
public class Tasks {
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return tasks.toString();
	}

	private static Tasks task = new Tasks();
	private ArrayList<Task> tasks;

	private Tasks() {
		// TODO Auto-generated constructor stub
		tasks = new ArrayList<Task>();
	}

	public static Tasks getInstanceofTasks() {
		return task;
	}

	public static Tasks getNewInstanceofTasks() {
		task = new Tasks();
		return task;
	}

	/**
	 * @return the tasks
	 */
	public ArrayList<Task> getTasks() {
		return tasks;
	}

	/**
	 * @param tasks
	 *            the tasks to set
	 */
	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}

	public Task getTask(String name) {
		if (name == null)
			return null;
		for (Task task : tasks) {
			if (task.getName().equals(name)) {
				return task;
			}
		}
		Task task = new Task();
		task.setName(name);
		tasks.add(task);
		return task;
	}

	public Task getTask(int index) {
		for (Task task : tasks) {
			if (task.getID() == index)
				return task;
		}
		return null;
	}

	public class Task {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			String string = new String();
			string = "taskname:" + name + "\tpredecessors:";
			for (Task task : Predecessors) {
				string += task.getName();
			}
			string += "\tSuccessors:";
			for (Task task : Successors) {
				string += task.getName();
			}
			string += "\tinputDataSets:";
			for (DataSet dataSet : inputDataSets) {
				string += dataSet.getName();
			}
			string += "\toutputDataSets:";
			for (DataSet dataSet : outputDataSets) {
				string += dataSet.getName();
			}
			return "\n" + string;

		}

		private String name;
		private ArrayList<Task> Predecessors;
		private ArrayList<Task> Successors;
		private ArrayList<DataSet> inputDataSets;
		private ArrayList<DataSet> outputDataSets;

		public int getID() {
			return Integer.parseInt(name.substring(1));
		}

		/**
		 * 
		 */
		public Task() {
			super();
			// TODO Auto-generated constructor stub
			Predecessors = new ArrayList<Task>();
			inputDataSets = new ArrayList<DataSet>();
			outputDataSets = new ArrayList<DataSet>();
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
		 * @return the predecessors
		 */
		public ArrayList<Task> getPredecessors() {
			return Predecessors;
		}

		/**
		 * @param predecessors
		 *            the predecessors to set
		 */
		public void setPredecessors(ArrayList<Task> predecessors) {
			Predecessors = predecessors;
		}

		/**
		 * @return the successors
		 */
		public ArrayList<Task> getSuccessors() {
			return Successors;
		}

		/**
		 * @param successors
		 *            the successors to set
		 */
		public void setSuccessors(ArrayList<Task> successors) {
			Successors = successors;
			for (Task task : successors) {
				task.getPredecessors().add(this);
			}
		}

		/**
		 * @return the inputDataSets
		 */
		public ArrayList<DataSet> getInputDataSets() {
			return inputDataSets;
		}

		/**
		 * @param inputDataSets
		 *            the inputDataSets to set
		 */
		public void setInputDataSets(ArrayList<DataSet> inputDataSets) {
			this.inputDataSets = inputDataSets;
		}

		/**
		 * @return the outputDataSets
		 */
		public ArrayList<DataSet> getOutputDataSets() {
			return outputDataSets;
		}

		/**
		 * @param outputDataSets
		 *            the outputDataSets to set
		 */
		public void setOutputDataSets(ArrayList<DataSet> outputDataSets) {
			this.outputDataSets = outputDataSets;
		}

	}
}