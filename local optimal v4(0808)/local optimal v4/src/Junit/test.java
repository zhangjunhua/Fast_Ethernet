package Junit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

import strategies.CreateRandomData;
import strategies.R;

public class test {
	public static Random random = new Random();

	@Test
	public void test1() {
		// System.out.println("1234".substring(1));
		// System.out.println(Double.MAX_VALUE);
		printlnLineInfo("Start!");
	}

	@Test
	public void test2() {
		int i = 100;
		while (i-- > 0) {
			System.out.println(random.nextDouble());
		}
	}

	@Test
	public void test3() throws IOException {
		File file = new File("DataReplication/testinput2");
		file.mkdir();
	}

	public static void printlnLineInfo(Object s) {
		StackTraceElement ste = new Throwable().getStackTrace()[1];
		System.err.println(s + "(" + ste.getFileName() + ":"
				+ ste.getLineNumber() + ")");
	}

	@Test
	public void test4() {
		String value = "abcdef2";
		value = value.substring(0, value.length() - 1)
				+ (Integer.parseInt(value.substring(value.length() - 1)) + 1);
		System.out.println(value);
	}

	@Test
	public void test5() {
		System.out.println(0);
		{
			System.out.println(1);
		}
		System.out.println(2);
		{
			System.out.println(3);
		}
		System.out.println(4);
	}

	@Test
	public void test6() {
		boolean[] bool = new boolean[10];
		for (int i = 0; i < 10; i++) {
			System.out.println(bool[i]);
		}
	}

	@Test
	public void test7() {
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		for (int i = 0; i < 5; i++) {
			arrayList.add(arrayList.size(), arrayList.size());
		}
		System.out.println(arrayList);
	}

	@Test
	public void text8() {
		R r = new R();
		try {
			testReflect(r);
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
		}
	}

	public static void testReflect(Object model) throws NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Field[] field = model.getClass().getDeclaredFields();
		for (int j = 0; j < field.length; j++) {
			String name = field[j].getName();
			String type = field[j].getGenericType().toString();

			Method m = model.getClass().getMethod(
					"get" + name.substring(0, 1).toUpperCase()
							+ name.substring(1));
			Object value = m.invoke(model);
			System.out.println(name + ":" + value);
		}
	}
	@Test
	public void test9(){
		System.out.println("testoutput0".substring(10));
		System.out.println("testoutput0".substring(0, 10));
		CreateRandomData.newfolderandrstconf();
		
//		outputdatafolder	 testoutput0
//		inputdatafolder	 testinput0

	}
}
