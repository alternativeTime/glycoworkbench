package org.eurocarbdb.application.glycoworkbench;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eurocarbdb.application.glycanbuilder.BuilderWorkspace;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GWDValidator{
	private DocumentBuilder builder;
	private XPathExpression structureTypeExp;
	private XPathExpression structureDictionaryExp;

	File outputDirectory=new File("/scratch/cleanedStructureFiles");
	
	public GWDValidator() throws ParserConfigurationException, XPathExpressionException{
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		XPathFactory factory = XPathFactory.newInstance();
		
		XPath xpath = factory.newXPath();
		structureTypeExp = xpath.compile("./StructureType");
		structureDictionaryExp = xpath.compile("/StructureDictionary");
		
		BuilderWorkspace workspace=new BuilderWorkspace(new GlycanRendererAWT());
		
		outputDirectory.mkdirs();
	}
	
	public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
		GWDValidator val=new GWDValidator();
		val.validate("/conf/carbbankraw_dict.gwd");
		val.validate("/conf/cfg_dict.gwd");
		val.validate("/conf/glycosciences_dict.gwd");
		val.validate("/conf/glycomedb_dict.gwd");
	}
	
	public void validate(String databaseName) throws  ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException{
		Document doc=builder.parse(getClass().getResourceAsStream(databaseName));
		NodeList obj=(NodeList)structureDictionaryExp.evaluate(doc,XPathConstants.NODESET);
		
		Node topLevelElement=obj.item(0);
		
		NodeList structureTypes=(NodeList)structureTypeExp.evaluate(topLevelElement,XPathConstants.NODESET);
		
		int total=structureTypes.getLength();
		int invalidStructures=0;
		for(int i=0;i<structureTypes.getLength();i++){
			Node node=structureTypes.item(i);
			String structure=node.getAttributes().getNamedItem("structure").getTextContent();
			try{
				Glycan ret = Glycan.fromString(structure,new MassOptions());
			}catch(Exception ex){
				invalidStructures++;
				
				topLevelElement.removeChild(node);
			}
		}
		
		File databaseFile=new File(databaseName);
		
		File outputDatabaseFile=new File(outputDirectory.getPath()+"/"+databaseFile.getName());
		
		TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        
       OutputStream out=new FileOutputStream(outputDatabaseFile);
        
        StreamResult result = new StreamResult(out);
        trans.transform(new DOMSource(doc), result);
		
		System.out.println("Output file name: "+outputDatabaseFile.getPath()+"(deleted: "+invalidStructures+" of  "+total+")");
	}
}