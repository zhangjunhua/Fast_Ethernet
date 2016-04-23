package strategies;

import java.util.ArrayList;
import java.util.List;

import newClass.CTT_ver;
import newClass.CloudService;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import utilities.DataDependencyGraph;
import utilities.Dataset;

public class CTT_SP3Print {

	/**
	 * �����
	 * 1.�洢�ڸ���cloud�ϵ����ݼ��ĸ�������������ݼ�
	 * 2.����cloud�ϲ��������ݼ��ĸ�������������ݼ�
	 * 3.���ƽ������ʱ��
	 * 3.�������cost���洢cost������cost
	 * 
	 */
	private static int minvalueno=0;
	private static int m=0;
	private static double averageGenerateTime = 0.0;
	public static void print(List<DefaultWeightedEdge> shortestPath,int cm,SimpleDirectedWeightedGraph<CTT_ver, DefaultWeightedEdge> cttGraph,DataDependencyGraph graph,CloudService[] CS,CTT_ver endver)
	{
		m=cm;
		// ��ȷ�����·��������������--------------
		int[] geneDSnum=new int[m];
		int[] storeDSnum=new int[m];
		ArrayList<ArrayList<String>> storeDS=new ArrayList<ArrayList<String>>();
	    ArrayList<ArrayList<String>> geneDS=new ArrayList<ArrayList<String>>();
		for(int i=0;i<m;i++)
		{
		    storeDS.add(new ArrayList<String>());
		    geneDS.add(new ArrayList<String>());
		}
		// cost����ɣ��洢cost������cost������cost
		double storecost=0.0;
		double transcost=0.0;
		double computecost=0.0;
		//����ʱ��
		double genetime=0.0;
	    CTT_ver ver1;
		CTT_ver ver2;
		//���·��������������ڵ�
		for (DefaultWeightedEdge anEdge : shortestPath) {
			ver1 = cttGraph.getEdgeSource(anEdge);
			ver2=cttGraph.getEdgeTarget(anEdge);
			if(!ver1.getdataset().getName().equals("start"))//���ver1Ϊ��ʼ�������ݼ�������Ҫ������洢cost��Ҳ������洢������
			{
				//��Ӧ�Ʒ����ϴ洢���ݼ�������1���������Ʒ����Ӧ�Ĵ洢������
				storeDSnum[ver1.getcsid()]++;
				storeDS.get(ver1.getcsid()).add(ver1.getdataset().getName());
				//����洢���ݼ��Ĵ洢����
				storecost=storecost+CS[ver1.getcsid()].getcostS()*ver1.getdataset().getSize();//ÿ��ֻ��Դ�ڵ�Ĵ洢cost����
			}
			//�ж�ver1�Ƿ�Ϊ���һ�����ݼ�������ǵĻ�����˵��ver1�ĺ��Ϊ����ڵ�endver
			Dataset DS;
			if(ver1.getdataset().getName().equals(graph.getLastDataset().getName()))
			{
				DS=endver.getdataset();
			}
			else
			{
				DS=ver1.getdataset().getSuccessors().get(0);
			}
			if(!DS.equals(ver2.getdataset()))//�����ж�ʽΪtrue����˵���������ݲ�ͬ���������м���������ݼ�
			{
				// ͳ�Ƶ�һ���м����ݼ�����ver1�ĵ�һ����̵�����������
				Dataset firstSucc=ver1.getdataset().getSuccessors().get(0);
				double[] temp=new double[m];
				double[] temp1=new double[m];
				double[] t=new double[m];
				//tempcost[i][0]Ϊ����cost��tempcost[i][1]Ϊ����cost��tempcost[i][2]Ϊ�õ����ݼ�����ʱ��=����ʱ��+����ʱ��
				double[][] tempcost=new double[m][3];
				double[][] tempcostt=new double[m][3];
				double[][] tempcost1=new double[m][3];
				if(ver1.getdataset().getName().equals("start"))//���ver1�ǳ�ʼ�������ݼ��������costΪ0
				{
					for(int i=0;i<m;i++)
					{
						temp[i]=CS[i].getcostC()*firstSucc.getGenerationTime();//һ�εļ������
						tempcost[i][0]=0;
						tempcost[i][1]=temp[i];
						tempcost[i][2]=firstSucc.getGenerationTime();
					}
				}
				else
				{
					for(int i=0;i<m;i++)
					{
						if(i==ver1.getcsid())//������ͬ��cloud�ϣ�����costΪ0
						{
							temp[i]=CS[i].getcostC()*firstSucc.getGenerationTime();
							tempcost[i][0]=0;
							tempcost[i][1]=temp[i];
							tempcost[i][2]=firstSucc.getGenerationTime();
						}
						else
						{
							temp[i]=CS[i].getcostC()*firstSucc.getGenerationTime()+ver1.getdataset().getSize()*CS[ver1.getcsid()].getcostT();
							tempcost[i][0]=ver1.getdataset().getSize()*CS[ver1.getcsid()].getcostT();
							tempcost[i][1]=CS[i].getcostC()*firstSucc.getGenerationTime();
							tempcost[i][2]=firstSucc.getGenerationTime()
											+(ver1.getdataset().getSize()/CS[ver1.getcsid()].getbandwiths()[i])/(60*24*60*1.0);
						}
					}
				}
				//minvaluenoΪfirstSucc�Ĳ���cloud
				//ͳ�Ʋ������ݼ�����Ϣ
				findmin(temp);
				geneDSnum[minvalueno]++;
				geneDS.get(minvalueno).add(firstSucc.getName());
				transcost=transcost+tempcost[minvalueno][0]*firstSucc.getUsageFrequency();
				computecost=computecost+tempcost[minvalueno][1]*firstSucc.getUsageFrequency();
				genetime=genetime+tempcost[minvalueno][2];			
			    //��ǰ�����ݼ�Ϊ��һ���м����ݼ��ĺ��
				Dataset currentSucc;
				if(firstSucc.equals(graph.getLastDataset()))
				{
					currentSucc=endver.getdataset();
				}
				else
				{
					currentSucc=firstSucc.getSuccessors().get(0);
				}
				Dataset oldSucc=firstSucc;
				while(!currentSucc.getName().equals(ver2.getdataset().getName()))//��currentSucc����ver2�����ж���Ϊfalse������ѭ��
				{			
					for(int i=0;i<m;i++)
					{
						for(int j=0;j<m;j++)
						{
							//����currentSuccһ�εĴ���,��j��i������������i�ϵļ������
							if(j==i)
							{
								t[j]=temp[j]+currentSucc.getGenerationTime()*CS[i].getcostC();
								tempcostt[j][0]=tempcost[j][0]+0;
								tempcostt[j][1]=tempcost[j][1]+currentSucc.getGenerationTime()*CS[i].getcostC();
								tempcostt[j][2]=tempcost[j][2]+currentSucc.getGenerationTime();
							}
							else
							{
								t[j]=temp[j]+oldSucc.getSize()*CS[j].getcostT()+currentSucc.getGenerationTime()*CS[i].getcostC();
								tempcostt[j][0]=tempcost[j][0]+oldSucc.getSize()*CS[j].getcostT();
								tempcostt[j][1]=tempcost[j][1]+currentSucc.getGenerationTime()*CS[i].getcostC();
								tempcostt[j][2]=tempcost[j][2]+currentSucc.getGenerationTime()
												+(oldSucc.getSize()/CS[j].getbandwiths()[i])/(60*24*60*1.0);
							}
						}
						temp1[i]=findmin(t);
						tempcost1[i][0]=tempcostt[minvalueno][0];
						tempcost1[i][1]=tempcostt[minvalueno][1];
						tempcost1[i][2]=tempcostt[minvalueno][2];
					}
					findmin(temp1);
					transcost=transcost+tempcost1[minvalueno][0]*currentSucc.getUsageFrequency();
					computecost=computecost+tempcost1[minvalueno][1]*currentSucc.getUsageFrequency();
					genetime=genetime+tempcost1[minvalueno][2];
					//ͳ�����²��������ݼ�����������Ϣ
					geneDSnum[minvalueno]++;
					geneDS.get(minvalueno).add(currentSucc.getName());
					if(currentSucc.getName().equals("end"))
					{
						break;
					}
					oldSucc=currentSucc;
					if(currentSucc.equals(graph.getLastDataset()))
					{
						currentSucc=endver.getdataset();
					}
					else
					{
						currentSucc=currentSucc.getSuccessors().get(0);
					}
					temp=temp1;
					tempcost=tempcost1;
				}
		   }
	     }
		System.out.println("the store cost is : "+storecost);
	    System.out.println("the transfer cost is : "+transcost);
	    System.out.println("the computation cost is :"+computecost);
		System.out.println("the total of three is "+(storecost+transcost+computecost));
		averageGenerateTime=genetime/graph.getDatasets().size();
	    System.out.println("the average generation time is : "+averageGenerateTime);
		//����ܹ��洢�˶������ݼ�
	    int storeTotalNum=0;
	    for(int i=0;i<m;i++)
	    {
		    storeTotalNum=storeTotalNum+storeDSnum[i];
	    }
	    System.out.println("the total number of dataset stored is : "+storeTotalNum);
	    System.out.println("the total number of dataset generated is : "+(graph.getDatasets().size()-storeTotalNum));
	    for(int i=0;i<m;i++)
	    {
			System.out.println("the number of dataset stored in cloud "+i+" is "+storeDSnum[i]);
			for(int j=0;j<storeDS.get(i).size();j++)
			{
			    System.out.println("the dataset stored in cloud "+i+" is "+storeDS.get(i).get(j));
			}
			System.out.println("the number of dataset generated in cloud "+i+" is "+geneDSnum[i]);
			for(int k=0;k<geneDS.get(i).size();k++)
			{
			    System.out.println("the dataset generated in cloud "+i+" is "+geneDS.get(i).get(k));
			}
	    }
		
	}
	// find the minimum value of a marry
	public static double findmin(double[] t)
	{
		double min=t[0];
		int j=0;
		for(int i=1;i<m;i++)
		{
			if(t[i]<min)
			{
				min=t[i];
				j=i;
			}
			minvalueno=j;
		}
		return min;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
