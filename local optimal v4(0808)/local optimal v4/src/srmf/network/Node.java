package srmf.network;

import java.util.ArrayList;

public class Node {
	public int nodeID;
	public ArrayList<Link> ajacent_links = new ArrayList<Link>();

	public Node() {
		// TODO Auto-generated constructor stub
	}

	public Node(int id) {
		this.nodeID = id;
	}

	public void addAjacent_link(Link link) {
		ajacent_links.add(link);
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public ArrayList<Link> getAjacent_links() {
		return ajacent_links;
	}

	public void setAjacent_links(ArrayList<Link> ajacent_links) {
		this.ajacent_links = ajacent_links;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Node:" + nodeID);
		stringBuffer.append("->{");
		for (int i = 0; i < ajacent_links.size() - 1; i++)
			stringBuffer.append(ajacent_links.get(i) + ",");
		if ((ajacent_links.size() - 1) >= 0)
			stringBuffer.append(ajacent_links.get(ajacent_links.size() - 1));
		stringBuffer.append("}");
		return stringBuffer.toString();
	}
}
