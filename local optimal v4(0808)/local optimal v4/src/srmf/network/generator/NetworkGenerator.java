package srmf.network.generator;

import java.util.Random;

import org.junit.Test;

import srmf.global.Constant;
import srmf.network.Link;
import srmf.network.Network;
import srmf.network.Node;
import srmf.network.SRLG;

public class NetworkGenerator {

	public static Node[] generateNodes() {
		Node[] nodes = new Node[Constant.GraphProperty.n];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new Node(i);
		}
		return nodes;
	}

	/*
	 * 生成link的方法：随机选择source_id和destination_id。然后去重
	 */
	public static Link[] generateLinks(Node[] nodes) {
		int m = Constant.GraphProperty.avg_deg * Constant.GraphProperty.n / 2;
		Constant.GraphProperty.m = m;
		Link[] links = new Link[m];
		Random random = new Random();
		for (int i = 0; i < m; i++) {
			links[i] = new Link(i);

			int a = random.nextInt(Constant.GraphProperty.n);
			int b = random.nextInt(Constant.GraphProperty.n);
			while (a == b) {
				b = random.nextInt(Constant.GraphProperty.n);
			}
			while (true) {
				boolean unique = true;
				for (int k = 0; k < i; k++) {
					if (links[k].endNodes[0].nodeID == a
							&& links[k].endNodes[1].nodeID == b) {
						unique = false;
						break;
					}
					if (links[k].endNodes[0].nodeID == b
							&& links[k].endNodes[1].nodeID == a) {
						unique = false;
						break;
					}
				}
				if (unique)
					break;
				else {
					a = random.nextInt(Constant.GraphProperty.n);
					b = random.nextInt(Constant.GraphProperty.n);
					while (a == b) {
						b = random.nextInt(Constant.GraphProperty.n);
					}
				}
			}
			links[i].linkNodes(nodes[a], nodes[b]);
			nodes[a].addAjacent_link(links[i]);
			nodes[b].addAjacent_link(links[i]);
		}
		return links;
	}

	/*
	 * 生成SRGLs的算法： 首先随机分配每个srlg的link的个数, 其次将每个link分配一次， 最后把他们补满. 最后去重
	 */
	public static SRLG[] generateSRLGs(Network network) {
		if (Constant.SrlgProperty.avg_link * 2 > Constant.GraphProperty.m) {
			throw new RuntimeException(
					"Constant.SrlgProperty.avg_link*2>Constant.GraphProperty.m");
		}
		Random random = new Random();

		SRLG[] srlgs = new SRLG[Constant.SrlgProperty.getSrlg_num()];
		int[] SRLGsize = new int[srlgs.length];
		for (int i = 0; i < srlgs.length; i++) {
			srlgs[i] = new SRLG(i);
		}
		int sum=0;
		while (sum<network.getLinks().length) {
			sum=0;
			for (int i = 0; i < srlgs.length; i++) {
				SRLGsize[i] = random
						.nextInt(2 * Constant.SrlgProperty.avg_link - 1) + 1;
				sum+=SRLGsize[i];
			}
			
		}
//		for(int i=0;i<SRLGsize.length;i++)
//			System.out.print(SRLGsize[i]+" ");
//		System.out.println();
		/*
		 * 将每个link分配一次
		 */
		for (int i = 0; i < network.getLinks().length; i++) {
			// System.out.println(srlgs.length+" "+Constant.SrlgProperty.getSrlg_num());
			int index = random.nextInt(srlgs.length);
			while (SRLGsize[index] == srlgs[index].getLinks().size()) {
				index = random.nextInt(srlgs.length);
			}
			srlgs[index].addLinks(network.getLinks()[i]);
		}
		/*
		 * 补满
		 */
		for (int i = 0; i < srlgs.length; i++) {
			while (srlgs[i].getLinks().size() < SRLGsize[i]) {
				int index = random.nextInt(network.getLinks().length);
				while (srlgs[i].getLinks().contains(network.getLinks()[index]))
					index = random.nextInt(network.getLinks().length);
				srlgs[i].addLinks(network.getLinks()[index]);
			}
		}
		/*
		 * 去重
		 */
		while (true) {
			boolean unique = true;
			out: for (int i = 0; i < srlgs.length; i++) {
				for (int j = i + 1; j < srlgs.length; j++) {
					if (srlgs[i].getLinks().size() == srlgs[j].getLinks()
							.size()) {
						if (srlgs[i].getLinks()
								.containsAll(srlgs[j].getLinks())) {
							unique = false;
							srlgs[i].getLinks().clear();
							while (srlgs[i].getLinks().size() < SRLGsize[i]) {
								int index = random
										.nextInt(network.getLinks().length);
								while (srlgs[i].getLinks().contains(
										network.getLinks()[index]))
									index = random
											.nextInt(network.getLinks().length);
								srlgs[i].addLinks(network.getLinks()[index]);
							}
							break out;
						}
					}
				}
			}
			if (unique)
				break;
		}
		invert_index(network, srlgs);
		return srlgs;
	}
	
	public static void invert_index(Network network,SRLG[] srlgs){
		
	}

	public static Network generateNetwork() {
		Node[] nodes = generateNodes();
		Link[] links = generateLinks(nodes);
		return new Network(nodes, links);
	}

	@Test
	public void test1() {
		Random random = new Random();
		for (int i = 0; i < 20; i++)
			System.out.println(random.nextInt(10));
	}

	@Test
	public void test2() {
		Constant.GraphProperty.n = 5;
		Constant.GraphProperty.avg_deg = 2;
		System.out.println(generateNetwork());
	}

	@Test
	public void test3() {
		Constant.GraphProperty.n = 5;
		Constant.GraphProperty.avg_deg = 2;
		Constant.SrlgProperty.avg_link = 2;
		Constant.SrlgProperty.srlg_num = 3;
		Network network = generateNetwork();
		System.out.println(network);
		SRLG[] srlgs = generateSRLGs(network);
		for (int i = 0; i < srlgs.length; i++)
			System.out.println(srlgs[i]);
	}
	
	@Test
	public void test4(){
		for(int i=0;i<100;i++)
			test3();
	}

}
