package org.com.home;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLManager {
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	Transformer transformer;
	File filePath;
	
	public XMLManager(File filePath) {
		this.filePath = filePath;
		this.factory = DocumentBuilderFactory.newInstance();
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			this.transformer = transformerFactory.newTransformer();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void createXML(String nameRoot) {
		try {			
			if(!filePath.exists()) {
				builder = factory.newDocumentBuilder();
				
				Document doc = builder.newDocument();
							
				doc.appendChild(doc.createElement(nameRoot));
				
				DOMSource source = new DOMSource(doc);
				
				StreamResult file = new StreamResult(filePath);
				
				transformer.transform(source, file);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void buildElement(String elementName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filePath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
			
			Element element = doc.createElement(elementName);
			
			root.appendChild(element);
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filePath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void addElement(String elementName, String parentName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filePath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
			
			Element childElement = doc.createElement(elementName);
			
			NodeList nodes = root.getElementsByTagName(parentName);
			Element parentElement = (Element) nodes.item(nodes.getLength() - 1);
			
			parentElement.appendChild(childElement);
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filePath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void addNode(NodeSync nodeSync, String childName, String parentName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filePath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
			
			Element childElement = (Element) getNode(doc, childName, nodeSync);
			
			NodeList nodes = root.getElementsByTagName(parentName);
			Element parentElement = (Element) nodes.item(nodes.getLength() - 1);
			
			parentElement.appendChild(childElement);
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filePath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void removeNode(NodeSync nodeSync, String nodeName, String parentName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filePath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
												
			Element nodes = (Element) root.getElementsByTagName(parentName).item(0);
						
			Node removeNode = findNode(nodes.getElementsByTagName(nodeName), nodeSync);
						
			if(removeNode != null)
				((Element) nodes).removeChild(removeNode);
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filePath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void editNode(NodeSync oldNodeSync, NodeSync newNodeSync, String nodeName, String parentName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filePath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
									
			Element nodes = (Element) root.getElementsByTagName(parentName).item(0);
						
			Node oldNode = findNode(nodes.getElementsByTagName(nodeName), oldNodeSync);
			
			if(oldNode != null) {
				Node newNode = getNode(doc, nodeName, newNodeSync);
				((Element) nodes).replaceChild(newNode, oldNode);
			}
				
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filePath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void saveXML(File toPath) {
		try {
			Files.copy(filePath.toPath(), toPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void loadXML(File fromPath) {
		try {
			builder = factory.newDocumentBuilder();
			Document docOpen = builder.parse(fromPath);
			Document doc = builder.parse(filePath);
			
			NodeList rootOpenNodes = docOpen.getFirstChild().getChildNodes();
			NodeList rootNodes = doc.getFirstChild().getChildNodes(); //DataBase
			
			for(int i = 0; i < rootNodes.getLength(); i++) {
				Element catalogueOpen = (Element) rootOpenNodes.item(i);
				Element catalogue = (Element) rootNodes.item(i); //Records
					
				NodeList intoCatalogueOpen = catalogueOpen.getChildNodes();
				int intoCatalogueOpenLength = intoCatalogueOpen.getLength(); //Every record
				
				for(int j = 0; j < intoCatalogueOpenLength; j++) {
					Node node = intoCatalogueOpen.item(j);
					
					catalogue.appendChild(getNode(doc, node));
				}
			}
				
			DOMSource source = new DOMSource(doc);
								
			StreamResult file = new StreamResult(filePath);
				
			transformer.transform(source, file);
//				
//			System.out.println("XML is created");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public List<String[]> getListOf(String nodeListName, String nodeNames, NodeSync node) {
		List<String[]> resultList = new ArrayList<String[]>();
		
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filePath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
			
			Element nodes = (Element) root.getElementsByTagName(nodeListName).item(0);
			
			List<String> keys = node.getKeys();
			
			NodeList childs = nodes.getElementsByTagName(nodeNames);
			
			for(int i = 0; i < childs.getLength(); i++) {
				String[] tmpArray = new String[keys.size()];
				
				NodeList fields = childs.item(i).getChildNodes();
				
				for(int j = 0; j < keys.size(); j++) {
					tmpArray[j] = fields.item(j).getTextContent();
				}
				
				resultList.add(tmpArray);
			}
			
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return resultList;
	}
	
	public void clear() {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filePath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
												
			doc.removeChild(root);
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filePath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void clear(String parentName) {
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filePath);
			doc.getDocumentElement().normalize();
			
			Element root = (Element) doc.getFirstChild();
												
			Element node = (Element) root.getElementsByTagName(parentName).item(0);
			
			NodeList childs = node.getChildNodes();
			
			int length = childs.getLength();
			
			for(int i = 0; i < length; i++) {
				node.removeChild(childs.item(0));
			}
			
			DOMSource source = new DOMSource(doc);
			
			StreamResult file = new StreamResult(filePath);
			
			transformer.transform(source, file);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private Node findNode(NodeList nodes, NodeSync nodeSync) {
		Node foundNode = null;
		
		String idKey = nodeSync.getIds().get(0);
		String idValue = nodeSync.getIds().get(1);
		
//		System.out.println( nodes.getLength());
				
		for(int i = 0; i < nodes.getLength(); i++) {
			Element node = (Element) nodes.item(i);
			
//			System.out.println("Date node >> " + node.getElementsByTagName(idKey).item(0).getTextContent());
//			System.out.println("Id value >> " + idValue);
			
			if(node.getElementsByTagName(idKey).item(0).getTextContent().equals(idValue)) {
				foundNode = node;
				break;
			}
		}
		
		return foundNode;
	}
	
	private Node getNode(Document doc, String nodeName, NodeSync nodeSync) {
		Element node = doc.createElement(nodeName);
		
		nodeSync.getMapElements().forEach((k, v) -> {
			node.appendChild(getElement(doc, k, v));
		});
				
		return node;
	}
	
	private Node getNode(Document doc, Node anotherNode) {
		Element node = doc.createElement(anotherNode.getNodeName());
		
		NodeList nodes = anotherNode.getChildNodes();
		
		for(int i = 0; i < nodes.getLength(); i++) {
			Element tmpNode = (Element) nodes.item(i);
			
			System.out.println(tmpNode.getTagName());
			System.out.println(tmpNode.getTextContent());
			
			node.appendChild(getElement(doc, tmpNode.getTagName(), tmpNode.getTextContent()));
		}
		
		return node;
	}
	
	private Element getElement(Document doc, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}
}