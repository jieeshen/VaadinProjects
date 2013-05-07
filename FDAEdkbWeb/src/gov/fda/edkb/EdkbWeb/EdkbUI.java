package gov.fda.edkb.EdkbWeb;

import java.io.File;


import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class EdkbUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		
		String basePathString = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		
		FileResource fileResource = new FileResource(new File(basePathString+"/WEB-INF/images/FDA.png"));
		
	
		Image imageFDAImage=new Image("", fileResource);
		
		Link linkFDAImage = new Link("Image link",fileResource);

		Button button = new Button("Click Me");
		
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});
		
		TextField inputPMidField=new TextField("PubMed ID","test");
		inputPMidField.addTextChangeListener(new TextChangeListener() {
			
			public void textChange(TextChangeEvent event) {
				layout.addComponent(new Label("changed...."));
				
			}
		});
		//inputPMidField.setComponentError(new UserError("Bad Value!"));
		final String pmid="";
		
		Button downloadXMLButton = new Button("Download the XML ...");
		//downloadXMLButton.setComponentError(new UserError("bad click"));
		downloadXMLButton.addClickListener(new Button.ClickListener() {			
			public void buttonClick(ClickEvent event) {
				StreamResource.StreamSource xmlSR = new GetPubMedXMLStream(pmid);
				StreamResource xmResource = new StreamResource(xmlSR,"pmxml");
				layout.addComponent(new BrowserFrame("xml", xmResource));	
			}
		});
		layout.addComponent(imageFDAImage);
		layout.addComponent(linkFDAImage);
		layout.addComponent(button);
		layout.addComponent(inputPMidField);
		layout.addComponent(downloadXMLButton);

	}

}