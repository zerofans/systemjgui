package graphmodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is a Singleton instance representing the overall system model. All
 * the methods in the classes are protected, so they can only be accessed by
 * this GloablSystem instance. This ensures that there is only one instance of
 * GlobalSystem, and therefore only one object model that all the other views
 * must be consistent with.
 * 
 * The GlobalSystem instance contains a list of SubSystem objects, and list of
 * LinkGroup objects. Within each SubSystem object is a list of ClockDomain
 * objects, and inside every ClockDomain object, there are the corresponding
 * Signals are Channels. The call the generate the XML file is made at this
 * level, which nests all the XML generation for all the Components to maintain
 * the ordering of the file. This object can also create a new model given the
 * link to the XML file.
 * 
 * @author Chanisha Somatilaka, rsom024
 * 
 */
public class GlobalSystem {

	// Names of events for the listeners to this model
	public static final String MODEL_CHANGED_EVENT = "Model Changed";
	public static final String MODEL_CREATED_EVENT = "Model Created";
	public static final String MODEL_CLEARED_EVENT = "Model Cleared";

	private List<SubSystem> subSystems;
	private List<LinkGroup> links;

	// List of action listeners for changes in this model
	private List<ActionListener> listeners;

	// Keeps track of the single instance
	private static GlobalSystem system = null;

	public static GlobalSystem getInstance() {
		// Ensures that system is only instantiated once
		if (system == null) {
			system = new GlobalSystem();
		}
		return system;
	}

	/**
	 * Constructor is private so that it cannot be called outside this class, so
	 * controls the number of instancess
	 */
	private GlobalSystem() {
		subSystems = new ArrayList<SubSystem>();
		links = new ArrayList<LinkGroup>();

		SubSystem sub = new SubSystem("SS1", "", "");
		subSystems.add(sub);

		listeners = new ArrayList<ActionListener>();
	}

	// ADD/DELETE SUB SYSTEMS

	public void addSubSystem(SubSystem c) {
		subSystems.add(c);

		// A change is made, so notify all listeners
		notifyListeners(MODEL_CHANGED_EVENT);
	}

	public void deleteSubSystem(SubSystem c) {
		subSystems.remove(c);

		// A change is made, so notify all listeners
		notifyListeners(MODEL_CHANGED_EVENT);
	}

	// ADD/DELETE GROUPS OF LINKS

	public void addLinkGroup(LinkGroup l) {
		links.add(l);
		notifyListeners(MODEL_CHANGED_EVENT);
	}

	public void deleteLinkGroup(LinkGroup l) {
		links.remove(l);
		notifyListeners(MODEL_CHANGED_EVENT);
	}

	/**
	 * This method gets the list of link groups present in this system
	 * 
	 * @return the List of link groups
	 */
	public List<LinkGroup> getLinks() {
		return links;
	}

	/**
	 * This method generates the full XML String from the object model. First,
	 * it creates the XML concerning the Links, and then it iterates through
	 * each Sub System and appends the XML from each Clock Domain.
	 * 
	 * @return String containing full XML structure
	 */
	public String createXMLFile() {

		StringBuilder result = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<System>\n");
		result.append("    <Interconnection>\n");

		for (LinkGroup l : links) {
			result.append("        " + l.generateXML());
		}

		result.append("    </Interconnection>\n");

		for (SubSystem c : subSystems) {
			result.append("    " + c.generateXML());
		}
		result.append("</System>");
		return result.toString();

	}

	/**
	 * Add an ActionListener object that will listen for changes in this object
	 * model
	 * 
	 * @param l
	 *            - ActionListener object to add
	 */
	public void addListener(ActionListener l) {
		listeners.add(l);
	}

	/**
	 * This method supports editing within the model. If any change is made to
	 * the graphical representation of the model, then the new component that is
	 * created in its place will replace the old one.
	 * 
	 * TODO: Finish implementing for the rest of the Component types
	 * 
	 * @param oldComp
	 *            - Component that has been edited
	 * @param newComp
	 *            - Component it is to be replaced with
	 * 
	 *            Both parameters passed in must be of the same type: ie 2 Clock
	 *            Domains, 2 Sub Systems, 2 Channels, etc
	 */
	public void changeComponent(Component oldComp, Component newComp) {

		if (oldComp instanceof ClockDomain) {
			if (!(newComp instanceof ClockDomain)) {
				throw new RuntimeException(
						"Change Component: Components must be of the same type");
			}
			ClockDomain oldCD = (ClockDomain) oldComp;
			ClockDomain newCD = (ClockDomain) newComp;

			for (SubSystem s : subSystems) {
				s.replaceClockDomain(oldCD, newCD);
			}
		} else if (oldComp instanceof SubSystem) {
			if (!(newComp instanceof SubSystem)) {
				throw new RuntimeException(
						"Change Component: Components must be of the same type");
			}

			SubSystem oldSub = (SubSystem) oldComp;
			SubSystem newSub = (SubSystem) newComp;

			int index = subSystems.indexOf(oldSub);
			subSystems.set(index, newSub);
		} else if (newComp instanceof Channel) {
			if (!(newComp instanceof Channel)) {
				throw new RuntimeException(
						"Change Component: Components must be of the same type");
			}

			// TODO: Finish implementation.
		}

		// When edit it made, notify the listeners
		notifyListeners(MODEL_CHANGED_EVENT);
	}

	/**
	 * This method notifies all registered listeners that a change has occurred
	 * within the model
	 * 
	 * @param command
	 *            - MODEL_CHANGED_EVENT or MODEL_CREATED_EVENT. Constants
	 *            present within Global System
	 */
	public void notifyListeners(String command) {
		for (ActionListener l : listeners) {
			l.actionPerformed(new ActionEvent(this, 0, command));
		}
	}

	/**
	 * This method calculates the number of Links a given Sub System has
	 * 
	 * @param s
	 *            - Sub System in question
	 * @return the number of Links present in this Sub System
	 */
	public int numberOfLinks(SubSystem s) {
		int numLinks = 0;
		for (LinkGroup l : links) {
			if (l.containsSubSystem(s)) {
				numLinks++;
			}
		}
		return numLinks;
	}

	/**
	 * This method returns the list of Sub Systems currently present in the
	 * model
	 * 
	 * @return
	 */
	public List<SubSystem> getSubSystems() {
		return subSystems;
	}

	/**
	 * This method iterates through all the contained Sub Systems and returns a
	 * list of all contained Clock Domains in the System
	 * 
	 * @return List of all Clock Domains in the system model
	 */
	public List<ClockDomain> getAllClockDomains() {
		List<ClockDomain> output = new ArrayList<ClockDomain>();

		for (SubSystem s : subSystems) {
			// Add all the Clock Domains in this Sub System to the list
			output.addAll(s.getClockDomains());
		}

		return output;

	}

	/**
	 * This method provides a public interface for classes outside this package
	 * to add a new Clock Domain to the system
	 * 
	 * @param cd
	 *            - Clock Domain to add
	 * @param sub
	 *            - Sub System that contains this Clock Domain
	 */
	public void addClockDomain(ClockDomain cd, SubSystem sub) {
		if (subSystems.contains(sub)) {
			sub.addClockDomain(cd);
		}

		notifyListeners(MODEL_CHANGED_EVENT);
	}
	
	public void removeClockDomain(ClockDomain cd){
		List<SubSystem> Sublist = getSubSystems(); 
		for (SubSystem tempsub : Sublist){
			tempsub.removeClockDomain(cd);	
		}
		notifyListeners(MODEL_CHANGED_EVENT);
	}
	

	/**
	 * This method provides a public interface for classes outside this package
	 * to add a new Signal to the system
	 * 
	 * @param s
	 *            - Signal to add
	 * @param cd
	 *            - Clock Domain that contains this Signal
	 */
	public void addSignal(Signal s, ClockDomain cd) {
		for (SubSystem sub : subSystems) {
			if (sub.containsClockDomain(cd)) {
				// Add this Signal to the corresponding list according to its
				// type
				if (s.getType().equals(IOType.INPUT)) {
					cd.addInputSignal(s);
				} else {
					cd.addOutputSignal(s);
				}

				notifyListeners(MODEL_CHANGED_EVENT);
				// Once the correct Clock Domain is found, and the Signal added,
				// there is no need to continue iterating through the Sub
				// Systems
				return;

			}
		}
	}

	/**
	 * This method provides a public interface for classes outside this package
	 * to add a new Channel to the system
	 * 
	 * @param ch
	 *            - Channel object to add to the system
	 * @param cdFrom
	 *            - Clock Domain that the Channel is coming from
	 * @param cdTo
	 *            - Clock Domain that the Channel is going to
	 * @param fromName
	 *            - the name of this Channel on the source Clock Domain
	 * @param toName
	 *            - the name of this Channel on the destination Clock Domain
	 */
	public void addChannel(Channel ch, ClockDomain cdFrom, ClockDomain cdTo,
			String fromName, String toName) {

		// Isn't set in the CreateComponent popup as it does not have access to
		// the Clock Domains the channel is connected to. So must be set here.
		ch.setFromClockDomain(cdFrom);
		ch.setToClockDomain(cdTo);

		// Add references to the Channel for the corresponding Clock Domains
		cdFrom.addOutputChannel(ch);
		cdTo.addInputChannel(ch);

		notifyListeners(MODEL_CHANGED_EVENT);
	}

	/**
	 * This method clears the existing model. Namely, emptying the list of Sub
	 * Systems and Links
	 */
	public void clearModel() {
		subSystems.clear();
		links.clear();

		notifyListeners(MODEL_CLEARED_EVENT);
	}

	/**
	 * This method constructs an object model of the SystemJ system provided a
	 * SystemJ XML configuration file. The resulting model will be saved as the
	 * new instance for the GlobalSystem
	 * 
	 * @param file
	 *            - the File object representing the SystemJ XML configuration
	 *            file
	 */
	public void constructModel(File file) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;

		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			doc = db.parse(file);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// clear model first before reconstructing
		system.clearModel();

		Element rootNode = doc.getDocumentElement();
		// Retrieve the Sub System XML nodes
		NodeList subSystems = rootNode.getElementsByTagName("SubSystem");

		// Iterate through all the Sub System XML tags
		for (int i = 0; i < subSystems.getLength(); i++) {

			
			
			
			Element el = (Element) subSystems.item(i);

			// Retrieve the <Name> and <Scheduler> Tags from the Sub System XML
			// tag
			String name = el.getAttribute("Name");
			String local = el.getAttribute("Local");
			NodeList scheduler = el.getElementsByTagName("Scheduler");
			String schedulerClass = "";
			// the <Scheduler> tag is optional, so if it does exist, then
			// retrieve the corresponding class from it.
			if (scheduler.getLength() == 1) {
				Element e = (Element) scheduler.item(0);
				schedulerClass = e.getAttribute("Class");

				// If a <Scheduler> tag is present, then all Clock Domains are
				// now enclosed in this tag. So make it the new node that is
				// referred to throughout to get the
				// Clock Domain information
				el = e;
			}

			// Create a new SubSystem object with the name and Scheduler class
			// information
			SubSystem sub = new SubSystem(name, schedulerClass, local);

			
			// Once all the Clock Domains have been added, then add this Sub
						// System to the overall system and move onto the next Sub System
						system.addSubSystem(sub);
			
			// Get a list of the contained Clock Domain XML nodes
			List<Node> clockDomains = getNodesAsElementList(el.getChildNodes());

			// Iterate through all the nodes
			for (Node n : clockDomains) {
				Element el2 = (Element) n;
				name = el2.getAttribute("Name");
				String className = el2.getAttribute("Class");

				// Create a new Clock Domain with the extracted Name and Class
				// fields
				ClockDomain cd = new ClockDomain(name, className);

				// Add in input and output signals
				List<Node> inputSignals = getNodesAsElementList(el2
						.getElementsByTagName("iSignal"));
				List<Node> outputSignals = getNodesAsElementList(el2
						.getElementsByTagName("oSignal"));

				for (Node sig : inputSignals) {
					NamedNodeMap map = sig.getAttributes();
					Hashtable<String, String> sigParameters = new Hashtable<String, String>();

					// Iterate through all the parameters found in the XML file,
					// and add them to the Hashtable
					for (int index = 0; index < map.getLength(); index++) {
						Node param = map.item(index);
						if (param.getNodeName().equals("Name")) {
							name = param.getNodeValue();
						} else if (param.getNodeName().equals("Class")) {
							className = param.getNodeValue();
						} else {
							sigParameters.put(param.getNodeName(),
									param.getNodeValue());
						}
					}

					// Once all the parameters have been retrieved, create the
					// Signal object, and add it to the current Clock Domain
					Signal inputSig = new Signal(name, className, IOType.INPUT,
							sigParameters);
					cd.addInputSignal(inputSig);
				}

				// Do the same for the output Signals
				for (Node sig : outputSignals) {
					NamedNodeMap map = sig.getAttributes();
					Hashtable<String, String> sigParameters = new Hashtable<String, String>();

					for (int index = 0; index < map.getLength(); index++) {
						Node param = map.item(index);
						if (param.getNodeName().equals("Name")) {
							name = param.getNodeValue();
						} else if (param.getNodeName().equals("Class")) {
							className = param.getNodeValue();
						} else {
							sigParameters.put(param.getNodeName(),
									param.getNodeValue());
						}
					}
					Signal outputSig = new Signal(name, className,
							IOType.OUTPUT, sigParameters);
					cd.addOutputSignal(outputSig);
				}

				// Once all the Signals have been added to the Clock Domain, add
				// this Clock Domain to the current Sub System
				
				//addClockDomain(cd,sub);
				sub.addClockDomain(cd);
			}


		}

		// Links and Channels are added after all Sub Systems and Clock Domains
		// have been created

		// Retrieve all the Clock Domains present in the whole system
		List<ClockDomain> allClockDomains = new ArrayList<ClockDomain>();
		for (SubSystem s : this.subSystems) {
			allClockDomains.addAll(s.getClockDomains());
		}

		// Using the Clock Domain nodes, get all the output Channels. This
		// provides all information for both sides of the connection.
		List<Node> clockDomainNodes = getNodesAsElementList(rootNode
				.getElementsByTagName("ClockDomain"));
		for (Node n : clockDomainNodes) {
			Element el = (Element) n;
			// Get all output Channels from this Clock Domain node
			List<Node> channelNodes = getNodesAsElementList(el
					.getElementsByTagName("oChannel"));
			String cdName = el.getAttribute("Name");
			ClockDomain fromCD = null;

			// Retrieve the corresponding Clock Domain object based on the name
			// of the Clock Domain found in the XML node
			// This will be the source Clock Domain
			for (SubSystem s : this.subSystems) {
				if (s.getClockDomain(cdName) != null) {
					fromCD = s.getClockDomain(cdName);
					break;
				}
			}

			for (Node n2 : channelNodes) {
				Element e = (Element) n2;
				String fromName = e.getAttribute("Name");
				// Destination clock domain and Channel name are in the format
				// ClockDomain.ChannelName, so split into two to get info
				String toClockDomainName = e.getAttribute("To").split("\\.")[0];
				String toChannelName = e.getAttribute("To").split("\\.")[1];
				ClockDomain toCD = null;

				// Retrieve the destination ClockDomain object based on the name
				// provided
				for (SubSystem s : this.subSystems) {
					if (s.getClockDomain(toClockDomainName) != null) {
						toCD = s.getClockDomain(toClockDomainName);
						break;
					}
				}

				// If both Clock Domain names in the XML provide link to
				// existing Clock Domains, then create the Channel object, and
				// add it to both Clock Domains
				if (toCD != null && fromCD != null) {
					Channel ch = new Channel(fromName, toChannelName, fromCD,
							toCD);
					fromCD.addOutputChannel(ch);
					toCD.addInputChannel(ch);
				}

			}

		}

		// Construct Links

		// All items in the <Link> tag correspond to a group of related links,
		// or communicating Sub Systems
		NodeList links = rootNode.getElementsByTagName("Link");

		for (int i = 0; i < links.getLength(); i++) {
			Element l = (Element) links.item(i);
			LinkGroup link = new LinkGroup(LinkType.getLinkType(l
					.getAttribute("Type")));

			NodeList interfaces = l.getElementsByTagName("Interface");

			// Iterate through all Link Interface nodes and create the
			// corresponding Link object
			for (int index = 0; index < interfaces.getLength(); index++) {
				Element n = (Element) interfaces.item(index);
				SubSystem sub = getSubSystem(n.getAttribute("SubSystem"));
				String className = n.getAttribute("Class");
				String physInterface = n.getAttribute("Interface");
				String args = n.getAttribute("Args");

				Link linkInterface = new Link(sub, className, physInterface,
						args);
				link.addLink(linkInterface);
			}

			this.links.add(link);
		}

		// Once the model is created, notify all the listeners
		notifyListeners(MODEL_CREATED_EVENT);

	}

	/**
	 * For Document parsing, the list of nodes is generated as a NodeList
	 * object. For easier iteration, this NodeList object is then parsed into a
	 * list of Node objects, so can then iterate throughout them with a for loop
	 * In addition, sometimes the NodeList object contains blank #text fields
	 * that need to be skipped to retrieve the proper objects relating to the
	 * file.
	 * 
	 * @param nodes
	 *            - NodeList object representing the list of nodes to parse
	 * @return List of Node objects representing the XML nodes
	 */
	private List<Node> getNodesAsElementList(NodeList nodes) {

		List<Node> actualNodes = new ArrayList<Node>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);

			// Nodes will just have blank spaces, so skip these
			if (n.getNodeName().equals("#text")) {
				continue;
			}
			actualNodes.add(n);
		}

		return actualNodes;
	}

	/**
	 * This method returns the SubSystem object given the String name of the Sub
	 * System
	 * 
	 * @param name
	 *            - name of the Sub System
	 * @return SubSystem with the given name, null if no SubSystem with this
	 *         name exists
	 */
	public SubSystem getSubSystem(String name) {
		for (SubSystem s : subSystems) {
			if (s.name.equals(name)) {
				return s;
			}
		}
		return null;
	}

}
