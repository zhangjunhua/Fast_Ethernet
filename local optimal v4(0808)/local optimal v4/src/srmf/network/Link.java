package srmf.network;

import java.util.ArrayList;

/**
 * @author Admin
 *
 */
public class Link {
	public int linkID;
	public Node[] endNodes = new Node[2];
	public ArrayList<SRLG> srlgs=new ArrayList<>();
	
	public Link() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 */
	public Link(int id) {
		this.linkID = id;
	}

	/**
	 * @param node1
	 * @param node2
	 */
	public void linkNodes(Node node1, Node node2) {
		endNodes[0] = node1;
		endNodes[1] = node2;
	}

	/**
	 * @return
	 */
	public int getLinkID() {
		return linkID;
	}

	/**
	 * @param linkID
	 */
	public void setLinkID(int linkID) {
		this.linkID = linkID;
	}

	/**
	 * @return
	 */
	public Node[] getEndNodes() {
		return endNodes;
	}

	/**
	 * 
	 * @param endNodes
	 */
	public void setEndNodes(Node[] endNodes) {
		this.endNodes = endNodes;
	}
	
	

	/**
	 * @return
	 */
	public ArrayList<SRLG> getSrlgs() {
		return srlgs;
	}

	/**
	 * @param srlgs
	 */
	public void setSrlgs(ArrayList<SRLG> srlgs) {
		this.srlgs = srlgs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("link:" + linkID + " {" + endNodes[0].nodeID + ","
				+ endNodes[1].nodeID + "}");
		return stringBuffer.toString();
	}

}
