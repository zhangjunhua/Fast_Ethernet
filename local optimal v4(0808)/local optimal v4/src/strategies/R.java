package strategies;

import java.util.HashMap;

/**
 * @author Admin
 */
public final class R {
	// 数据中心
	public static int maxDCnum = 15;
	public static int minDCnum = 15;
	public static double maxDCstorage = 1000;
	public static double minDCstorage = 1000;
	// 带宽
	public static double maxBandWidth = 10;
	public static double minBandWith = 5;
	// 数据集
	public static int maxiDSnum = 30;
	public static int miniDSnum = 30;
	public static double minDsSize = 17.6;
	public static double maxDsSize = 26.4;

	public static int maxCopyno = 1;
	public static int minCopyno = 1;

	public static int maxTnum = 15;
	public static int minTnum = 15;
	public static int maxIDS = 3;
	public static int minIDS = 1;

	public static double lamda = 0.87723824;
	public static double variation = 0.23423242;
	public static double chiasma = 0.88738278;

	public static double origInputDS_ratio = 0.2;
	// 程序退出控制条件
	public static double variance = 0.001;
	public static double speed = 0.001;
	public static int var_and_speed_Gen = 10;
	public static int speed_Gen = 50;
	public static int minGen = 50;

	public static int genSize = 1000;
	public static int popSize = 100;
	public static int maxGen = 10000;

	public static String FOLDER = "DataReplication/";
	public static String CONFIGURATION = "configuration";
	public static String DATASETS = "datasets.xml";
	public static String NDATASETS = "ndatasets.xml";
	public static String CONTROL = "control.xml";
	public static String CLOUD = "cloud.xml";
	public static HashMap<String, String> configerationhMap = null;
	public static String inputFolder = null;
	public static String outputFolder = null;

	/**
	 * @return the origInputDS_ratio
	 */
	public static double getOrigInputDS_ratio() {
		return origInputDS_ratio;
	}

	/**
	 * @param origInputDS_ratio
	 *            the origInputDS_ratio to set
	 */
	public static void setOrigInputDS_ratio(double origInputDS_ratio) {
		R.origInputDS_ratio = origInputDS_ratio;
	}

	/**
	 * @return the maxDCnum
	 */
	public static int getMaxDCnum() {
		return maxDCnum;
	}

	/**
	 * @return the minDCnum
	 */
	public static int getMinDCnum() {
		return minDCnum;
	}

	/**
	 * @return the maxDCstorage
	 */
	public static double getMaxDCstorage() {
		return maxDCstorage;
	}

	/**
	 * @return the minDCstorage
	 */
	public static double getMinDCstorage() {
		return minDCstorage;
	}

	/**
	 * @return the minGen
	 */
	public static int getMinGen() {
		return minGen;
	}

	/**
	 * @return the maxBandWidth
	 */
	public static double getMaxBandWidth() {
		return maxBandWidth;
	}

	/**
	 * @return the minBandWith
	 */
	public static double getMinBandWith() {
		return minBandWith;
	}

	/**
	 * @return the maxiDSnum
	 */
	public static int getMaxiDSnum() {
		return maxiDSnum;
	}

	/**
	 * @return the miniDSnum
	 */
	public static int getMiniDSnum() {
		return miniDSnum;
	}

	/**
	 * @return the minDsSize
	 */
	public static double getMinDsSize() {
		return minDsSize;
	}

	/**
	 * @return the maxDsSize
	 */
	public static double getMaxDsSize() {
		return maxDsSize;
	}

	/**
	 * @return the maxCopyno
	 */
	public static int getMaxCopyno() {
		return maxCopyno;
	}

	/**
	 * @return the minCopyno
	 */
	public static int getMinCopyno() {
		return minCopyno;
	}

	/**
	 * @return the maxTnum
	 */
	public static int getMaxTnum() {
		return maxTnum;
	}

	/**
	 * @return the minTnum
	 */
	public static int getMinTnum() {
		return minTnum;
	}

	/**
	 * @return the maxIDS
	 */
	public static int getMaxIDS() {
		return maxIDS;
	}

	/**
	 * @return the minIDS
	 */
	public static int getMinIDS() {
		return minIDS;
	}

	/**
	 * @return the lamda
	 */
	public static double getLamda() {
		return lamda;
	}

	/**
	 * @return the variation
	 */
	public static double getVariation() {
		return variation;
	}

	/**
	 * @return the chiasma
	 */
	public static double getChiasma() {
		return chiasma;
	}

	/**
	 * @return the variance
	 */
	public static double getVariance() {
		return variance;
	}

	/**
	 * @return the speed
	 */
	public static double getSpeed() {
		return speed;
	}

	/**
	 * @return the var_and_speed_Gen
	 */
	public static int getVar_and_speed_Gen() {
		return var_and_speed_Gen;
	}

	/**
	 * @return the speed_Gen
	 */
	public static int getSpeed_Gen() {
		return speed_Gen;
	}

	/**
	 * @return the genSize
	 */
	public static int getGenSize() {
		return genSize;
	}

	/**
	 * @return the popSize
	 */
	public static int getPopSize() {
		return popSize;
	}

	/**
	 * @return the maxGen
	 */
	public static int getMaxGen() {
		return maxGen;
	}

	/**
	 * @param maxDCnum
	 *            the maxDCnum to set
	 */
	public static void setMaxDCnum(int maxDCnum) {
		R.maxDCnum = maxDCnum;
	}

	/**
	 * @param minDCnum
	 *            the minDCnum to set
	 */
	public static void setMinDCnum(int minDCnum) {
		R.minDCnum = minDCnum;
	}

	/**
	 * @param maxDCstorage
	 *            the maxDCstorage to set
	 */
	public static void setMaxDCstorage(double maxDCstorage) {
		R.maxDCstorage = maxDCstorage;
	}

	/**
	 * @param minDCstorage
	 *            the minDCstorage to set
	 */
	public static void setMinDCstorage(double minDCstorage) {
		R.minDCstorage = minDCstorage;
	}

	/**
	 * @param maxBandWidth
	 *            the maxBandWidth to set
	 */
	public static void setMaxBandWidth(double maxBandWidth) {
		R.maxBandWidth = maxBandWidth;
	}

	/**
	 * @param minBandWith
	 *            the minBandWith to set
	 */
	public static void setMinBandWith(double minBandWith) {
		R.minBandWith = minBandWith;
	}

	/**
	 * @param maxiDSnum
	 *            the maxiDSnum to set
	 */
	public static void setMaxiDSnum(int maxiDSnum) {
		R.maxiDSnum = maxiDSnum;
	}

	/**
	 * @param miniDSnum
	 *            the miniDSnum to set
	 */
	public static void setMiniDSnum(int miniDSnum) {
		R.miniDSnum = miniDSnum;
	}

	/**
	 * @param minDsSize
	 *            the minDsSize to set
	 */
	public static void setMinDsSize(double minDsSize) {
		R.minDsSize = minDsSize;
	}

	/**
	 * @param maxDsSize
	 *            the maxDsSize to set
	 */
	public static void setMaxDsSize(double maxDsSize) {
		R.maxDsSize = maxDsSize;
	}

	/**
	 * @param maxCopyno
	 *            the maxCopyno to set
	 */
	public static void setMaxCopyno(int maxCopyno) {
		R.maxCopyno = maxCopyno;
	}

	/**
	 * @param minCopyno
	 *            the minCopyno to set
	 */
	public static void setMinCopyno(int minCopyno) {
		R.minCopyno = minCopyno;
	}

	/**
	 * @param maxTnum
	 *            the maxTnum to set
	 */
	public static void setMaxTnum(int maxTnum) {
		R.maxTnum = maxTnum;
	}

	/**
	 * @param minTnum
	 *            the minTnum to set
	 */
	public static void setMinTnum(int minTnum) {
		R.minTnum = minTnum;
	}

	/**
	 * @param maxIDS
	 *            the maxIDS to set
	 */
	public static void setMaxIDS(int maxIDS) {
		R.maxIDS = maxIDS;
	}

	/**
	 * @param minIDS
	 *            the minIDS to set
	 */
	public static void setMinIDS(int minIDS) {
		R.minIDS = minIDS;
	}

	/**
	 * @param lamda
	 *            the lamda to set
	 */
	public static void setLamda(double lamda) {
		R.lamda = lamda;
	}

	/**
	 * @param variation
	 *            the variation to set
	 */
	public static void setVariation(double variation) {
		R.variation = variation;
	}

	/**
	 * @param chiasma
	 *            the chiasma to set
	 */
	public static void setChiasma(double chiasma) {
		R.chiasma = chiasma;
	}

	/**
	 * @param variance
	 *            the variance to set
	 */
	public static void setVariance(double variance) {
		R.variance = variance;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public static void setSpeed(double speed) {
		R.speed = speed;
	}

	/**
	 * @param var_and_speed_Gen
	 *            the var_and_speed_Gen to set
	 */
	public static void setVar_and_speed_Gen(int var_and_speed_Gen) {
		R.var_and_speed_Gen = var_and_speed_Gen;
	}

	/**
	 * @param speed_Gen
	 *            the speed_Gen to set
	 */
	public static void setSpeed_Gen(int speed_Gen) {
		R.speed_Gen = speed_Gen;
	}

	/**
	 * @param minGen
	 *            the minGen to set
	 */
	public static void setMinGen(int minGen) {
		R.minGen = minGen;
	}

	/**
	 * @param genSize
	 *            the genSize to set
	 */
	public static void setGenSize(int genSize) {
		R.genSize = genSize;
	}

	/**
	 * @param popSize
	 *            the popSize to set
	 */
	public static void setPopSize(int popSize) {
		R.popSize = popSize;
	}

	/**
	 * @param maxGen
	 *            the maxGen to set
	 */
	public static void setMaxGen(int maxGen) {
		R.maxGen = maxGen;
	}

	/**
	 * @param fOLDER
	 *            the fOLDER to set
	 */
	public static void setFOLDER(String fOLDER) {
		FOLDER = fOLDER;
	}

	/**
	 * @param cONFIGURATION
	 *            the cONFIGURATION to set
	 */
	public static void setCONFIGURATION(String cONFIGURATION) {
		CONFIGURATION = cONFIGURATION;
	}

	/**
	 * @param dATASETS
	 *            the dATASETS to set
	 */
	public static void setDATASETS(String dATASETS) {
		DATASETS = dATASETS;
	}

	/**
	 * @param nDATASETS
	 *            the nDATASETS to set
	 */
	public static void setNDATASETS(String nDATASETS) {
		NDATASETS = nDATASETS;
	}

	/**
	 * @param cONTROL
	 *            the cONTROL to set
	 */
	public static void setCONTROL(String cONTROL) {
		CONTROL = cONTROL;
	}

	/**
	 * @param cLOUD
	 *            the cLOUD to set
	 */
	public static void setCLOUD(String cLOUD) {
		CLOUD = cLOUD;
	}

	/**
	 * @param configerationhMap
	 *            the configerationhMap to set
	 */
	public static void setConfigerationhMap(
			HashMap<String, String> configerationhMap) {
		R.configerationhMap = configerationhMap;
	}

	/**
	 * @param inputFolder
	 *            the inputFolder to set
	 */
	public static void setInputFolder(String inputFolder) {
		R.inputFolder = inputFolder;
	}

	/**
	 * @param outputFolder
	 *            the outputFolder to set
	 */
	public static void setOutputFolder(String outputFolder) {
		R.outputFolder = outputFolder;
	}

	/**
	 * @return the fOLDER
	 */
	public static String getFOLDER() {
		return FOLDER;
	}

	/**
	 * @return the cONFIGURATION
	 */
	public static String getCONFIGURATION() {
		return CONFIGURATION;
	}

	/**
	 * @return the dATASETS
	 */
	public static String getDATASETS() {
		return DATASETS;
	}

	/**
	 * @return the nDATASETS
	 */
	public static String getNDATASETS() {
		return NDATASETS;
	}

	/**
	 * @return the cONTROL
	 */
	public static String getCONTROL() {
		return CONTROL;
	}

	/**
	 * @return the cLOUD
	 */
	public static String getCLOUD() {
		return CLOUD;
	}

	/**
	 * @return the configerationhMap
	 */
	public static HashMap<String, String> getConfigerationhMap() {
		return configerationhMap;
	}

	/**
	 * @return the inputFolder
	 */
	public static String getInputFolder() {
		return inputFolder;
	}

	/**
	 * @return the outputFolder
	 */
	public static String getOutputFolder() {
		return outputFolder;
	}

}