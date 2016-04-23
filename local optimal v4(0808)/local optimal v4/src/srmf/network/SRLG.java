package srmf.network;

import java.util.ArrayList;

public class SRLG {
	public int srlgID;

	public ArrayList<Link> links = new ArrayList<>();

	public SRLG() {
		// TODO Auto-generated constructor stub
	}

	public SRLG(int id) {
		srlgID = id;
	}

	public SRLG(int id, ArrayList<Link> links) {
		srlgID = id;
		this.links = links;
	}

	public int getSrlgID() {
		return srlgID;
	}

	public void setSrlgID(int srlgID) {
		this.srlgID = srlgID;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

	public void addLinks(Link link) {
		links.add(link);
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("srlgID:" + srlgID + "->{");
		for (int i = 0; i < links.size() - 1; i++)
			stringBuffer.append(links.get(i) + ",");
		if ((links.size() - 1) >= 0)
			stringBuffer.append(links.get(links.size() - 1) + "}");
		return stringBuffer.toString();
	}

}
