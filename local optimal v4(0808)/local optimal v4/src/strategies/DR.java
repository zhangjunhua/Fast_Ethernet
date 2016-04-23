package strategies;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import newClass.Cloud;
import newClass.Cloud.DataCenter;
import newClass.DataSets;
import newClass.DataSets.DataSet;
import newClass.Tasks;
import newClass.Tasks.Task;

/**
 * @author Admin
 *
 */
public class DR {
	static DataSets dataSets = DataSets.getInstanceofDataSets();
	static Tasks tasks = Tasks.getInstanceofTasks();
	static Cloud cloud = Cloud.getInstanceofCloud();
	static HashMap<String, Double> sumR_HashMap = new HashMap<String, Double>();
	public static boolean exit = false;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new Thread(new exit()).start();
		while (!exit) {
			test2015_8_24();
		}
	}
	
	/**
	 * 2014_11_15
	 * 
	 * 在计算hadoop策略时，去掉Sc（任务调度）编码，在任务执行时再确认用哪一个副本
	 * 
	 * 而遗传算法策略依然用Sc编码，在任务执行前就确定调度哪个策略
	 * 
	 * --------------
	 * 
	 * 2015_8_19
	 * 
	 * 这个实验室基于2014年11月25日Hadoop vs 遗传 在不同数据中心的对比来写的。
	 * 
	 * 这个实验和以前最主要的区别在于数据集的数目会比较小，任务数目可能会比较多？（这个通过多次实验待定）。而其他的不会变
	 * 
	 * 参数设置：dataset number：10， task number：20，data center number：5：2：13
	 * 
	 * -----------------------
	 * 
	 * 2015_8_24
	 * 
	 * 这个实验又加了条件，every step用到的original数据集减小,第一个用到的数据比较多，
	 * 
	 * 后面的every step用到的数据主要为0~1个original数据集，1~3个generated数据集
	 * 
	 * @throws IOException
	 */
	public static void test2015_8_24() throws IOException {
		/**
		 * hadoop 和 遗传算法 的比较。 数据中心数不同，其它相同。
		 */
		{
			R.maxiDSnum = R.miniDSnum = 10;
			R.maxCopyno = R.minCopyno = 1;// 这个参数在构建数据的时候再修改，初始值为1
			int copyno = 3;
			R.maxTnum = R.minTnum = 20;

			for (int i = 0; i < 10; i++) {// 测试个数为10的整数倍
				for (int dc = 5; dc <= 13; dc = dc + 2) {

					R.minDCnum = R.maxDCnum = dc;

					readandwrite.readConfiguration();
					CreateRandomData.newfolderandrstconf();
					CreateRandomData.createData_2015_8_22();
					CreateRandomData.writeArgs();

					dataSets = DataSets.getNewInstanceofDataSets();
					tasks = Tasks.getNewInstanceofTasks();
					cloud = Cloud.getNewInstanceofCloud();

					System.err.println("ReadData");
					readandwrite.readDatas(copyno);
					System.err.println("ReadData finished");

					// 初始化Strategy
					Strategy.initialize();

					System.err.println("The Heredity Begin!");
					ArrayList<Strategy.S> CH = Strategy.Heredity();
					readandwrite.OutputTheResult(CH, dc);
					System.err.println("The Heredity End!");

					Strategy.S s = Strategy.S.getRandomS();
					Strategy.TimeAndTransAndMoveCosttotal_withoutSc(s);

					readandwrite.OutputOneSolution(s, "rand" + dc);
				}
			}
		}
	}

	/**
	 * 基于2014_11_20的
	 * 
	 * 时间2015年8月23日，
	 * 
	 * 张俊华于凌晨
	 * 
	 * 这次实验主要是解决every step need 1~3 original
	 * datasets可能会对实验造成影响的问题。（reviewer提出来的）
	 * 
	 * 这次实验的方法就是在every step的需要较少original datasets的情况下，对不同size的科学工作流genetic 和
	 * Hadoop算法之间的对比
	 * 
	 * 实验具体参数设置如下：
	 * 
	 * datacenter：9
	 * 
	 * original datasets： [5,8,10,13,15]
	 * 
	 * task number：【10:5:30】
	 * 
	 * 第一个task 需要1~3个 original 数据集，后面的task 每个需要0~1个original数据集。
	 * 
	 * 后面的每个task需要1~3个generated数据集。
	 * 
	 * 这个不是sequence的。
	 * 
	 * @throws IOException
	 * 
	 */
	public static void test2015_8_22() throws IOException {
		/*
		 * 数据集：[5,8,10,13,15] 任务数:【10:5:30】 数据副本数：3 数据中心数：9
		 */
		R.minDCnum = R.maxDCnum = 9;
		R.maxCopyno = R.minCopyno = 1;
		int copyno = 3;
		for (int i = 0; i < 10; i++) {
			for (int Tn = 10; Tn <= 30; Tn += 5) {
				// 初始化数据
				{
					R.maxTnum = R.minTnum = Tn;
					if (Tn % 2 == 0) {
						R.miniDSnum = R.maxiDSnum = Tn / 2;
					} else {
						R.miniDSnum = Tn / 2;
						R.maxiDSnum = R.miniDSnum + 1;
					}
				}

				readandwrite.readConfiguration();
				CreateRandomData.newfolderandrstconf();
				CreateRandomData.createData_2015_8_22();
				CreateRandomData.writeArgs();

				dataSets = DataSets.getNewInstanceofDataSets();
				tasks = Tasks.getNewInstanceofTasks();
				cloud = Cloud.getNewInstanceofCloud();

				System.err.println("ReadData");
				readandwrite.readDatas(copyno);
				System.err.println("ReadData finished");

				// 初始化Strategy
				Strategy.initialize();

				System.err.println("The Heredity Begin!");
				ArrayList<Strategy.S> CH = Strategy.Heredity();
				readandwrite.OutputTheResult(CH, Tn);
				System.err.println("The Heredity End!");

				Strategy.S s = Strategy.S.getRandomS();
				Strategy.TimeAndTransAndMoveCosttotal_withoutSc(s);

				readandwrite.OutputOneSolution(s, "rand" + Tn);
			}
		}
	}

	/**
	 * 2015年8月13日，张俊华 1.这次实验主要是8月data replica 论文的二审后，有一个minor
	 * revision，提到输入数据集太多， 而我们之前的实验都是数据集的个数是任务数目的2倍，这个感觉跟实际的科学应用不太一样。
	 * 2.另一方面，reviewer1就讲到关于每一步都使用input dataset的这种情况。every step a task needs
	 * randomly one to three "original" data replica。但是实际的工作流并不是这样的，实际的工作流
	 * 主要的original input dataset 是在一开始就被用到的。因此有问题
	 * 
	 * 这次实验主要用来说明：1、不管我们的数据集数量的多少，都ok，是一个general的算法，并不和实际的数据
	 * 想关联；2、不管是什么时候输入的数据，都ok，仍然是一个general的算法，
	 * 
	 * 下面来说说主要的实验思路。 实验的思路主要是要构造多种不同的科学应用（工作流）。 有不同的任务，输入数据集数的比例。
	 * 有不同的比例的输入数据，输出数据被用到。
	 * 
	 * 
	 * 
	 * 
	 * 要点： 1、为了尽早观察到实验结果，每次运行完一个时就将结果输出出来，监控实验进展。以便及时修改实验
	 * 
	 * @throws IOException
	 * 
	 */
	public static void test2015_8_13() throws IOException {

		/*
		 * 2015年8月14日，实验A,数据集与任务数不是按照2：1， 而是多种情况 ds：(5~30:5)× task(5~30:5)
		 * 
		 * 副本：3
		 */
		testA_2015_8_14: {
			// int copyno = 3;
			// for (int ds_num = 5; ds_num <= 30; ds_num += 5) {
			// for (int task_num = 5; task_num <= 30; task_num += 5) {
			// // 初始化初始化DSnum和Tnum
			// R.maxiDSnum = R.miniDSnum = ds_num;
			// R.maxTnum = R.minTnum = task_num;
			// R.maxCopyno = R.minCopyno = 1;
			// R.minDCnum = R.maxDCnum = 9;
			//
			// readandwrite.readConfiguration();
			// CreateRandomData.newfolderandrstconf();
			// CreateRandomData.createData();
			// CreateRandomData.writeArgs();
			//
			// dataSets = DataSets.getNewInstanceofDataSets();
			// tasks = Tasks.getNewInstanceofTasks();
			// cloud = Cloud.getNewInstanceofCloud();
			//
			// System.err.println("ReadData");
			// readandwrite.readDatas(copyno);
			// System.err.println("ReadData finished");
			//
			// // 初始化Strategy
			// Strategy.initialize();
			//
			// System.err.println("The Heredity Begin!");
			// ArrayList<Strategy.S> CH = Strategy.Heredity();
			// readandwrite.record_2015_8_14(CH, "every_results",
			// "sum_result", ds_num, task_num, sumR_HashMap);
			//
			// System.err.println("The Heredity End!");
			// }
			// }
		}

		/**
		 * 2015年8月17日凌晨
		 * 
		 * 这个实验主要面对不同比例的,20,40,60,80
		 * 
		 * 数据中心数目9，tasknum：15，datasetnum：15
		 */
		testA_2015_8_16: {
			int copyno = 3;
			// 初始化初始化DSnum和Tnum
			R.maxiDSnum = R.miniDSnum = 12;
			R.maxTnum = R.minTnum = 12;
			R.maxCopyno = R.minCopyno = 1;
			R.minDCnum = R.maxDCnum = 9;
			for (double ratio = 0.2; ratio <= 0.8; ratio += 0.2) {
				R.setOrigInputDS_ratio(ratio);

				readandwrite.readConfiguration();
				CreateRandomData.newfolderandrstconf();
				CreateRandomData.createData();
				CreateRandomData.writeArgs();

				dataSets = DataSets.getNewInstanceofDataSets();
				tasks = Tasks.getNewInstanceofTasks();
				cloud = Cloud.getNewInstanceofCloud();

				System.err.println("ReadData");
				readandwrite.readDatas(copyno);
				System.err.println("ReadData finished");

				// 初始化Strategy
				Strategy.initialize();

				System.err.println("The Heredity Begin!");
				ArrayList<Strategy.S> CH = Strategy.Heredity();
				readandwrite.record_2015_8_17(CH, "every_results",
						"sum_result", ratio, sumR_HashMap);

				System.err.println("The Heredity End!");
			}
		}
	}

	/**
	 * 遗传算法策略本身
	 * 
	 * @throws IOException
	 */
	public static void test2014_11_25() throws IOException {
		{
			int copyno = 3;
			for (int i = 0; i < 10; i++) {
				for (int dsn = 10; dsn <= 30; dsn += 5) {// dsn指的是DS数目
					{// 初始化DSnum和Tnum
						R.maxiDSnum = R.miniDSnum = dsn;
						if (dsn % 2 == 0) {
							R.minTnum = R.maxTnum = dsn / 2;
						} else {
							R.minTnum = dsn / 2;
							R.maxTnum = R.minTnum + 1;
						}
					}
					for (int dc = 5; dc <= 13; dc = dc + 2) {
						R.maxCopyno = R.minCopyno = 1;
						R.minDCnum = R.maxDCnum = dc;

						readandwrite.readConfiguration();
						CreateRandomData.newfolderandrstconf();
						CreateRandomData.createData();
						CreateRandomData.writeArgs();

						dataSets = DataSets.getNewInstanceofDataSets();
						tasks = Tasks.getNewInstanceofTasks();
						cloud = Cloud.getNewInstanceofCloud();

						System.err.println("ReadData");
						readandwrite.readDatas(copyno);
						System.err.println("ReadData finished");

						// 初始化Strategy
						Strategy.initialize();

						System.err.println("The Heredity Begin!");
						ArrayList<Strategy.S> CH = Strategy.Heredity();
						readandwrite.OutputTheResult(CH, dsn);
						System.err.println("The Heredity End!");
					}
				}
			}
		}
	}

	/**
	 * hadoop 和 遗传算法 的比较 科学工作流规模不同，其它相同
	 * 
	 * @throws IOException
	 */
	public static void test2014_11_20() throws IOException {
		{
			/*
			 * 数据集：【10:5:30】任务数:[5,8,10,13,15] 数据副本数：3 数据中心数：9
			 */
			R.minDCnum = R.maxDCnum = 9;
			R.maxCopyno = R.minCopyno = 1;
			int copyno = 3;
			for (int i = 0; i < 10; i++) {
				for (int dsn = 10; dsn <= 30; dsn += 5) {
					// 初始化数据
					{
						R.maxiDSnum = R.miniDSnum = dsn;
						if (dsn % 2 == 0) {
							R.minTnum = R.maxTnum = dsn / 2;
						} else {
							R.minTnum = dsn / 2;
							R.maxTnum = R.minTnum + 1;
						}
					}

					readandwrite.readConfiguration();
					CreateRandomData.newfolderandrstconf();
					CreateRandomData.createData();
					CreateRandomData.writeArgs();

					dataSets = DataSets.getNewInstanceofDataSets();
					tasks = Tasks.getNewInstanceofTasks();
					cloud = Cloud.getNewInstanceofCloud();

					System.err.println("ReadData");
					readandwrite.readDatas(copyno);
					System.err.println("ReadData finished");

					// 初始化Strategy
					Strategy.initialize();

					System.err.println("The Heredity Begin!");
					ArrayList<Strategy.S> CH = Strategy.Heredity();
					readandwrite.OutputTheResult(CH, dsn);
					System.err.println("The Heredity End!");

					Strategy.S s = Strategy.S.getRandomS();
					Strategy.TimeAndTransAndMoveCosttotal_withoutSc(s);

					readandwrite.OutputOneSolution(s, "rand" + dsn);
				}
			}
		}
	}

	/**
	 * 在计算hadoop策略时，去掉Sc（任务调度）编码，在任务执行时再确认用哪一个副本
	 * 
	 * 而遗传算法策略依然用Sc编码，在任务执行前就确定调度哪个策略
	 * 
	 * @throws IOException
	 */
	public static void test2014_11_15() throws IOException {
		/**
		 * hadoop 和 遗传算法 的比较。 数据中心数不同，其它相同。
		 */
		{
			/*
			 * 数据集数：20 数据集大小：17.6~26.4 数据副本数：3 任务数：10
			 */
			R.maxiDSnum = R.miniDSnum = 20;
			R.maxCopyno = R.minCopyno = 1;// 这个参数在构建数据的时候再修改，初始值为1
			int copyno = 3;
			R.maxTnum = R.minTnum = 10;

			for (int i = 0; i < 10; i++) {// 测试个数为10的整数倍
				for (int dc = 5; dc <= 13; dc = dc + 2) {

					R.minDCnum = R.maxDCnum = dc;

					readandwrite.readConfiguration();
					CreateRandomData.newfolderandrstconf();
					CreateRandomData.createData();
					CreateRandomData.writeArgs();

					dataSets = DataSets.getNewInstanceofDataSets();
					tasks = Tasks.getNewInstanceofTasks();
					cloud = Cloud.getNewInstanceofCloud();

					System.err.println("ReadData");
					readandwrite.readDatas(copyno);
					System.err.println("ReadData finished");

					// 初始化Strategy
					Strategy.initialize();

					System.err.println("The Heredity Begin!");
					ArrayList<Strategy.S> CH = Strategy.Heredity();
					readandwrite.OutputTheResult(CH, dc);
					System.err.println("The Heredity End!");

					Strategy.S s = Strategy.S.getRandomS();
					Strategy.TimeAndTransAndMoveCosttotal_withoutSc(s);

					readandwrite.OutputOneSolution(s, "rand" + dc);
				}
			}
		}
	}

	/**
	 * 随机布局和遗传算法布局在不同副本下的对比实验 找出hadoop在高副本时数据传输代价依然很高的原因
	 * 
	 * @throws IOException
	 */
	public static void test2014_11_9() throws IOException {
		for (int testnum = 1; testnum <= 10; testnum++) {
			readandwrite.readConfiguration();
			CreateRandomData.newfolderandrstconf();
			CreateRandomData.createData();
			CreateRandomData.writeArgs();
			for (int copyno = 1; copyno <= 4; copyno++) {
				dataSets = DataSets.getNewInstanceofDataSets();
				tasks = Tasks.getNewInstanceofTasks();
				cloud = Cloud.getNewInstanceofCloud();

				System.err.println("ReadData");
				readandwrite.readDatas(copyno);
				System.err.println("ReadData finished");

				// 初始化Strategy
				Strategy.initialize();

				System.err.println("The Heredity Begin!");
				ArrayList<Strategy.S> CH = Strategy.Heredity();
				readandwrite.OutputTheResult(CH, copyno);
				System.err.println("The Heredity End!");

				Strategy.S s = Strategy.S.getRandomS();
				Strategy.TimeAndTransAndMoveCosttotal(s);

				readandwrite.OutputOneSolution(s, "rand" + copyno);
			}
		}
	}

	public static class readandwrite {
		static Scanner scanner;

		public static void readConfiguration() {
			FileReader fReader = null;
			try {
				fReader = new FileReader(R.FOLDER + R.CONFIGURATION);
				scanner = new Scanner(fReader);
				R.configerationhMap = new HashMap<String, String>();
				while (scanner.hasNext()) {
					R.configerationhMap.put(scanner.next(), scanner.next());
				}
				R.inputFolder = R.configerationhMap.get("inputdatafolder")
						+ "/";
				R.outputFolder = R.configerationhMap.get("outputdatafolder")
						+ "/";
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					fReader.close();
					scanner.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * 
		 * 2015_8_14 将运行结果记录在一个文件里面，并进行统计
		 * 
		 * @param Ss
		 * @param every_result_OPfile_name
		 * @param analyze_result_OPfile_nameString
		 * @throws IOException
		 */
		public static void record_2015_8_14(ArrayList<Strategy.S> Ss,
				String every_result_OPfile_name,
				String analyze_result_OPfile_nameString, int ds_num, int tnum,
				HashMap<String, Double> sumRHashMap) throws IOException {
			// 最佳策略
			Strategy.S S = null;
			{
				double timecost = Double.MAX_VALUE;
				for (strategies.DR.Strategy.S s : Ss) {
					if (s.getTimecost() < timecost) {
						timecost = s.getTimecost();
						S = s;
					}
				}
			}
			{// 写every_result文件
				int movetimes = S.getMovetimes();
				double transcost = S.getTranscost();
				double timecost = S.getTimecost();
				// OutputOneSolution(S, "result" + param);
				File every_result_OPfile = new File(R.FOLDER
						+ every_result_OPfile_name + ".txt");
				FileWriter every_result_OPfileFileWriter = new FileWriter(
						every_result_OPfile, true);

				BufferedWriter bufferedWriter = new BufferedWriter(
						every_result_OPfileFileWriter);
				bufferedWriter.write(R.configerationhMap
						.get("outputdatafolder"));
				bufferedWriter.newLine();
				bufferedWriter.write("ds_num:" + ds_num);
				bufferedWriter.write("tnum" + tnum);
				bufferedWriter.write("timecost = ");
				bufferedWriter.write("" + timecost);
				bufferedWriter.newLine();

				bufferedWriter.write("movetimes = ");
				bufferedWriter.write("" + movetimes);
				bufferedWriter.newLine();

				bufferedWriter.write("transcost = ");
				bufferedWriter.write("" + transcost);
				bufferedWriter.newLine();
				bufferedWriter.newLine();
				bufferedWriter.flush();
				bufferedWriter.close();
				every_result_OPfileFileWriter.close();
			}
			{// 对hashmap进行变动
				if (sumRHashMap.get(ds_num + " " + tnum + "MoveTimes") == null) {
					sumRHashMap.put(ds_num + " " + tnum + "MoveTimes", 0.0);
				}
				if (sumRHashMap.get(ds_num + " " + tnum + "TransCost") == null) {
					sumRHashMap.put(ds_num + " " + tnum + "TransCost", 0.0);
				}
				if (sumRHashMap.get(ds_num + " " + tnum + "TimeCost") == null) {
					sumRHashMap.put(ds_num + " " + tnum + "TimeCost", 0.0);
				}
				sumRHashMap.put(
						ds_num + " " + tnum + "MoveTimes",
						sumRHashMap.get(ds_num + " " + tnum + "MoveTimes")
								+ S.getMovetimes());
				sumRHashMap.put(
						ds_num + " " + tnum + "TransCost",
						sumRHashMap.get(ds_num + " " + tnum + "TransCost")
								+ S.getTranscost());
				sumRHashMap.put(
						ds_num + " " + tnum + "TimeCost",
						sumRHashMap.get(ds_num + " " + tnum + "TimeCost")
								+ S.getTimecost());
			}
			{// 写sumresult文件

				// OutputOneSolution(S, "result" + param);
				File every_result_OPfile = new File(R.FOLDER
						+ analyze_result_OPfile_nameString + ".txt");
				FileWriter every_result_OPfileFileWriter = new FileWriter(
						every_result_OPfile);

				BufferedWriter bufferedWriter = new BufferedWriter(
						every_result_OPfileFileWriter);

				bufferedWriter.write("movetimes：\n");
				for (int i = 5; i <= 30; i += 5) {
					for (int j = 5; j <= 30; j += 5) {
						bufferedWriter.write(sumRHashMap.get(i + " " + j
								+ "MoveTimes")
								+ "\t");
					}
					bufferedWriter.newLine();
				}
				bufferedWriter.newLine();

				bufferedWriter.write("Transcost：\n");
				for (int i = 5; i <= 30; i += 5) {
					for (int j = 5; j <= 30; j += 5) {
						bufferedWriter.write(sumRHashMap.get(i + " " + j
								+ "TransCost")
								+ "\t");
					}
					bufferedWriter.newLine();
				}
				bufferedWriter.newLine();

				bufferedWriter.write("TimeCost：\n");
				for (int i = 5; i <= 30; i += 5) {
					for (int j = 5; j <= 30; j += 5) {
						bufferedWriter.write(sumRHashMap.get(i + " " + j
								+ "TimeCost")
								+ "\t");
					}
					bufferedWriter.newLine();
				}
				bufferedWriter.newLine();

				bufferedWriter.flush();
				bufferedWriter.close();
				every_result_OPfileFileWriter.close();
			}

		}

		/**
		 * 
		 * 2015_8_17凌晨 将运行结果记录在一个文件里面，并进行统计
		 * 
		 * @param Ss
		 * @param every_result_OPfile_name
		 * @param analyze_result_OPfile_nameString
		 * @throws IOException
		 */
		public static void record_2015_8_17(ArrayList<Strategy.S> Ss,
				String every_result_OPfile_name,
				String analyze_result_OPfile_nameString, double ratio,
				HashMap<String, Double> sumRHashMap) throws IOException {
			// 最佳策略
			Strategy.S S = null;
			{
				double timecost = Double.MAX_VALUE;
				for (strategies.DR.Strategy.S s : Ss) {
					if (s.getTimecost() < timecost) {
						timecost = s.getTimecost();
						S = s;
					}
				}
			}
			{// 写every_result文件
				int movetimes = S.getMovetimes();
				double transcost = S.getTranscost();
				double timecost = S.getTimecost();
				// OutputOneSolution(S, "result" + param);
				File every_result_OPfile = new File(R.FOLDER
						+ every_result_OPfile_name + ".txt");
				FileWriter every_result_OPfileFileWriter = new FileWriter(
						every_result_OPfile, true);

				BufferedWriter bufferedWriter = new BufferedWriter(
						every_result_OPfileFileWriter);
				bufferedWriter.write(R.configerationhMap
						.get("outputdatafolder"));
				bufferedWriter.newLine();
				bufferedWriter.write("ratio:" + ratio);
				bufferedWriter.write("timecost = ");
				bufferedWriter.write("" + timecost);
				bufferedWriter.newLine();

				bufferedWriter.write("movetimes = ");
				bufferedWriter.write("" + movetimes);
				bufferedWriter.newLine();

				bufferedWriter.write("transcost = ");
				bufferedWriter.write("" + transcost);
				bufferedWriter.newLine();
				bufferedWriter.newLine();
				bufferedWriter.flush();
				bufferedWriter.close();
				every_result_OPfileFileWriter.close();
			}
			{// 对hashmap进行变动
				if (sumRHashMap.get(ratio + " " + "MoveTimes") == null) {
					sumRHashMap.put(ratio + " " + "MoveTimes", 0.0);
				}
				if (sumRHashMap.get(ratio + " " + "TransCost") == null) {
					sumRHashMap.put(ratio + " " + "TransCost", 0.0);
				}
				if (sumRHashMap.get(ratio + " " + "TimeCost") == null) {
					sumRHashMap.put(ratio + " " + "TimeCost", 0.0);
				}
				sumRHashMap.put(
						ratio + " " + "MoveTimes",
						sumRHashMap.get(ratio + " " + "MoveTimes")
								+ S.getMovetimes());
				sumRHashMap.put(
						ratio + " " + "TransCost",
						sumRHashMap.get(ratio + " " + "TransCost")
								+ S.getTranscost());
				sumRHashMap.put(
						ratio + " " + "TimeCost",
						sumRHashMap.get(ratio + " " + "TimeCost")
								+ S.getTimecost());
			}
			{// 写sumresult文件

				// OutputOneSolution(S, "result" + param);
				File every_result_OPfile = new File(R.FOLDER
						+ analyze_result_OPfile_nameString + ".txt");
				FileWriter every_result_OPfileFileWriter = new FileWriter(
						every_result_OPfile);

				BufferedWriter bufferedWriter = new BufferedWriter(
						every_result_OPfileFileWriter);

				bufferedWriter.write("movetimes：\n");
				for (double ratiok = 0.2; ratiok <= 0.8; ratiok += 0.2) {
					bufferedWriter.write(sumRHashMap.get(ratiok + " "
							+ "MoveTimes")
							+ "\t");
				}
				bufferedWriter.newLine();

				bufferedWriter.write("Transcost：\n");
				for (double ratiok = 0.2; ratiok <= 0.8; ratiok += 0.2) {
					bufferedWriter.write(sumRHashMap.get(ratiok + " "
							+ "TransCost")
							+ "\t");
				}
				bufferedWriter.newLine();

				bufferedWriter.write("TimeCost：\n");
				for (double ratiok = 0.2; ratiok <= 0.8; ratiok += 0.2) {
					bufferedWriter.write(sumRHashMap.get(ratiok + " "
							+ "TimeCost")
							+ "\t");
				}
				bufferedWriter.newLine();

				bufferedWriter.flush();
				bufferedWriter.close();
				every_result_OPfileFileWriter.close();
			}

		}

		public static void readDatas() {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			NodeList datasetNodeList = null;

			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document dom;
				try {
					dom = db.parse(R.FOLDER + R.inputFolder + R.DATASETS);
					if (dom != null) {
						Element docEle = dom.getDocumentElement();
						datasetNodeList = docEle
								.getElementsByTagName("dataset");
						constructData(datasetNodeList);
					}
					dom = null;
					dom = db.parse(R.FOLDER + R.inputFolder + R.CONTROL);
					if (dom != null) {
						Element element = dom.getDocumentElement();
						datasetNodeList = element.getElementsByTagName("task");
						constructControl(datasetNodeList);
					}
					dom = null;
					dom = db.parse(R.FOLDER + R.inputFolder + R.CLOUD);
					if (dom != null) {
						Element element = dom.getDocumentElement();
						NodeList dcnodList = element
								.getElementsByTagName("datacenter");
						constructCloud(dcnodList);
						NodeList bwnoNodeList = element
								.getElementsByTagName("bandwidth");
						constructBandWidth(bwnoNodeList);

					}
				} catch (FileNotFoundException ex) {
					System.out.println("DDG xml file is not found.");
				}
			} catch (ParserConfigurationException pce) {
				System.out.println("Error while parsing the xml.");
			} catch (SAXException se) {
				System.out.println("Error while parsing the xml.");
			} catch (IOException ioe) {
				System.out.println("Exception while reading the xml.");
			}
		}

		/**
		 * 用来实现第一部分第一个实验的，构造不同的数据副本
		 * 
		 * @param copyno
		 */
		public static void readDatas(int copyno) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			NodeList datasetNodeList = null;

			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document dom;
				try {
					dom = db.parse(R.FOLDER + R.inputFolder + R.DATASETS);
					if (dom != null) {
						Element docEle = dom.getDocumentElement();
						datasetNodeList = docEle
								.getElementsByTagName("dataset");
						constructData(datasetNodeList, copyno);
					}
					dom = null;
					dom = db.parse(R.FOLDER + R.inputFolder + R.CONTROL);
					if (dom != null) {
						Element element = dom.getDocumentElement();
						datasetNodeList = element.getElementsByTagName("task");
						constructControl(datasetNodeList);
					}
					dom = null;
					dom = db.parse(R.FOLDER + R.inputFolder + R.CLOUD);
					if (dom != null) {
						Element element = dom.getDocumentElement();
						NodeList dcnodList = element
								.getElementsByTagName("datacenter");
						constructCloud(dcnodList);
						NodeList bwnoNodeList = element
								.getElementsByTagName("bandwidth");
						constructBandWidth(bwnoNodeList);

					}
				} catch (FileNotFoundException ex) {
					System.out.println("DDG xml file is not found.");
				}
			} catch (ParserConfigurationException pce) {
				System.out.println("Error while parsing the xml.");
			} catch (SAXException se) {
				System.out.println("Error while parsing the xml.");
			} catch (IOException ioe) {
				System.out.println("Exception while reading the xml.");
			}
		}

		public static void readDatas2() {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			NodeList datasetNodeList = null;

			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document dom;
				try {
					dom = db.parse(R.FOLDER + R.inputFolder + R.NDATASETS);
					if (dom != null) {
						Element docEle = dom.getDocumentElement();
						datasetNodeList = docEle
								.getElementsByTagName("dataset");
						constructData(datasetNodeList);
					}
					dom = null;
					dom = db.parse(R.FOLDER + R.inputFolder + R.CONTROL);
					if (dom != null) {
						Element element = dom.getDocumentElement();
						datasetNodeList = element.getElementsByTagName("task");
						constructControl(datasetNodeList);
					}
					dom = null;
					dom = db.parse(R.FOLDER + R.inputFolder + R.CLOUD);
					if (dom != null) {
						Element element = dom.getDocumentElement();
						NodeList dcnodList = element
								.getElementsByTagName("datacenter");
						constructCloud(dcnodList);
						NodeList bwnoNodeList = element
								.getElementsByTagName("bandwidth");
						constructBandWidth(bwnoNodeList);

					}
				} catch (FileNotFoundException ex) {
					System.out.println("DDG xml file is not found.");
				}
			} catch (ParserConfigurationException pce) {
				System.out.println("Error while parsing the xml.");
			} catch (SAXException se) {
				System.out.println("Error while parsing the xml.");
			} catch (IOException ioe) {
				System.out.println("Exception while reading the xml.");
			}
		}

		private static void constructData(NodeList dataNodeList) {
			for (int i = 0; i < dataNodeList.getLength(); i++) {
				Element element = (Element) dataNodeList.item(i);
				String dataname = getValueOfTag("name", element);
				String copyno = getValueOfTag("copyno", element);
				DataSet dataSet = dataSets.getDataset(dataname,
						Integer.parseInt(copyno));

				String datasize = getValueOfTag("datasize", element);// ===============================================================================================
				// String datasize = "0";

				dataSet.setDatasize(Double.parseDouble(datasize));
				String gt = getValueOfTag("gt", element);
				dataSet.setGt(Integer.parseInt(gt));
				dataSet.setUsedtasks(getUsedtasks(element));
				dataSet.setCreatetask(tasks.getTask(getValueOfTag("createtask",
						element)));
			}
		}

		/**
		 * 根据指定的副本数，构造数据集（所读的数据集的副本数应该都为1
		 * 
		 * @param dataNodeList
		 * @param copyno
		 */
		private static void constructData(NodeList dataNodeList, int copyno) {
			for (int i = 0; i < dataNodeList.getLength(); i++) {
				Element element = (Element) dataNodeList.item(i);
				String dataname = getValueOfTag("name", element);
				String datasize = getValueOfTag("datasize", element);
				String gt = getValueOfTag("gt", element);

				for (int j = 1; j <= copyno; j++) {
					DataSet dataSet = dataSets.getDataset(dataname, j);
					dataSet.setDatasize(Double.parseDouble(datasize));
					dataSet.setGt(Integer.parseInt(gt));
					if (j == 1) {
						dataSet.setUsedtasks(getUsedtasks(element));
						dataSet.setCreatetask(tasks.getTask(getValueOfTag(
								"createtask", element)));
					}
				}
			}
		}

		private static void constructControl(NodeList nodeList) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				String taskname = getValueOfTag("name", element);
				Task task = tasks.getTask(taskname);
				task.setSuccessors(getNextTasks(element));
			}
		}

		private static void constructCloud(NodeList nodeList) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				String name = getValueOfTag("name", element);
				String cs = getValueOfTag("cs", element);
				DataCenter dataCenter = cloud.getDataCenter(name);
				dataCenter.setCs(Double.parseDouble(cs));
			}
		}

		private static void constructBandWidth(NodeList nodeList) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				String dc1 = getValueOfTag("dc1", element);
				String dc2 = getValueOfTag("dc2", element);
				String bw = getValueOfTag("bw", element);
				cloud.addBandWidth(dc1, dc2, Double.parseDouble(bw));
			}
		}

		/**
		 * Reads the value of the tag with the given tag name and returns it
		 * 
		 * @param tagName
		 * @param datasetElement
		 * @return
		 */
		private static String getValueOfTag(String tagName,
				Element datasetElement) {
			NodeList aNodeList = datasetElement.getElementsByTagName(tagName);
			Element aElement = (Element) aNodeList.item(0);
			if (aElement == null)
				return null;
			String tagValue = aElement.getFirstChild().getNodeValue();
			return tagValue;
		}

		/**
		 * Reads the usedtasks tag of the xml and returns the list of usedtasks
		 * 
		 * @param datasetElement
		 * @return
		 */
		private static ArrayList<Task> getUsedtasks(Element datasetElement) {
			ArrayList<Task> usedtasks = new ArrayList<Task>();

			NodeList usedtasksNodeList = datasetElement
					.getElementsByTagName("usedtasks");
			if (usedtasksNodeList != null && usedtasksNodeList.getLength() > 0) {
				Element usedtasksElement = (Element) usedtasksNodeList.item(0);
				NodeList taskNodeList = usedtasksElement
						.getElementsByTagName("task");
				for (int i = 0; i < taskNodeList.getLength(); i++) {
					Element taskElement = (Element) taskNodeList.item(i);
					String taskName = taskElement.getFirstChild()
							.getNodeValue();
					Task task = tasks.getTask(taskName);
					// Task successorDS = getDataset(successorName);
					usedtasks.add(task);
				}
			}
			return usedtasks;
		}

		/**
		 * Reads the nexttasks tag of the control.xml and returns the list of
		 * nexttasks
		 * 
		 * @param element
		 * @return
		 */
		private static ArrayList<Task> getNextTasks(Element element) {
			// TODO Auto-generated method stub
			ArrayList<Task> nextTasks = new ArrayList<Task>();

			NodeList nexttasksNodeList = element
					.getElementsByTagName("nexttasks");
			if (nexttasksNodeList != null && nexttasksNodeList.getLength() > 0) {
				Element nexttaskseElement = (Element) nexttasksNodeList.item(0);
				NodeList tasknNodeList = nexttaskseElement
						.getElementsByTagName("nexttask");
				for (int i = 0; i < tasknNodeList.getLength(); i++) {
					Element taskElement = (Element) tasknNodeList.item(i);
					String taskName = taskElement.getFirstChild()
							.getNodeValue();
					Task task = tasks.getTask(taskName);
					nextTasks.add(task);
				}
			}
			return nextTasks;
		}

		public static void OutputTheResult(ArrayList<Strategy.S> Ss) {
			// TODO Auto-generated method stub
			int movetimes = 0;
			double transcost = 0;
			double timecost = Double.MAX_VALUE;
			for (strategies.DR.Strategy.S s : Ss)
				if (s.getTimecost() < timecost) {
					timecost = s.getTimecost();
					movetimes = s.getMovetimes();
					transcost = s.getTranscost();
				}
			File file = new File(R.FOLDER + R.outputFolder + "result.txt");
			try {
				BufferedWriter bufferedWriter = new BufferedWriter(
						new FileWriter(file));
				bufferedWriter.write("timecost = ");
				bufferedWriter.write("" + timecost);
				bufferedWriter.newLine();

				bufferedWriter.write("movetimes = ");
				bufferedWriter.write("" + movetimes);
				bufferedWriter.newLine();

				bufferedWriter.write("transcost = ");
				bufferedWriter.write("" + transcost);

				bufferedWriter.flush();
				bufferedWriter.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * 从Ss里面获得最优解，输出
		 * 
		 * @param Ss
		 * @param param
		 */
		public static void OutputTheResult(ArrayList<Strategy.S> Ss, int param) {
			Strategy.S S = null;
			double timecost = Double.MAX_VALUE;
			for (strategies.DR.Strategy.S s : Ss)
				if (s.getTimecost() < timecost) {
					timecost = s.getTimecost();
					S = s;
				}
			OutputOneSolution(S, "result" + param);
		}

		public static void OutputOneSolution(Strategy.S s, String filename) {
			int movetimes = s.getMovetimes();
			double transcost = s.getTranscost();
			double timecost = s.getTimecost();
			File file = new File(R.FOLDER + R.outputFolder + filename + ".txt");
			try {
				BufferedWriter bufferedWriter = new BufferedWriter(
						new FileWriter(file));
				bufferedWriter.write("timecost = ");
				bufferedWriter.write("" + timecost);
				bufferedWriter.newLine();

				bufferedWriter.write("movetimes = ");
				bufferedWriter.write("" + movetimes);
				bufferedWriter.newLine();

				bufferedWriter.write("transcost = ");
				bufferedWriter.write("" + transcost);

				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static void OutputTheResult(ArrayList<Strategy.S> Ss,
				ArrayList<Strategy.S> Ss2, Strategy.S hadoop) {
			// TODO Auto-generated method stub
			int movetimes = 0;
			double transcost = 0;
			double timecost = Double.MAX_VALUE;
			for (strategies.DR.Strategy.S s : Ss)
				if (s.getTimecost() < timecost) {
					timecost = s.getTimecost();
					movetimes = s.getMovetimes();
					transcost = s.getTranscost();
				}
			File file = new File(R.FOLDER + R.outputFolder + "result.txt");
			try {
				BufferedWriter bufferedWriter = new BufferedWriter(
						new FileWriter(file));
				bufferedWriter.write("无副本策略");
				bufferedWriter.newLine();
				bufferedWriter.write("timecost = ");
				bufferedWriter.write("" + timecost);
				bufferedWriter.newLine();

				bufferedWriter.write("movetimes = ");
				bufferedWriter.write("" + movetimes);
				bufferedWriter.newLine();

				bufferedWriter.write("transcost = ");
				bufferedWriter.write("" + transcost);
				bufferedWriter.newLine();

				movetimes = 0;
				transcost = 0;
				timecost = Double.MAX_VALUE;
				for (strategies.DR.Strategy.S s : Ss2)
					if (s.getTimecost() < timecost) {
						timecost = s.getTimecost();
						movetimes = s.getMovetimes();
						transcost = s.getTranscost();
					}

				bufferedWriter.write("本文策略");
				bufferedWriter.newLine();
				bufferedWriter.write("timecost = ");
				bufferedWriter.write("" + timecost);
				bufferedWriter.newLine();

				bufferedWriter.write("movetimes = ");
				bufferedWriter.write("" + movetimes);
				bufferedWriter.newLine();

				bufferedWriter.write("transcost = ");
				bufferedWriter.write("" + transcost);
				bufferedWriter.newLine();

				bufferedWriter.write("hadoop策略");
				bufferedWriter.newLine();
				bufferedWriter.write("timecost = ");
				bufferedWriter.write("" + hadoop.getTimecost());
				bufferedWriter.newLine();

				bufferedWriter.write("movetimes = ");
				bufferedWriter.write("" + hadoop.getMovetimes());
				bufferedWriter.newLine();

				bufferedWriter.write("transcost = ");
				bufferedWriter.write("" + hadoop.getTranscost());
				bufferedWriter.newLine();

				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static class Strategy {
		// 这个变量用来优化淘汰算法的
		static double maxtimecost = 0;

		static Random random = new Random();
		public static int ScCHROMOSOMELENGTH;
		public static int ScGENELENGTH;
		public static int PaCHROMOSOMELENGTH;
		public static int PaGENENLENGTH;
		public static int[] DescripDSC;
		public static ArrayList<S> CH;
		public static ArrayList<Task> tasksequence;

		public static void initialize() {
			{
				ScCHROMOSOMELENGTH = tasks.getTasks().size();
				ScGENELENGTH = dataSets.getDataSets().size();
				PaCHROMOSOMELENGTH = dataSets.getDataSets().size();
				PaGENENLENGTH = (int) (Math.log(cloud.getDataCenters().size())
						/ Math.log(2) + 0.999999);
				DescripDSC = new int[dataSets.getDataSets().size()];
			}
			{
				int pointer = 0;
				for (int i = 0; i < dataSets.getDistinctDataNum(); i++) {
					String name = dataSets.getDataset(i + 1).getName();
					int num = dataSets.gettheCopyNum(name);
					for (int j = 0; j < num; j++)
						DescripDSC[pointer++] = i + 1;
				}
			}
			{
				CH = new ArrayList<S>();
				tasksequence = new ArrayList<Task>();
			}
			{
				ArrayList<Task> tempTasks = new ArrayList<Task>();
				for (Task task : tasks.getTasks())
					tempTasks.add(task);
				while (tempTasks.size() > 0) {
					for (int i = 0; i < tempTasks.size(); i++) {
						// for (Task task : tempTasks) {
						Task task = tempTasks.get(i);
						boolean isSelected = true;
						for (Task Predecessor : task.getPredecessors()) {
							boolean tobeSelected = false;
							for (Task finished : tasksequence) {
								if (finished.getName().equals(
										Predecessor.getName()))
									tobeSelected = true;
							}
							if (!tobeSelected)
								isSelected = false;
						}
						if (isSelected) {
							tasksequence.add(tempTasks.get(i));
							tempTasks.remove(i);
						}
					}
				}
			}
		}

		public static void TimeAndTransAndMoveCosttotal_withoutSc(S s) {
			double cost = 0;
			double transcost = 0;
			int movecost = 0;
			int i = 0;
			Task t = tasksequence.get(i++);
			while (i < tasksequence.size()) {// 按照任务执行先后次序遍历出每个任务
				if (false) {// if(t是分支任务)
					// for (每个分支){
					// 记第i个分支对应的子流程为Pi ;
					// 递归计算TimeCosttotal(DC, Pi, s), 结果记为costi;
					// cost = cost + max{costi};
					t = tasksequence.get(i++);
				} else {
					double mincost = Double.MAX_VALUE;
					int tmovecost = 0;
					double ttranscost = 0;
					for (int j = 1; j <= cloud.getDataCenters().size(); j++) {// 遍历出每个数据中心
						double sumcost = 0;
						int summovecost = 0;
						double sumtranscost = 0;

						String tdc = cloud.getDataCenter(j).getName();
						int tID = t.getID();
						S.Gene[] Pa = s.getPa();

						for (DataSet dataSet : t.getInputDataSets()) {// 遍历出出每个输入数据
							String dName = dataSet.getName();
							double dSize = dataSet.getDatasize();
							int dId = dataSet.getID();
							int copyno = dataSets.gettheCopyNum(dName);

							int pointer = 0;
							while (DescripDSC[pointer] != dId)
								pointer++;

							double mincostDSC = Double.MAX_VALUE;
							for (int k = 0; k < copyno; k++) {// 遍历出每个输入数据的副本，找出最“临近”的那一个
								int lc = Pa[pointer + k].getValueofGene();
								String ddc = cloud.getDataCenter(lc).getName();
								if (ddc.equals(tdc)) {
									mincostDSC = 0;
									break;
								}
								double bandwidth = cloud.getBandWidth(tdc, ddc);
								double DSCcost = dSize / bandwidth;
								if (DSCcost < mincostDSC)
									mincostDSC = DSCcost;
							}
							if (mincostDSC == 0)
								continue;
							sumcost += mincostDSC;
							sumtranscost += dSize;
							summovecost++;
						}
						if (mincost > sumcost) {
							mincost = sumcost;
							tmovecost = summovecost;
							ttranscost = sumtranscost;
						}
					}
					cost += mincost;
					movecost += tmovecost;
					transcost += ttranscost;
					t = tasksequence.get(i++);
				}
			}
			s.setTimecost(cost);
			s.setMovetimes(movecost);
			s.setTranscost(transcost);

		}

		public static void TimeAndTransAndMoveCosttotal(S s) {
			double cost = 0;
			double transcost = 0;
			int movecost = 0;
			int i = 0;
			Task t = tasksequence.get(i++);
			while (i < tasksequence.size()) {
				if (false) {// if(t是分支任务)
					// for (每个分支){
					// 记第i个分支对应的子流程为Pi ;
					// 递归计算TimeCosttotal(DC, Pi, s), 结果记为costi;
					// cost = cost + max{costi};
					t = tasksequence.get(i++);
				} else {
					double mincost = Double.MAX_VALUE;
					int tmovecost = 0;
					double ttranscost = 0;
					for (int j = 1; j <= cloud.getDataCenters().size(); j++) {
						double sumcost = 0;
						int summovecost = 0;
						double sumtranscost = 0;
						String tdc = cloud.getDataCenter(j).getName();
						int tID = t.getID();
						S.Gene[] Sc = s.getSc();
						S.Gene[] Pa = s.getPa();
						for (int k = 0; k < Sc[tID - 1].getBit().length; k++) {
							if (Sc[tID - 1].getBit()[k] == 1) {
								int dID = DescripDSC[k];
								String ddc = cloud.getDataCenter(
										Pa[k].getValueofGene()).getName();
								if (ddc.equals(tdc))
									continue;
								double bandwidth = cloud.getBandWidth(tdc, ddc);
								sumcost += dataSets.getDataset(dID)
										.getDatasize() / bandwidth;
								sumtranscost += dataSets.getDataset(dID)
										.getDatasize();
								summovecost++;
							}
						}
						if (mincost > sumcost) {
							mincost = sumcost;
							tmovecost = summovecost;
							ttranscost = sumtranscost;
						}
					}
					cost += mincost;
					movecost += tmovecost;
					transcost += ttranscost;
					t = tasksequence.get(i++);
				}
			}
			s.setTimecost(cost);
			s.setMovetimes(movecost);
			s.setTranscost(transcost);
		}

		public static ArrayList<S> Heredity() {
			maxtimecost = 0;
			System.err.println("Create popSize random S");
			int i = 1;
			while (i <= R.popSize) {
				System.err.println("Create " + i + "th random S");
				S s = S.getRandomS();

				TimeAndTransAndMoveCosttotal(s);
				if (!CHcontainsS(s, 0)) {
					System.err.println("Create " + i + "th random S Finished");
					i++;
				}
			}
			System.err.println("Create popSize random S Finished");
			int var_speed_gen = 0;
			int speed_gen = 0;
			double lttc = Double.MAX_VALUE;
			int curGen = 0;
			while (curGen < R.maxGen) {
				System.out.println("============curGen:" + curGen
						+ "=============");
				printlnLineInfo("变异阶段");
				ArrayList<S> Ss = new ArrayList<S>();
				for (S s : CH) {
					if (random.nextDouble() < R.variation) {
						// SC变异阶段
						try {
							s = s.clone();
						} catch (CloneNotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						boolean changed = false;
						while (!changed) {
							changed = true;
							strategies.DR.Strategy.S.Gene[] Sc = s.getSc();
							int p = random.nextInt(ScCHROMOSOMELENGTH
									* ScGENELENGTH) + 1;
							int t = (p - 1) / ScGENELENGTH;
							int d = (p - 1) % ScGENELENGTH;
							int start, end;
							for (start = d; start >= 0; start--)
								if (DescripDSC[start] != DescripDSC[d]) {
									start++;
									break;
								}
							for (end = d; end < ScGENELENGTH; end++)
								if (DescripDSC[end] != DescripDSC[d]) {
									end--;
									break;
								}
							if (start < 0)
								start++;
							if (end >= ScGENELENGTH)
								end--;
							boolean zero = true;
							for (int k = start; k <= end; k++)
								if (Sc[t].getBit()[k] == 1)
									zero = false;
							if (Sc[t].getBit()[d] == 0) {
								if (zero) {
									changed = false;
									continue;
								} else {
									for (int k = start; k <= end; k++)
										Sc[t].reSet(k);
									Sc[t].Set(d);
								}
							} else {
								Sc[t].reSet(d);
								Sc[t].Set(start
										+ random.nextInt(end - start + 1));
							}
						}
						// Pa变异阶段
						S.Gene[] Pa = s.getPa();
						while (true) {
							int p = random.nextInt(PaCHROMOSOMELENGTH
									* PaGENENLENGTH) + 1;
							int d = (p - 1) / PaGENENLENGTH;
							int c = (p - 1) % PaGENENLENGTH;
							Pa[d].Reverse(c);
							if (S.isPaValid(Pa))
								break;
							Pa[d].Reverse(c);
						}

						Ss.add(s);
					}
				}

				for (S s : Ss) {
					TimeAndTransAndMoveCosttotal(s);
					CHcontainsS(s, 0);
				}
				printlnLineInfo("变异阶段->end");

				// 赌轮算法计算概率
				double m = 0;
				for (S s : CH)
					if (m < s.getTimecost())
						m = s.getTimecost();
				m++;// 使结果不至于出现0或者null
				double totaltime = 0;
				for (S s : CH)
					totaltime += m - s.getTimecost();
				double sumtime = 0;
				for (S s : CH) {
					s.setProbilityl(sumtime / totaltime);
					sumtime += m - s.getTimecost();
					s.setProbilityr(sumtime / totaltime);
				}

				System.out.print("交叉阶段");
				int j = 0;
				double select1, select2;
				S s1 = null, s2 = null;
				while (j < R.genSize) {
					select1 = random.nextDouble();
					select2 = random.nextDouble();
					for (int k = 0; k < CH.size(); k++) {
						S s = CH.get(k);
						if (s.isSelected(select1) && s.isSelected(select2)) {
							select1 = random.nextDouble();
							select2 = random.nextDouble();
							k = -1;
							continue;
						}
						if (s.isSelected(select1))
							s1 = s;
						if (s.isSelected(select2))
							s2 = s;
					}
					try {
						s1 = s1.clone();
						s2 = s2.clone();
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (random.nextDouble() < R.chiasma) {// 交叉操作
						S.Gene[] Sc1 = s1.getSc();
						S.Gene[] Sc2 = s2.getSc();
						int q = random.nextInt(ScCHROMOSOMELENGTH
								* ScGENELENGTH) + 1;
						int t = (q - 1) / ScGENELENGTH;
						int d = (q - 1) % ScGENELENGTH;
						int start;
						for (start = d; start >= 0; start--)
							if (DescripDSC[start] != DescripDSC[d]) {
								start++;
								break;
							}
						if (start < 0)
							start++;
						for (int k = start; k < ScGENELENGTH; k++) {
							byte temp = Sc1[t].getBit()[k];
							Sc1[t].getBit()[k] = Sc2[t].getBit()[k];
							Sc2[t].getBit()[k] = temp;
						}
						for (int k = t + 1; k < ScCHROMOSOMELENGTH; k++) {
							S.Gene temp = Sc1[k];
							Sc1[k] = Sc2[k];
							Sc2[k] = temp;
						}
						S.Gene[] Pa1 = s1.getPa();
						S.Gene[] Pa2 = s2.getPa();

						boolean[] ptemp = new boolean[PaCHROMOSOMELENGTH
								* PaGENENLENGTH + 1];
						while (true) {
							int p = random.nextInt(PaCHROMOSOMELENGTH
									* PaGENENLENGTH) + 1;
							while (true) {
								if (ptemp[p - 1]) {
									p = random.nextInt(PaCHROMOSOMELENGTH
											* PaGENENLENGTH) + 1;
									continue;
								}
								ptemp[p - 1] = true;
								break;
							}

							d = (p - 1) / PaGENENLENGTH;
							int c = (p - 1) % PaGENENLENGTH;

							for (int k = c; k < PaGENENLENGTH; k++) {
								byte temp = Pa1[d].getBit()[k];
								Pa1[d].getBit()[k] = Pa2[d].getBit()[k];
								Pa2[d].getBit()[k] = temp;
							}
							if (Pa1[d].getValueofGene() > cloud
									.getDataCenters().size()
									|| Pa1[d].getValueofGene() > cloud
											.getDataCenters().size()) {
								for (int k = c; k < PaGENENLENGTH; k++) {
									byte temp = Pa1[d].getBit()[k];
									Pa1[d].getBit()[k] = Pa2[d].getBit()[k];
									Pa2[d].getBit()[k] = temp;
								}
								continue;
							}

							for (int k = d; k < PaCHROMOSOMELENGTH; k++) {
								S.Gene temp = Pa1[k];
								Pa1[k] = Pa2[k];
								Pa2[k] = temp;
							}

							if (S.isPaValid(Pa1) && S.isPaValid(Pa2))
								break;

							for (int k = c; k < PaGENENLENGTH; k++) {
								byte temp = Pa1[d].getBit()[k];
								Pa1[d].getBit()[k] = Pa2[d].getBit()[k];
								Pa2[d].getBit()[k] = temp;
							}

							for (int k = d; k < PaCHROMOSOMELENGTH; k++) {
								S.Gene temp = Pa1[k];
								Pa1[k] = Pa2[k];
								Pa2[k] = temp;
							}
						}

						TimeAndTransAndMoveCosttotal(s1);
						boolean contained;
						if (s1.getTimecost() < maxtimecost) {
							contained = CHcontainsS(s1, 2);
							if (!contained)
								j++;
						} else {
							j++;
						}

						TimeAndTransAndMoveCosttotal(s2);
						if (s2.getTimecost() < maxtimecost) {
							contained = CHcontainsS(s2, 2);
							if (!contained)
								j++;
						} else {
							j++;
						}
					}
				}
				printlnLineInfo("交叉阶段->end");

				// 从CH中除去后面genSize个解，
				while (CH.size() > R.popSize)
					CH.remove(CH.size() - 1);
				double mintimecost = CH.get(0).getTimecost();
				maxtimecost = CH.get(CH.size() - 1).getTimecost();
				System.out.println("mintimecost:\t" + mintimecost
						+ "\tmaxtimecost:" + maxtimecost);

				// 程序退出条件
				if (CH.get(0).getMovetimes() == 0) {
					System.err.println("结果为零！提前退出。");
					break;
				}
				if (curGen > R.minGen) {
					// 计算连续误差小的代数
					if ((maxtimecost - mintimecost) / maxtimecost < R.variance)
						var_speed_gen++;
					else
						var_speed_gen = 0;

					// 计算连续进化速度慢的代数
					double nttc = 0;
					for (S s : CH)
						nttc += s.getTimecost();
					if ((lttc - nttc) / lttc < R.speed)
						speed_gen++;
					else
						speed_gen = 0;
					lttc = nttc;

					// 相对误差小&&进化速度慢
					if (var_speed_gen > R.var_and_speed_Gen
							&& speed_gen > R.var_and_speed_Gen) {
						System.err.println("(相对误差小&&进化速度慢)这样的情况超过若干代如"
								+ R.var_and_speed_Gen + "代");
						break;
					}

					// 进化速度慢
					if (speed_gen > R.speed_Gen) {
						System.err.println("(进化速度慢)这样的情况超过若干代如" + R.speed_Gen
								+ "代");
						break;
					}

				}
				curGen++;
			}
			return CH;
		}

		/**
		 * 重要函数优化，完成搜索、插入、淘汰操作 mxtc为0是插入操作，2在0的基础上有淘汰操作
		 * 
		 * @param s
		 * @param mxtc
		 * @return
		 */
		public static boolean CHcontainsS(S s, int mxtc) {
			int l = 0, r = CH.size() - 1;
			int location = (l + r) / 2;
			while (l < r - 1) {
				if (CH.get(location).getTimecost() > s.getTimecost()) {
					r = location;
					location = (l + r) / 2;
					continue;
				}
				if (CH.get(location).getTimecost() < s.getTimecost()) {
					l = location;
					location = (l + r) / 2;
					continue;
				}
				if (CH.get(location).getTimecost() == s.getTimecost()) {
					break;
				}
			}
			if (CH.size() == 0) {
				CH.add(s);
				return false;
			}
			if (CH.get(l).isTheSame(s) || CH.get(r).isTheSame(s)) {
				return true;
			}
			if (mxtc == 0 || mxtc == 2) {
				if (CH.get(0).getTimecost() >= s.getTimecost())
					CH.add(0, s);
				else if (CH.get(CH.size() - 1).getTimecost() <= s.getTimecost()) {
					CH.add(CH.size(), s);
				} else {
					CH.add(location + 1, s);
				}
			}
			if (mxtc == 2) {
				for (int i = CH.size() - 1; i >= R.popSize; i--) {
					if (CH.get(i).getProbilityr() < 0) {
						CH.remove(i);
						i--;
					}
				}
			}
			return false;
		}

		private static class S implements Comparable<S> {
			private double timecost = 0;
			private int movetimes = 0;
			private double transcost = 0;
			private double probilityl = -1, probilityr = -1;
			private Gene[] Sc, Pa;

			public static S getRandomS() {
				printlnLineInfo("getRandomS");
				S randS = new S();
				// Sc
				printlnLineInfo("Sc");
				Gene[] Sc = new Gene[ScCHROMOSOMELENGTH];
				for (int i = 0; i < ScCHROMOSOMELENGTH; i++)
					Sc[i] = new Gene(ScGENELENGTH);
				for (int i = 0; i < ScCHROMOSOMELENGTH; i++) {
					int pointer = 0;
					for (int j = 0; j < dataSets.getDistinctDataNum(); j++) {
						String name = dataSets.getDataset(j + 1).getName();
						int num = dataSets.gettheCopyNum(name);
						for (DataSet dataSet : tasks.getTask(i + 1)
								.getInputDataSets()) {
							if (dataSet.getName().equals(name)) {
								Sc[i].Set(random.nextInt(num) + pointer);
							}
						}
						pointer += num;
					}
				}
				printlnLineInfo("Sc-out");
				printlnLineInfo("Pa");
				// Pa
				Gene[] Pa = new Gene[PaCHROMOSOMELENGTH];
				boolean jumpout = false;
				int dcnum = cloud.getDataCenters().size();
				{
					double[] referstorage = new double[dcnum];
					double[] tempstorage = new double[dcnum];
					for (int i = 0; i < dcnum; i++)
						referstorage[i] = cloud.getDataCenter(i + 1).getCs()
								* R.lamda;
					for (int i = 0; i < PaCHROMOSOMELENGTH; i++)
						Pa[i] = new Gene(PaGENENLENGTH);
					do {
						for (int i = 0; i < dcnum; i++)
							tempstorage[i] = 0;
						jumpout = false;
						int pointer = 0;
						for (int i = 0; i < dataSets.getDistinctDataNum(); i++) {
							DataSet dataSet = dataSets.getDataset(i + 1);
							String name = dataSet.getName();
							double size = dataSet.getDatasize();
							int num = dataSets.gettheCopyNum(name);

							// 随机分配数据中心
							int[] toBePa = new int[num];
							for (int j = 0; j < num; ++j) {

								boolean[] bool = new boolean[dcnum];
								for (int k = 0; k < bool.length; k++)
									bool[k] = false;
								int testcount = 0;

								toBePa[j] = random.nextInt(dcnum) + 1;
								bool[toBePa[j] - 1] = true;
								testcount++;

								int k = 0;
								while (k < j) {
									if (toBePa[k] == toBePa[j]
											|| (referstorage[toBePa[j] - 1] <= tempstorage[toBePa[j] - 1]
													+ size)) {
										toBePa[j] = random.nextInt(dcnum) + 1;

										if (testcount > 2 * dcnum) {
											boolean result = true;
											for (int L = 0; L < bool.length; L++)
												result = result && bool[L];
											if (result) {
												// 现在表示无法为改数据集分配到数据中心，得重来
												jumpout = true;
												break;
											}
										}
										bool[toBePa[j] - 1] = true;
										testcount++;
										k = 0;
									} else {
										k++;
									}
								}
								if (jumpout)
									break;

								tempstorage[toBePa[j] - 1] += size;
								Pa[pointer++].setGene(toBePa[j]);

							}
							if (jumpout)
								break;
							else {
							}
						}
						// if (!jumpout && isPaValid(Pa))
						// printlnLineInfo(jumpout);
						// System.out.println(jumpout + "" + isPaValid(Pa));
						// System.out.println(jumpout || !isPaValid(Pa));
						// printlnLineInfo("Pa-loop");
					} while (jumpout || !isPaValid(Pa));
				}
				printlnLineInfo("Pa-out");
				randS.setSc(Sc);
				randS.setPa(Pa);
				printlnLineInfo("getRandomS-out");
				return randS;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Object#clone()
			 */
			@Override
			protected S clone() throws CloneNotSupportedException {
				// TODO Auto-generated method stub
				Gene[] newSc = new Gene[Sc.length];
				for (int i = 0; i < Sc.length; i++)
					newSc[i] = Sc[i].clone();
				Gene[] newPa = new Gene[Pa.length];
				for (int i = 0; i < Pa.length; i++)
					newPa[i] = Pa[i].clone();
				S s = new S();
				s.setSc(newSc);
				s.setPa(newPa);
				return s;
			}

			/**
			 * @param probilityl
			 *            the probilityl to set
			 */
			public void setProbilityl(double probilityl) {
				this.probilityl = probilityl;
			}

			public boolean isSelected(double rand) {
				return rand >= probilityl && rand < probilityr;
			}

			/**
			 * @param probilityr
			 *            the probilityr to set
			 */
			public void setProbilityr(double probilityr) {
				this.probilityr = probilityr;
			}

			/**
			 * @return the probilityl
			 */
			public double getProbilityl() {
				return probilityl;
			}

			/**
			 * @return the probilityr
			 */
			public double getProbilityr() {
				return probilityr;
			}

			/**
			 * @return the timecost
			 */
			public double getTimecost() {
				return timecost;
			}

			/**
			 * @param timecost
			 *            the timecost to set
			 */
			public void setTimecost(double timecost) {
				this.timecost = timecost;
			}

			/**
			 * @return the movetimes
			 */
			public int getMovetimes() {
				return movetimes;
			}

			/**
			 * @param movetimes
			 *            the movetimes to set
			 */
			public void setMovetimes(int movetimes) {
				this.movetimes = movetimes;
			}

			/**
			 * @return the transcost
			 */
			public double getTranscost() {
				return transcost;
			}

			/**
			 * @param transcost
			 *            the transcost to set
			 */
			public void setTranscost(double transcost) {
				this.transcost = transcost;
			}

			/**
			 * @return the sc
			 */
			public Gene[] getSc() {
				return Sc;
			}

			/**
			 * @param sc
			 *            the sc to set
			 */
			public void setSc(Gene[] sc) {
				Sc = sc;
			}

			/**
			 * @return the pa
			 */
			public Gene[] getPa() {
				return Pa;
			}

			/**
			 * @param pa
			 *            the pa to set
			 */
			public void setPa(Gene[] pa) {
				Pa = pa;
			}

			public boolean isValid() {
				return isScValid(Sc) && isPaValid(Pa);
			}

			public static boolean isScValid(Gene[] Sc) {
				for (int i = 0; i < Sc.length; i++) {
					int pointer = 0;
					for (int j = 0; j < dataSets.getDistinctDataNum(); j++) {
						String name = dataSets.getDataset(j + 1).getName();
						int num = dataSets.gettheCopyNum(name);
						int counts = 0;
						for (int k = 0; k < num; k++)
							if (Sc[i].getState(pointer++) == 1)
								counts++;
						if (counts == 2)
							return false;// 第一类无效解
						boolean contain = false;
						for (DataSet dataSet : tasks.getTask(i + 1)
								.getInputDataSets())
							if (dataSet.getName().equals(name))
								contain = true;
						if ((contain && counts == 0)
								|| (!contain && counts > 0))
							return false;// 第二类无效解
					}
				}
				return true;
			}

			public static boolean isPaValid(Gene[] Pa) {
				// Pa第一类无效解
				for (int i = 0; i < Pa.length; i++) {
					if (Pa[i].getValueofGene() > cloud.getDataCenters().size()) {
						return false;
					}
				}
				// Pa第三类无效解
				int pointer = 0;
				for (int i = 0; i < dataSets.getDistinctDataNum(); i++) {
					String name = dataSets.getDataset(i + 1).getName();
					int num = dataSets.gettheCopyNum(name);
					for (int j = 0; j < num; ++j) {
						int k = 0;
						while (k < j) {
							if (Pa[pointer + j].getValueofGene() == Pa[pointer
									+ k].getValueofGene()) {
								return false;
							}
							k++;
						}
					}
					pointer += num;
				}
				// Pa第二类无效解
				int dcnum = cloud.getDataCenters().size();
				double[] stored = new double[dcnum];
				for (int i = 0; i < dcnum; i++)
					stored[i] = 0;
				for (int i = 0; i < Pa.length; i++) {
					stored[Pa[i].getValueofGene() - 1] += dataSets.getDataset(
							DescripDSC[i]).getDatasize();
				}
				for (int i = 0; i < dcnum; ++i)
					if (stored[i] >= (cloud.getDataCenter(i + 1).getCs() * R.lamda)) {
						return false;
					}
				return true;
			}

			public boolean isTheSame(S s) {
				if (movetimes != s.getMovetimes())
					return false;
				if ((timecost - s.getTimecost() > 0 ? (timecost - s
						.getTimecost()) : (s.getTimecost() - timecost)) > 0.01)
					return false;

				for (int i = 0; i < Sc.length; i++) {
					for (int j = 0; j < Sc[i].getBit().length; j++) {
						if (Sc[i].getBit()[j] != s.getSc()[i].getBit()[j])
							return false;
					}
				}
				for (int i = 0; i < Pa.length; i++) {
					for (int j = 0; j < Pa[i].getBit().length; j++) {
						if (Pa[i].getBit()[j] != s.getPa()[i].getBit()[j])
							return false;
					}
				}
				return true;
			}

			private static class Gene {
				private byte[] bit;

				Gene() {

				}

				// for Pa
				public void setGene(int i) {
					for (int k = 0; k < bit.length; k++) {
						bit[k] = 0;
					}
					if (i == cloud.getDataCenters().size())
						return;
					int P = 1;
					while (i >= 1) {
						if (i % 2 != 0) {
							Set(bit.length - P);
						} else {
							reSet(bit.length - P);
						}
						i /= 2;
						P++;
					}
				}

				// for Pa
				public int getValueofGene() {

					int re = 0;
					for (int pointer = 1; pointer <= bit.length; pointer++) {
						if (bit[bit.length - pointer] == 1)
							re += Math.pow(2, pointer - 1);
					}
					if (re == 0)
						re = cloud.getDataCenters().size();
					return re;
				}

				public int getState(int i) {
					return bit[i];
				}

				public void Set(int i) {
					bit[i] = 1;
				}

				public void reSet(int i) {
					bit[i] = 0;
				}

				public void Reverse(int i) {
					bit[i] = (byte) (1 - bit[i]);
				}

				Gene(int genelength) {
					bit = new byte[genelength];
					for (int i = 0; i < bit.length; i++)
						bit[i] = 0;
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Object#clone()
				 */
				@Override
				protected Gene clone() throws CloneNotSupportedException {
					// TODO Auto-generated method stub
					byte[] newbit = new byte[bit.length];
					for (int i = 0; i < bit.length; i++) {
						newbit[i] = bit[i];
					}
					Gene gene = new Gene();
					gene.setBit(newbit);
					return gene;
				}

				/**
				 * @return the bit
				 */
				public byte[] getBit() {
					return bit;
				}

				/**
				 * @param bit
				 *            the bit to set
				 */
				public void setBit(byte[] bit) {
					this.bit = bit;
				}
			}

			@Override
			public int compareTo(S o) {
				if (this.timecost < o.timecost)
					return -1;
				if (this.timecost > o.timecost)
					return 1;
				return 0;
			}
		}
	}

	public static int getID() {
		String subs, string = "w123";
		int Ret = 0;
		subs = string.substring(1, string.length());
		try {
			if (subs == null || subs.trim().equals(""))
				subs = Ret + "";
			Ret = Integer.parseInt(subs);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return Ret;
	}

	public static void printlnLineInfo(Object s) {
		System.out.println(new Throwable().getStackTrace()[1] + "\n" + "at:"
				+ new Date().toLocaleString() + "-->" + s);
	}

	private static class exit implements Runnable {
		static Scanner scanner = new Scanner(System.in);
		static int i = 0;

		public void run() {
			System.out.println("??");
			while (!exit) {
				i = scanner.nextInt();
				System.out.println("bb");
				if (i == 0) {
					exit = true;
				}
			}
		}

	}

}
