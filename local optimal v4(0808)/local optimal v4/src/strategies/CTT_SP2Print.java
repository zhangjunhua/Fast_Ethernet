package strategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import newClass.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import utilities.DataDependencyGraph;
import utilities.Dataset;

public class CTT_SP2Print {

	/**
	 * �����
	 * 1.�洢�ڸ���cloud�ϵ����ݼ��ĸ�������������ݼ�
	 * 2.���ƽ������ʱ��
	 * 3.�������cost���洢cost������cost
	 * 
	 */
	public static void print(List<DefaultWeightedEdge> shortestPath,int m,SimpleDirectedWeightedGraph<CTT_ver, DefaultWeightedEdge> cttGraph,CloudService[] CS,DataDependencyGraph graph)
    {
    	int[] storeDSnum=new int[m];
    	ArrayList<ArrayList<String>> storeDS=new ArrayList<ArrayList<String>>();
    	for(int i=0;i<m;i++)
    	{
    		storeDS.add(new ArrayList<String>());
    	}
    	double averageGenerateTime=0.0;
		// give the storage strategy according to the shortest path--------------------
		CTT_ver ver1;
		CTT_ver ver2;
		Dataset currentinterDS;
		double temptime = 0.0;
		double genetime=0.0;
		CTT_ver endver = new CTT_ver(); // end node
		endver.getdataset().setName("end");
		endver.setcsid(-1);
		double[] bandwiths=CS[0].getbandwiths();
		for (DefaultWeightedEdge anEdge : shortestPath) {
			ver1 = cttGraph.getEdgeSource(anEdge);
			ver2 = cttGraph.getEdgeTarget(anEdge);
			ver1.getdataset().setStored(true);
			ver1.getdataset().setcsid(ver1.getcsid());
			
			if(ver1.getdataset().getName()!="start")
			{
				storeDSnum[ver1.getcsid()]++;
				storeDS.get(ver1.getcsid()).add(ver1.getdataset().getName());
				//ver1��Ӧ���ݼ��Ĵ���ʱ��
				if(bandwiths[ver1.getcsid()]!=0)
				{
					genetime=genetime+(ver1.getdataset().getSize()/bandwiths[ver1.getcsid()])/(60*60*24);
				}
			}
			//�õ�ver1�ĺ�����ݼ�
			if(ver1.getdataset().getName().equals("start"))
			{
				currentinterDS=graph.getFirstDataset();
			}
			else
			{
				if(ver1.getdataset().equals(graph.getLastDataset()))
				{
					currentinterDS=endver.getdataset();
				}
				else
				{
					currentinterDS=ver1.getdataset().getSuccessors().get(0);
				}
			}
			//�����м����ݼ��Ĳ���ʱ��
			if(!currentinterDS.getName().equals(ver2.getdataset().getName()))
			{
				
				if(bandwiths[ver1.getcsid()]==0)
				{
					temptime=0;//������Ϊ0�����ʾver1�Ĵ���ʱ��Ϊ0
				}
				else
				{
					temptime=(ver1.getdataset().getSize()/bandwiths[ver1.getcsid()])/(60*60*24);//this is the transfer time
				}
				while(!currentinterDS.equals(ver2.getdataset()))
				{
					temptime=temptime+currentinterDS.getGenerationTime();
					genetime=genetime+temptime;
					if(currentinterDS.equals(graph.getLastDataset()))
					{
						currentinterDS=endver.getdataset();
					}
					else
					{
						currentinterDS=currentinterDS.getSuccessors().get(0);
					}
				}
			}
		}
	    List<Dataset> Datasets=graph.getDatasets();
	    double storagecost = 0.0;
	    double tempstorecost=0;
		double transcost=0.0;
		double temptranscost=0;
		double computecost = 0.0;
		double tempcomputecost=0;
		int csid=0;
	    for(Dataset aDataset:Datasets)
	    {
			if (aDataset.isStored()) 
			{
				csid=aDataset.getcsid();
				//aDataset�洢��cloud �ϵĴ洢����
				tempstorecost = aDataset.getSize()*CS[csid].getcostS();
				//��aDataset�������صĴ������
				temptranscost=CS[csid].getcostT()*aDataset.getSize()*aDataset.getUsageFrequency();
				storagecost=storagecost+tempstorecost;
				transcost=transcost+temptranscost;
			} 
			else 
			{
				//������зǴ洢���ݼ��Ĳ�������
				tempcomputecost = aDataset.getGenerationTime() * CS[0].getcostC()* aDataset.getUsageFrequency();
				Set<Dataset> pSet = getPSet(aDataset);
				for (Dataset pSetDS : pSet) 
				{
					tempcomputecost = tempcomputecost+pSetDS.getGenerationTime() * CS[0].getcostC()
							* aDataset.getUsageFrequency();
				}
				Dataset firststoreDS = getStorefirstSet(aDataset,graph);
				// the transcost first store dataset 
				if(firststoreDS==null)
				{
					temptranscost=0;
				}
				else
				{
					csid=firststoreDS.getcsid();
					temptranscost= CS[csid].getcostT()*firststoreDS.getSize()*aDataset.getUsageFrequency();
				}
				computecost=computecost+tempcomputecost;
				transcost=transcost+temptranscost;
			}
			tempstorecost=0;
			tempcomputecost=0;
			temptranscost=0;
			
	    }
	    System.out.println("the storage cost:"+storagecost);
	    System.out.println("the transfer cost:"+transcost);
	    System.out.println("the computation cost:"+computecost);
	    System.out.println("the total of three is "+(storagecost+transcost+computecost));
		averageGenerateTime = genetime / graph.getDatasets().size();
		System.out.println("the average generation time is "+averageGenerateTime);
		int storeNum=0;
		for(int i=0;i<m;i++)
		{
			storeNum=storeNum+storeDSnum[i];
		}
		System.out.println("the number of dataset stored is : "+storeNum);
		System.out.println("the number of dataset generated in local is : "+(graph.getDatasets().size()-storeNum));
		//����洢��cloud0�ϵ����ݼ��ĸ������Լ�����Щ��������ݼ�
		for(int i=0;i<m;i++)
		{
			System.out.println("the number of dataset stored in cloud service "+i+" is "+storeDSnum[i]);
			for(int a0=0;a0<storeDS.get(i).size();a0++)
		    {
		    	System.out.println("the datasets stored in cloud service "+i+" is "+storeDS.get(i).get(a0));
		    }
		}
    }
	//get all datasets that are not saved before aDataset
	private static Set<Dataset> getPSet(Dataset aDataset) 
	{
		Set<Dataset> pSet = new HashSet<Dataset>();
		List<Dataset> predecessors = aDataset.getPredecessors();
		for (Dataset predecessor : predecessors) 
		{
			if (!predecessor.isStored()) 
			{
				pSet.add(predecessor);
				pSet.addAll(getPSet(predecessor));
			}
		}
		return pSet;
	}
	//get the first dataset that is saved before aDataset; aDataset.getPredecessors() only can get the direct predecessors
	private static Dataset getStorefirstSet(Dataset aDataset,DataDependencyGraph graph) 
	{
		Dataset firststoreDS=null;
		List<Dataset> datasets=graph.getDatasets();
		int di=0;
		Dataset bDataset=datasets.get(0);
		while(bDataset!=aDataset)
		{
			if(bDataset.isStored())
			{
				firststoreDS=bDataset;
			}
			di=di+1;
			bDataset=datasets.get(di);
		}
		return firststoreDS;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
