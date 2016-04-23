package strategies;

import java.io.IOException;

import org.junit.Test;

import strategies.DR.readandwrite;

public class DRjunitTester {
	public static void main(String[] args) {
		new DRjunitTester().test_2015_8_22();
	}

	/**
	 * 测试生成数据是不是有问题
	 */
	@Test
	public void test_2015_8_22() {

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

				System.out.println("----");
				System.out.println(Tn + " " + R.miniDSnum + " " + R.maxiDSnum);
				CreateRandomData.test1();
			}
		}
	}
}
