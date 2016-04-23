package srmf.global;

public class Constant {
	public static class GraphProperty {
		/*
		 * G=<V,E> n=|V|,m=|E| avg_deg=|E| / ( 2 * |V| )
		 */
		public static int n;
		public static int avg_deg;

		 public static int m;
		public static int getN() {
			return n;
		}

		public static void setN(int n) {
			GraphProperty.n = n;
		}

		public static int getAvg_deg() {
			return avg_deg;
		}

		public static void setAvg_deg(int avg_deg) {
			GraphProperty.avg_deg = avg_deg;
		}

		public static int getM() {
			return m;
		}

		public static void setM(int m) {
			GraphProperty.m = m;
		}
		
	}

	public static class SrlgProperty {
		/*
		 * avg_link: the average number of links each SRGL contains
		 */
		public static int avg_link;
		/*
		 * number of SRGLs
		 * 
		 * srgl_num
		 */
		public static int srlg_num;

		public static int getAvg_link() {
			return avg_link;
		}

		public static void setAvg_link(int avg_link) {
			SrlgProperty.avg_link = avg_link;
		}

		public static int getSrlg_num() {
			return srlg_num;
		}

		public static void setSrlg_num(int srlg_num) {
			SrlgProperty.srlg_num = srlg_num;
		}


	}

	public static class Cost {
		/*
		 * y:cost ratio
		 */
		public static double y;
		/*
		 * cost_of_detector
		 */
		public static double cost_of_detector;

		public static double getY() {
			return y;
		}

		public static void setY(double y) {
			Cost.y = y;
		}

		public static double getCost_of_detector() {
			return cost_of_detector;
		}

		public static void setCost_of_detector(double cost_of_detector) {
			Cost.cost_of_detector = cost_of_detector;
		}
	}

	public static class Strategy {
		/*
		 * iteration times:N
		 */
		public static int N;
		
		
		/*
		 * Experiment 1
		 */
		public static enum ExtendStrategy {
			random, maxWeight
		}

		public static ExtendStrategy extendStrategy;

		/*
		 * Experiment 2
		 */
		public static enum WeightStrategy {
			useDeg, noDeg
		}

		public static WeightStrategy weightStrategy;

		public static ExtendStrategy getExtendStrategy() {
			return extendStrategy;
		}

		public static void setExtendStrategy(ExtendStrategy extendStrategy) {
			Strategy.extendStrategy = extendStrategy;
		}

		public static WeightStrategy getWeightStrategy() {
			return weightStrategy;
		}

		public static void setWeightStrategy(WeightStrategy weightStrategy) {
			Strategy.weightStrategy = weightStrategy;
		}
	}
}