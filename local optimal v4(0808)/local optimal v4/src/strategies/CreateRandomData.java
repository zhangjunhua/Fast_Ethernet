package strategies;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

//
/**
 * 用于构造随机的测试数据，这些随机的测试数据以xml文件的形式放在文件夹里
 * 
 * 科学工作流分为任务，数据和控制流，由于这里控制流的不同对本程序运行的结果没有实质性的影响，
 * 目前暂时采用线性控制流程（以后有时间将其改进为更加符合实际情况的流程），并将控制流程以图的形式存放在任务的集合中。
 * 
 * 为了生成合理的测试数据，测试数据的生成顺序为：数据中心及带宽->数据集和任务集->数据集副本集合。（待优化）
 */

public class CreateRandomData {
	static Random random = new Random();
	public static Scanner scanner;

	/**
	 * 配置主要为 inputdatafolder 和 outputdatafolder
	 * 更新configuration文件里面的配置，更新R文件里面的配置
	 */
	public static void newfolderandrstconf() {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					new File(R.FOLDER + R.CONFIGURATION)));
			for (Entry<String, String> entry : R.configerationhMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key.equals("outputdatafolder")) {
					value = value.substring(0, 10)
							+ (Integer.parseInt(value.substring(10)) + 1);
				}
				if (key.equals("inputdatafolder")) {
					value = value.substring(0, 9)
							+ (Integer.parseInt(value.substring(9)) + 1);
				}
				R.configerationhMap.put(key, value);
				bufferedWriter.write(key + "\t " + value + "\n");
			}
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		R.inputFolder = R.configerationhMap.get("inputdatafolder") + "/";
		R.outputFolder = R.configerationhMap.get("outputdatafolder") + "/";
		File file = new File(R.FOLDER + R.inputFolder);
		file.mkdirs();
		file = new File(R.FOLDER + R.outputFolder);
		file.mkdirs();
	}

	/**
	 * 测试使用
	 */
	public static void test1() {
		double totalstorage = 0;
		double totalsize = 0;

		// 构建数据中心及带宽
		DR.printlnLineInfo("构建数据中心及带宽");
		final int DCnum = randomint(R.minDCnum, R.maxDCnum);
		DC[] dcs = new DC[DCnum];
		for (int i = 0; i < DCnum; i++) {
			dcs[i] = new DC();
			dcs[i].name = "c" + (i + 1);
			dcs[i].storage = randomdouble(R.minDCstorage, R.maxDCstorage);
			totalstorage += dcs[i].storage;
		}
		double[] bandWidthes = new double[DCnum * (DCnum - 1) / 2];
		for (int i = 0; i < bandWidthes.length; i++) {
			bandWidthes[i] = randomdouble(R.minBandWith, R.maxBandWidth);
		}
		// 构建初始数据集及任务集
		DR.printlnLineInfo("构建初始数据集及任务集");
		final int DSnum = randomint(R.miniDSnum, R.maxiDSnum);
		final int Tnum = randomint(R.minTnum, R.maxTnum);
		DS[] dss = new DS[DSnum + Tnum];
		for (int i = 0; i < DSnum + Tnum; i++) {
			dss[i] = new DS();
			dss[i].name = "d" + (i + 1);
			if (i < DSnum) {
				dss[i].gt = 0;
			} else {
				dss[i].gt = 1;
			}
		}
		for (int i = 0; i < DSnum + Tnum; i++) {
			dss[i].size = randomdouble(R.minDsSize, R.maxDsSize);
			// if (i < DSnum)
			dss[i].copyno = R.maxCopyno > 0 ? randomint(R.minCopyno,
					R.maxCopyno) : randomint(1, 5);
			totalsize += dss[i].size * dss[i].copyno;
			if (totalsize > totalstorage * R.lamda) {
				totalsize = 0;
				i = -1;
			}
		}
		T[] ts = new T[Tnum];
		for (int i = 0; i < Tnum; i++) {
			ts[i] = new T();
			ts[i].name = "t" + (i + 1);
			dss[i + DSnum].createtask = ts[i].name;
		}
		for (int i = 0; i < Tnum - 1; i++) {
			dss[i + DSnum].usedtasks.add(ts[i + 1].name);
			ts[i].successor = "t" + (i + 2);
		}

		for (int i = 0; i < Tnum; i++) {
			if (i == 0) {// 第一个task
				ts[i].needed = randomint(R.minIDS, R.maxIDS);
				for (int j = 0; j < ts[i].needed; j++) {// 使用originalDS
					int randd = randomint(0, DSnum - 1);
					if (!dss[randd].usedtasks.contains(ts[i].name)) {
						dss[randd].usedtasks.add(ts[i].name);
					} else {
						j--;
					}
				}
			} else {// 后面的task
				int ori_needed;
				int gen_needed;
				if (i < 3) {
					ori_needed = randomint(0, 3);
					if (i > 1)
						gen_needed = randomint(0, 1);
					else
						gen_needed = 0;
				} else {
					ori_needed = randomint(0, 1);
					gen_needed = randomint(0, 2);
				}
				ts[i].needed = ori_needed + gen_needed;
				for (int j = 0; j < ori_needed; j++) {// 使用originalDS
					int randd = randomint(0, DSnum - 1);
					if (!dss[randd].usedtasks.contains(ts[i].name)) {
						dss[randd].usedtasks.add(ts[i].name);
					} else {
						j--;
					}
				}
				for (int j = 0; j < gen_needed; j++) {// 使用generatedDS
					int randd = randomint(DSnum, DSnum + i - 1);
					if (!dss[randd].usedtasks.contains(ts[i].name)) {
						dss[randd].usedtasks.add(ts[i].name);
					} else {
						j--;
					}
				}
			}
		}

		// //
		// ==========================================================================================================================
		// // 将构建好的随机测试数据写入文件
		// DR.printlnLineInfo("将构建好的随机测试数据写入文件");
		// // 数据中心和网络
		// {
		// Document cloudDocument = DocumentHelper.createDocument();
		// Element rootCloudElement = cloudDocument.addElement("cloud");
		// for (int i = 0; i < DCnum; i++) {
		// Element datacenterElement = rootCloudElement
		// .addElement("datacenter");
		// datacenterElement.addElement("name").setText(dcs[i].name);
		// datacenterElement.addElement("cs").setText("" + dcs[i].storage);
		// }
		// int pointer = 0;
		// for (int i = 0; i < DCnum - 1; i++) {
		// for (int j = i + 1; j < DCnum; j++) {
		// Element bandwidthElement = rootCloudElement
		// .addElement("bandwidth");
		// bandwidthElement.addElement("dc1").setText(dcs[i].name);
		// bandwidthElement.addElement("dc2").setText(dcs[j].name);
		// bandwidthElement.addElement("bw").setText(
		// "" + bandWidthes[pointer++]);
		// }
		// }
		// OutputFormat format = OutputFormat.createPrettyPrint();
		// XMLWriter writer = new XMLWriter(new FileWriter(new File(R.FOLDER
		// + R.inputFolder + R.CLOUD)), format);
		// writer.write(cloudDocument);
		// writer.close();
		// }
		// // 带有副本的数据集
		// {
		// Document datasetsDocument = DocumentHelper.createDocument();
		// Element rootElement = datasetsDocument.addElement("datasets");
		// for (int i = 0; i < DSnum + Tnum; i++) {
		// for (int j = 0; j < dss[i].copyno; j++) {
		// Element datasetElement = rootElement.addElement("dataset");
		// datasetElement.addElement("name").setText(dss[i].name);
		// datasetElement.addElement("copyno").setText("" + (j + 1));
		// datasetElement.addElement("datasize").setText(
		// "" + dss[i].size);
		// datasetElement.addElement("gt").setText("" + dss[i].gt);
		// if (dss[i].createtask != null)
		// datasetElement.addElement("createtask").setText(
		// dss[i].createtask);
		// if (j == 0) {
		// Element usedtasksElement = datasetElement
		// .addElement("usedtasks");
		// for (String t : dss[i].usedtasks)
		// usedtasksElement.addElement("task").setText(t);
		// }
		// }
		// }
		// OutputFormat format = OutputFormat.createPrettyPrint();
		// XMLWriter writer = new XMLWriter(new FileWriter(new File(R.FOLDER
		// + R.inputFolder + R.DATASETS)), format);
		// writer.write(datasetsDocument);
		// writer.close();
		// }
		//
		// // 任务集
		// {
		// Document controlDocument = DocumentHelper.createDocument();
		// Element rootElement = controlDocument.addElement("tasks");
		// for (int i = 0; i < Tnum; i++) {
		// Element taskElement = rootElement.addElement("task");
		// taskElement.addElement("name").setText(ts[i].name);
		// if (ts[i].successor != null)
		// taskElement.addElement("nexttasks").addElement("nexttask")
		// .setText(ts[i].successor);
		// }
		// OutputFormat format = OutputFormat.createPrettyPrint();
		// XMLWriter writer = new XMLWriter(new FileWriter(new File(R.FOLDER
		// + R.inputFolder + R.CONTROL)), format);
		// writer.write(controlDocument);
		// writer.close();
		// }
	}

	/**
	 * 根据之前的改造过来，配合DR里面相应时间段的函数一起使用
	 * 
	 * @throws IOException
	 */
	public static void createData_2015_8_22() throws IOException {
		double totalstorage = 0;
		double totalsize = 0;

		// 构建数据中心及带宽
		DR.printlnLineInfo("构建数据中心及带宽");
		final int DCnum = randomint(R.minDCnum, R.maxDCnum);
		DC[] dcs = new DC[DCnum];
		for (int i = 0; i < DCnum; i++) {
			dcs[i] = new DC();
			dcs[i].name = "c" + (i + 1);
			dcs[i].storage = randomdouble(R.minDCstorage, R.maxDCstorage);
			totalstorage += dcs[i].storage;
		}
		double[] bandWidthes = new double[DCnum * (DCnum - 1) / 2];
		for (int i = 0; i < bandWidthes.length; i++) {
			bandWidthes[i] = randomdouble(R.minBandWith, R.maxBandWidth);
		}
		// 构建初始数据集及任务集
		DR.printlnLineInfo("构建初始数据集及任务集");
		final int DSnum = randomint(R.miniDSnum, R.maxiDSnum);
		final int Tnum = randomint(R.minTnum, R.maxTnum);
		DS[] dss = new DS[DSnum + Tnum];
		for (int i = 0; i < DSnum + Tnum; i++) {
			dss[i] = new DS();
			dss[i].name = "d" + (i + 1);
			if (i < DSnum) {
				dss[i].gt = 0;
			} else {
				dss[i].gt = 1;
			}
		}
		for (int i = 0; i < DSnum + Tnum; i++) {
			dss[i].size = randomdouble(R.minDsSize, R.maxDsSize);
			// if (i < DSnum)
			dss[i].copyno = R.maxCopyno > 0 ? randomint(R.minCopyno,
					R.maxCopyno) : randomint(1, 5);
			totalsize += dss[i].size * dss[i].copyno;
			if (totalsize > totalstorage * R.lamda) {
				totalsize = 0;
				i = -1;
			}
		}
		T[] ts = new T[Tnum];
		for (int i = 0; i < Tnum; i++) {
			ts[i] = new T();
			ts[i].name = "t" + (i + 1);
			dss[i + DSnum].createtask = ts[i].name;
		}
		for (int i = 0; i < Tnum - 1; i++) {
			dss[i + DSnum].usedtasks.add(ts[i + 1].name);
			ts[i].successor = "t" + (i + 2);
		}

		for (int i = 0; i < Tnum; i++) {
			if (i == 0) {// 第一个task
				ts[i].needed = randomint(R.minIDS, R.maxIDS);
				for (int j = 0; j < ts[i].needed; j++) {// 使用originalDS
					int randd = randomint(0, DSnum - 1);
					if (!dss[randd].usedtasks.contains(ts[i].name)) {
						dss[randd].usedtasks.add(ts[i].name);
					} else {
						j--;
					}
				}
			} else {// 后面的task
				int ori_needed;
				int gen_needed;
				if (i < 3) {
					ori_needed = randomint(0, 3);
					if (i > 1)
						gen_needed = randomint(0, 1);
					else
						gen_needed = 0;
				} else {
					ori_needed = randomint(0, 1);
					gen_needed = randomint(0, 2);
				}
				ts[i].needed = ori_needed + gen_needed;
				for (int j = 0; j < ori_needed; j++) {// 使用originalDS
					int randd = randomint(0, DSnum - 1);
					if (!dss[randd].usedtasks.contains(ts[i].name)) {
						dss[randd].usedtasks.add(ts[i].name);
					} else {
						j--;
					}
				}
				for (int j = 0; j < gen_needed; j++) {// 使用generatedDS
					int randd = randomint(DSnum, DSnum + i - 1);
					if (!dss[randd].usedtasks.contains(ts[i].name)) {
						dss[randd].usedtasks.add(ts[i].name);
					} else {
						j--;
					}
				}
			}
		}

		// ==========================================================================================================================
		// 将构建好的随机测试数据写入文件
		DR.printlnLineInfo("将构建好的随机测试数据写入文件");
		// 数据中心和网络
		{
			Document cloudDocument = DocumentHelper.createDocument();
			Element rootCloudElement = cloudDocument.addElement("cloud");
			for (int i = 0; i < DCnum; i++) {
				Element datacenterElement = rootCloudElement
						.addElement("datacenter");
				datacenterElement.addElement("name").setText(dcs[i].name);
				datacenterElement.addElement("cs").setText("" + dcs[i].storage);
			}
			int pointer = 0;
			for (int i = 0; i < DCnum - 1; i++) {
				for (int j = i + 1; j < DCnum; j++) {
					Element bandwidthElement = rootCloudElement
							.addElement("bandwidth");
					bandwidthElement.addElement("dc1").setText(dcs[i].name);
					bandwidthElement.addElement("dc2").setText(dcs[j].name);
					bandwidthElement.addElement("bw").setText(
							"" + bandWidthes[pointer++]);
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter(new File(R.FOLDER
					+ R.inputFolder + R.CLOUD)), format);
			writer.write(cloudDocument);
			writer.close();
		}
		// 带有副本的数据集
		{
			Document datasetsDocument = DocumentHelper.createDocument();
			Element rootElement = datasetsDocument.addElement("datasets");
			for (int i = 0; i < DSnum + Tnum; i++) {
				for (int j = 0; j < dss[i].copyno; j++) {
					Element datasetElement = rootElement.addElement("dataset");
					datasetElement.addElement("name").setText(dss[i].name);
					datasetElement.addElement("copyno").setText("" + (j + 1));
					datasetElement.addElement("datasize").setText(
							"" + dss[i].size);
					datasetElement.addElement("gt").setText("" + dss[i].gt);
					if (dss[i].createtask != null)
						datasetElement.addElement("createtask").setText(
								dss[i].createtask);
					if (j == 0) {
						Element usedtasksElement = datasetElement
								.addElement("usedtasks");
						for (String t : dss[i].usedtasks)
							usedtasksElement.addElement("task").setText(t);
					}
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter(new File(R.FOLDER
					+ R.inputFolder + R.DATASETS)), format);
			writer.write(datasetsDocument);
			writer.close();
		}

		// 任务集
		{
			Document controlDocument = DocumentHelper.createDocument();
			Element rootElement = controlDocument.addElement("tasks");
			for (int i = 0; i < Tnum; i++) {
				Element taskElement = rootElement.addElement("task");
				taskElement.addElement("name").setText(ts[i].name);
				if (ts[i].successor != null)
					taskElement.addElement("nexttasks").addElement("nexttask")
							.setText(ts[i].successor);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter(new File(R.FOLDER
					+ R.inputFolder + R.CONTROL)), format);
			writer.write(controlDocument);
			writer.close();
		}
	}

	public static void createData() throws IOException {
		double totalstorage = 0;
		double totalsize = 0;

		// 构建数据中心及带宽
		DR.printlnLineInfo("构建数据中心及带宽");
		final int DCnum = randomint(R.minDCnum, R.maxDCnum);
		DC[] dcs = new DC[DCnum];
		for (int i = 0; i < DCnum; i++) {
			dcs[i] = new DC();
			dcs[i].name = "c" + (i + 1);
			dcs[i].storage = randomdouble(R.minDCstorage, R.maxDCstorage);
			totalstorage += dcs[i].storage;
		}
		double[] bandWidthes = new double[DCnum * (DCnum - 1) / 2];
		for (int i = 0; i < bandWidthes.length; i++) {
			bandWidthes[i] = randomdouble(R.minBandWith, R.maxBandWidth);
		}
		// 构建初始数据集及任务集
		DR.printlnLineInfo("构建初始数据集及任务集");
		final int DSnum = randomint(R.miniDSnum, R.maxiDSnum);
		final int Tnum = randomint(R.minTnum, R.maxTnum);
		DS[] dss = new DS[DSnum + Tnum];
		for (int i = 0; i < DSnum + Tnum; i++) {
			dss[i] = new DS();
			dss[i].name = "d" + (i + 1);
			if (i < DSnum) {
				dss[i].gt = 0;
			} else {
				dss[i].gt = 1;
			}
		}
		for (int i = 0; i < DSnum + Tnum; i++) {
			dss[i].size = randomdouble(R.minDsSize, R.maxDsSize);
			// if (i < DSnum)
			dss[i].copyno = R.maxCopyno > 0 ? randomint(R.minCopyno,
					R.maxCopyno) : randomint(1, 5);
			totalsize += dss[i].size * dss[i].copyno;
			if (totalsize > totalstorage * R.lamda) {
				totalsize = 0;
				i = -1;
			}
		}
		T[] ts = new T[Tnum];
		for (int i = 0; i < Tnum; i++) {
			ts[i] = new T();
			ts[i].name = "t" + (i + 1);
			dss[i + DSnum].createtask = ts[i].name;
		}
		for (int i = 0; i < Tnum - 1; i++) {
			dss[i + DSnum].usedtasks.add(ts[i + 1].name);
			ts[i].successor = "t" + (i + 2);
		}

		for (int i = 0; i < Tnum; i++) {
			ts[i].needed = randomint(R.minIDS, R.maxIDS);
			for (int j = 0; j < ts[i].needed; j++) {
				double random_Deci = randomdouble(0, 1);
				if (random_Deci < R.getOrigInputDS_ratio() || i == 0) {// 使用originalDS
					int randd = randomint(0, DSnum - 1);
					if (!dss[randd].usedtasks.contains(ts[i].name)) {
						dss[randd].usedtasks.add(ts[i].name);
					} else {
						j--;
					}
				} else {// 使用generatedDS
					int randd = randomint(DSnum, DSnum + i - 1);
					if (!dss[randd].usedtasks.contains(ts[i].name)) {
						dss[randd].usedtasks.add(ts[i].name);
					} else {
						j--;
					}
				}

			}
		}
		// ==========================================================================================================================
		// 将构建好的随机测试数据写入文件
		DR.printlnLineInfo("将构建好的随机测试数据写入文件");
		// 数据中心和网络
		{
			Document cloudDocument = DocumentHelper.createDocument();
			Element rootCloudElement = cloudDocument.addElement("cloud");
			for (int i = 0; i < DCnum; i++) {
				Element datacenterElement = rootCloudElement
						.addElement("datacenter");
				datacenterElement.addElement("name").setText(dcs[i].name);
				datacenterElement.addElement("cs").setText("" + dcs[i].storage);
			}
			int pointer = 0;
			for (int i = 0; i < DCnum - 1; i++) {
				for (int j = i + 1; j < DCnum; j++) {
					Element bandwidthElement = rootCloudElement
							.addElement("bandwidth");
					bandwidthElement.addElement("dc1").setText(dcs[i].name);
					bandwidthElement.addElement("dc2").setText(dcs[j].name);
					bandwidthElement.addElement("bw").setText(
							"" + bandWidthes[pointer++]);
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter(new File(R.FOLDER
					+ R.inputFolder + R.CLOUD)), format);
			writer.write(cloudDocument);
			writer.close();
		}
		// 带有副本的数据集
		{
			Document datasetsDocument = DocumentHelper.createDocument();
			Element rootElement = datasetsDocument.addElement("datasets");
			for (int i = 0; i < DSnum + Tnum; i++) {
				for (int j = 0; j < dss[i].copyno; j++) {
					Element datasetElement = rootElement.addElement("dataset");
					datasetElement.addElement("name").setText(dss[i].name);
					datasetElement.addElement("copyno").setText("" + (j + 1));
					datasetElement.addElement("datasize").setText(
							"" + dss[i].size);
					datasetElement.addElement("gt").setText("" + dss[i].gt);
					if (dss[i].createtask != null)
						datasetElement.addElement("createtask").setText(
								dss[i].createtask);
					if (j == 0) {
						Element usedtasksElement = datasetElement
								.addElement("usedtasks");
						for (String t : dss[i].usedtasks)
							usedtasksElement.addElement("task").setText(t);
					}
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter(new File(R.FOLDER
					+ R.inputFolder + R.DATASETS)), format);
			writer.write(datasetsDocument);
			writer.close();
		}

		// 任务集
		{
			Document controlDocument = DocumentHelper.createDocument();
			Element rootElement = controlDocument.addElement("tasks");
			for (int i = 0; i < Tnum; i++) {
				Element taskElement = rootElement.addElement("task");
				taskElement.addElement("name").setText(ts[i].name);
				if (ts[i].successor != null)
					taskElement.addElement("nexttasks").addElement("nexttask")
							.setText(ts[i].successor);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(new FileWriter(new File(R.FOLDER
					+ R.inputFolder + R.CONTROL)), format);
			writer.write(controlDocument);
			writer.close();
		}
	}

	// 记录配置信息
	public static void writeArgs() {
		File file = new File(R.FOLDER + R.inputFolder + "args.txt");
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					file));
			R r = new R();
			Field[] field = r.getClass().getDeclaredFields();
			for (int j = 0; j < field.length; j++) {
				try {
					String name = field[j].getName();
					String type = field[j].getGenericType().toString();

					Method m = r.getClass().getMethod(
							"get" + name.substring(0, 1).toUpperCase()
									+ name.substring(1));
					Object value = m.invoke(r);
					System.out.println(name + ":" + value);
					bufferedWriter.write(name + ":" + value);
					bufferedWriter.newLine();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int randomint(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	public static double randomdouble(double min, double max) {
		return min + random.nextDouble() * (max - min);
	}

	private static class DC {
		String name;
		double storage;
	}

	private static class T {
		String name;
		String successor;
		int needed;
	}

	private static class DS {
		String name;
		int copyno;
		ArrayList<String> usedtasks = new ArrayList<String>();
		String createtask;
		double size;
		int gt;
	}
}