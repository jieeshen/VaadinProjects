package gov.fda.edkb.EdkbWeb;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PubMedRef {
    int pmid;
    String journal;
    String volume;
    String issue;
    String pgn;
    String year;
    String affiliation;
    String fauthor;
    ArrayList<String> authors=new ArrayList<String>();
    String atitle;
    String aabs;
    String doi;

    // Constructor initialize
    public PubMedRef(String pmidString) {

    	try{
    		String urlString="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id="+pmidString+"&retmode=xml";
    	
	        URL xmlUrl= new URL(urlString);
		
	    	//get an remote XML and transform to Document
	
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	       
	        //Document doc = db.parse(new File(path+pmidString+".xml"));
	        Document doc = db.parse(xmlUrl.openStream());
	
	        Node root=doc.getDocumentElement();
	
	        parserNode(root);
	        fauthor=authors.get(0);
	        pmid=Integer.parseInt(pmidString);
    	}catch (Exception e) {
        	System.out.println("something is wrong when open the URL");
        }
    }

    private void parserNode(Node node){
        String tagName = node.getNodeName();
        NodeList children = node.getChildNodes();

        for (int i=0;i<children.getLength();i++){
            Node childnode=children.item(i);
            Short nodeType = childnode.getNodeType();

            if(childnode.getNodeName()=="AuthorList"){
                NodeList authorNodes=childnode.getChildNodes();
                for (int j=0;j<authorNodes.getLength();j++){
                    Node authNode=authorNodes.item(j);
                    if (authNode.getNodeType()== 1){
                        NodeList authName=authNode.getChildNodes();
                        String aname= authName.item(1).getTextContent()+", "+authName.item(3).getTextContent();
                        authors.add(aname);
                    }

                }

            }

            if(nodeType == Node.ELEMENT_NODE ){
                parserNode(childnode);
            }else if(nodeType ==Node.TEXT_NODE){
                if (tagName=="Year" && childnode.getParentNode().getParentNode().getNodeName()=="PubDate"){
                     year=childnode.getNodeValue();
                }
                if (tagName=="Affiliation"){
                     affiliation=childnode.getNodeValue();
                }
                if (tagName=="ISOAbbreviation"){
                    journal=childnode.getNodeValue();
                }
                if (tagName=="Volume"){
                     volume=childnode.getNodeValue();
                }
                if (tagName=="Issue"){
                    issue=childnode.getNodeValue();
                }
                if (tagName=="MedlinePgn"){
                    pgn=childnode.getNodeValue();
                }

                if (tagName=="ArticleTitle"){
                    atitle=childnode.getNodeValue();
                }
                if (tagName=="AbstractText"){
                    aabs=childnode.getNodeValue();
                }
                if (tagName=="ELocationID"){
                    doi=childnode.getNodeValue();
                }

            }
        }
    }
}
