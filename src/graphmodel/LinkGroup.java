package graphmodel;

import graphelements.jgraphx.DrawComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates all the Sub Systems that are joined together through a single Link. It keeps as a list
 * all the difference interfaces of the Sub Systems that are part of the link, as well as the type of Link it is.
 * It is represented in the XML through the <Link> tag.
 * 
 * TODO: Revisit naming of Link and LinkGroup.
 * TODO: Add error checking for including Links with the same Sub System, and empty parameters
 * 
 * @author Chanisha Somatilaka, rsom024
 *
 */
public class LinkGroup extends Component {

	private List<Link> links;
	private LinkType type;
	
	public LinkGroup(LinkType type) {
		// This Link group isn't technically a SystemJ construct, just a grouping of them. So doesn't have its own name
		super("");
		links = new ArrayList<Link>();
		this.type = type;
	}
	
	/**
	 * Add a new Link interface to this group of Links.
	 * @param i - new Link interface to add in
	 */
	public void addLink(Link i) {
		links.add(i);
	}
	
	/**
	 * Retrieve the label for a given Link interface object, which is defined in its "name" field.
	 * @param index
	 * @return String for its label
	 */
	public String getLinkLabel(int index) {
		return links.get(index).getName();
	}
	
	
	public void removeLink(Link i) {
		links.remove(i);
	}
	
	/**
	 * This method returns a list of all the Sub Systems that are included in this Link group.
	 * @return
	 */
	public ArrayList<SubSystem> getInvolvedSubSystems() {
		ArrayList<SubSystem> subSystems = new ArrayList<SubSystem>();
		
		for (Link i : links) {
			subSystems.add(i.getSubSystem());
		}
		
		return subSystems;
		
	}
	
	
	@Override
	public String generateXML() {
		StringBuilder result = new StringBuilder("<Link Type=\"" + this.type.code + "\">\n");
		
		for (Link i : links) {
			result.append("            " + i.generateXML() + "\n");
		}
		result.append("        </Link>\n");
		return result.toString();		
	}
	
	/**
	 * This method checks to see if the passed in SubSystem object is contained in this group of Links.
	 * @param s - SubSystem object
	 * @return true if it is, false if not
	 */
	public boolean containsSubSystem(SubSystem s) {
		
		for (Link i : links) {
			if (i.hasSubSystem(s)) {
				return true;
			}
		}
		return false;
	}
	
	public LinkType getLinkType() {
		return this.type;
	}
	
	/**
	 * This method returns the list of Link interfaces contained within this particular Link Group.
	 * @return
	 */
	public List<Link> getLinks() {
		return links;
	}
	
	@Override
	public String getTypeAsString() {
		return DrawComponent.LINK_NAME;
	}
}
