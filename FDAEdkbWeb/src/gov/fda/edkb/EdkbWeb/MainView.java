package gov.fda.edkb.EdkbWeb;



import java.io.File;
import java.util.Locale;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class MainView extends CustomComponent implements View {
	public static final String NAME = "";
	
	private String pmid;
	
	Label text = new Label();
    Button logout = new Button("Logout", new Button.ClickListener() {

        @Override
        public void buttonClick(ClickEvent event) {

            // "Logout" the user
            getSession().setAttribute("user", null);

            // Refresh this view, should redirect to login view
            getUI().getNavigator().navigateTo(NAME);
        }
    });
    
	static class PMidValidator implements Validator{
		public void validate(Object value) throws InvalidValueException {
			if (!isNumber(value)) {
                throw new InvalidValueException(
                        "PubMed ID must be a number.");
            }
		}
		private boolean isNumber(Object value) {
			if (value==null || !(value instanceof String)){
				return false;	
			}
			return ((String) value).matches("[0-9]*");
		}
	}


	static class ValueColumnGenerator implements Table.ColumnGenerator{

		private String format;

		public ValueColumnGenerator(String format){
			this.format=format;
		}
		
		@Override
		public Object generateCell(Table source, Object itemId, Object columnId) {
			final Property prop1 = source.getItem(itemId).getItemProperty("wage");
			final Property prop2 = source.getItem(itemId).getItemProperty("taxrate");
			
			
			final Label label=new Label(String.format(format, new Object[]{((Double) prop1.getValue())*((Double) prop2.getValue())}));
			label.addStyleName("column-type-value");
			label.addStyleName("column-"+(String) columnId);
			
			
			
			ValueChangeListener listener = new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					label.setValue(String.format(format, new Object[]{((Double) prop1.getValue())*((Double) prop2.getValue())}));
					
				}
			};
			
			for (String pid:new String[]{"wage","taxrate"})
				((ValueChangeNotifier) source.getItem(itemId).getItemProperty(pid)).addListener(listener);
			
			return label;
		}
		
	}
    
	
    
    
    
    @SuppressWarnings({ "serial", "deprecation" })
	public MainView() {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		
		Locale displayLocale=Locale.ENGLISH;
		final Locale[] locales=Locale.getAvailableLocales();
			
		
		String basePathString = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		
		FileResource fileResource = new FileResource(new File(basePathString+"/WEB-INF/images/FDA.png"));
		
	
		Image imageFDAImage=new Image("", fileResource);
		
		Link linkFDAImage = new Link("Image link",fileResource);

		Button button = new Button("Click Me");
		
		final TextField secretTextField= new TextField("Internel Use Only");
		secretTextField.setValue("only developer can see!!!");
		secretTextField.setVisible(false);
		final Button downloadXMLButton = new Button("Download the XML ...");
		downloadXMLButton.setDescription("click to download");
		
		downloadXMLButton.setEnabled(false);
		
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				downloadXMLButton.setEnabled(false);
				secretTextField.setVisible(true);
			}
		});
		
		final TextField inputPMidField=new TextField("PubMed ID","");
		inputPMidField.setWidth("200px");
		inputPMidField.addValidator(new PMidValidator());
		
		final Button getRefInfoButton=new Button("Get the Information");
		getRefInfoButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				pmid=inputPMidField.getValue();
				PubMedRef pmRef=new PubMedRef(pmid);
				TextArea tArea=new TextArea(pmRef.atitle);
				
				tArea.setWidth("800px");
				tArea.setHeight("600px");
				if (pmRef.aabs!=null){
					tArea.setValue(pmRef.aabs);
				}else{
					tArea.setValue("No Abstract");
				}
				layout.addComponent(tArea);
				
			}
		});
		
				
		
		inputPMidField.addTextChangeListener(new TextChangeListener() {			
			public void textChange(TextChangeEvent event) {
				downloadXMLButton.setEnabled(true);		
			}
		});
		inputPMidField.setTextChangeEventMode(TextChangeEventMode.LAZY);
		
		downloadXMLButton.addClickListener(new Button.ClickListener() {			
			public void buttonClick(ClickEvent event) {
				pmid=inputPMidField.getValue();
				layout.addComponent(new Label("downloading "+pmid));
				StreamResource.StreamSource xmlSR = new GetPubMedXMLStream(pmid);
				StreamResource xmResource = new StreamResource(xmlSR,pmid+".xml");
				BrowserFrame bfBrowserFrame = new BrowserFrame(pmid,xmResource);
				bfBrowserFrame.setWidth("800px");
				bfBrowserFrame.setHeight("600px");
				layout.addComponent(bfBrowserFrame);
			}
		});
		

		
		final ListSelect select=new ListSelect("Select a language");
		//select.setNewItemsAllowed(true);
		
		for (int i=0; i<locales.length; i++) {
		    select.addItem(locales[i]);
		    select.setItemCaption(locales[i],
		                          locales[i].getDisplayName(displayLocale));
		    
		    // Automatically select the current locale
		    if (locales[i].equals(getLocale()))
		        select.setValue(locales[i]);
		}
		// Locale code of the selected locale
		final Label localeCode = new Label("");
		
		
		final PopupDateField date = new PopupDateField("Calendar in the selected language");
		date.setResolution(DateField.RESOLUTION_DAY.DAY);
		
		select.addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
		        Locale locale = (Locale) select.getValue();
		        date.setLocale(locale);
		        localeCode.setValue("Locale code: " +
		                            locale.getLanguage() + "_" +
		                            locale.getCountry());
				
			}
		});
		select.setImmediate(true);
		
		
		
		// A container with a defined width. The default content layout
		/*/ of Panel is VerticalLayout, which has 100% default width.
		Panel panel = new Panel("Panel Containing a Label");
		panel.setWidth("300px");

		panel.setContent(
		    new Label("This is a Label inside a Panel. There is " +
		              "enough text in the label to make the text " +
		              "wrap when it exceeds the width of the panel."));
		layout.addComponent(panel);*/
		
		final Table table=new Table("Table1");
		table.addContainerProperty("name", String.class, null,"Name",null,null);
		table.addContainerProperty("wage", Double.class, null,"Wage",null,null);
		table.addContainerProperty("taxrate", Double.class, null,"Tax Rate",null,null);
		table.addContainerProperty("tax", Double.class, null,"Tax Due",null,null);
		table.setWidth("600px");
		
		table.addItem(new Object[]{
				"Jie Shen", new Double(1983), new Double(0.1),new Double(198.3)				
		}, new Integer(1));

		table.addItem(new Object[]{
				"Xin Gu", new Double(2004), new Double(0.15),null				
		}, new Integer(2));
		
		table.addItem(new Object[]{
				"Esame Shen", new Double(6983), new Double(0.2),null				
		}, new Integer(3));
		table.addGeneratedColumn("tax",new ValueColumnGenerator("%.2f"));
		
		table.setPageLength(3);
		table.setSelectable(true);
		table.setMultiSelect(true);
		//table.setEditable(true);
		table.setColumnCollapsingAllowed(true);
		table.setImmediate(true);
		table.setColumnCollapsed(1, false);
		//table.setVisibleColumns();
		final Label current = new Label();
		table.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				current.setValue(table.getValue().toString());
			}
		});
		
		
		
		final CheckBox switchEditableBox = new CheckBox("Edit");
		switchEditableBox.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				table.setEditable(((Boolean)event.getProperty().getValue()).booleanValue());				
			}
		});
		layout.addComponent(imageFDAImage);
		
		
		layout.addComponent(select);
		layout.addComponent(localeCode);
		layout.addComponent(date);
		
		
		layout.addComponent(linkFDAImage);
		layout.addComponent(button);
		layout.addComponent(getRefInfoButton);
		layout.addComponent(inputPMidField);
		layout.addComponent(downloadXMLButton);
		layout.addComponent(secretTextField);
		Notification.show("Attention!","You are entering the testing website, you may lost your connection anytime!",Notification.TYPE_TRAY_NOTIFICATION);
		layout.addComponent(table);
		layout.addComponent(current);
		layout.addComponent(switchEditableBox);
    	
    	setCompositionRoot(new CssLayout(logout,layout));
    }
    
    
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		String username = String.valueOf(getSession().getAttribute("user"));
		text.setValue("Hello " + username);
	}

}
