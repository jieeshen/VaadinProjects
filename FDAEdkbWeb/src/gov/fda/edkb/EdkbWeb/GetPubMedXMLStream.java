package gov.fda.edkb.EdkbWeb;

import java.io.InputStream;
import java.net.URL;

import com.vaadin.server.StreamResource.StreamSource;


@SuppressWarnings("serial")
public class GetPubMedXMLStream implements StreamSource {

	String pmidString;
	
	@Override
	public InputStream getStream() {
		String urlString="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id="+pmidString+"&retmode=xml";
        try{
        	URL xmlUrl= new URL(urlString);
            InputStream is = xmlUrl.openStream();
            return is;
        }catch (Exception e) {
        	System.out.println("something is wrong when open the URL");
        	return null;
        }
		        
		
	}

	public GetPubMedXMLStream(String pmid){
		pmidString=pmid;		
	}


}
