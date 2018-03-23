package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoTestCases;

public class UnmarshallerCollectionTestCases  extends TypeMappingInfoTestCases {
	
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/jaxb/UnmarshallCollection.xml";

    public UnmarshallerObject testField;

    @XmlList
    public Object xmlListAnnotationField;

    public UnmarshallerCollectionTestCases(String name) throws Exception {
        super(name);
        setupParser();
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setTypeMappingInfos(getTypeMappingInfos());
    }

    protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
    	  if(typeMappingInfos == null) {
              typeMappingInfos = new TypeMappingInfo[1];

              TypeMappingInfo tmi = new TypeMappingInfo();
              tmi.setXmlTagName(new QName("someUri","testTagName1"));
              tmi.setElementScope(ElementScope.Global);
              tmi.setType(getClass().getField("testField").getGenericType());
              typeMappingInfos[0] = tmi;

          }
        return typeMappingInfos;
    }

    protected Object getControlObject() {


        QName qname = new QName("someUri", "testTagName");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        UnmarshallerObject details = new UnmarshallerObject();
        details.firstName ="theFirstName";
        Object object = details;
        jaxbElement.setValue(object);

        return jaxbElement;
    }

    public Map<String, InputStream> getControlSchemaFiles(){
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/jaxb/UnmarshallCollection.xsd");

        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("someUri", instream);

        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE;
    }

   public void testUnmarshallerCollectionNullTestCase(){
    	try{
	    	if(null != XML_INPUT_FACTORY) {
	    		InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
	            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
	            JAXBElement testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(xmlStreamReader, getTypeMappingInfo());
	            instream.close();
	            if(testObject.getValue()!=null){
	            	UnmarshallerObject details = (UnmarshallerObject)testObject.getValue();
	            	if(details!=null){
			            assertNotNull(details.getAccountNumber());
	            	}
		       }
	            
	    	}
    	}catch(Exception e){}
    }
    
  public Map getProperties() {
        Map props = new HashMap();
        props.put(JAXBContextFactory.DEFAULT_TARGET_NAMESPACE_KEY, "someUri");
        return props;
   }
  public void testSchemaGen() throws Exception {}
  public void testXMLToObjectFromSourceWithTypeMappingInfoXML() throws Exception {}
  public void testXMLToObjectFromXMLStreamReaderWithTypeMappingInfo() throws Exception {}
  public void testXMLToObjectFromXMLEventReaderWithTypeMappingInfo() throws Exception {}
  public void testObjectToXMLStreamWriterWithTypeMappingInfo() throws Exception {}
  public void testObjectToResultWithTypeMappingInfoXML() throws Exception {}
  public void testObjectToXMLEventWriterWithTypeMappingInfo() throws Exception {}

}
