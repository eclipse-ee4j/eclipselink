package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.textui.TestRunner;

import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class LoadAndSaveBase64AttachmentTestCases extends LoadAndSaveTestCases {
    private static String OPTIONS_CONTROL_FILE_NAME = "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/EmployeeWithBase64AttachmentOptions.xml";
    private static String OPTIONS_CONTROL_ID = "OPTIONS";
    private static byte[] OPTIONS_CONTROL_BYTES = "OPTIONS".getBytes();

    public LoadAndSaveBase64AttachmentTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();

        XMLMarshaller aMarshaller = ((SDOXMLHelper)xmlHelper).getXmlMarshaller();
        XMLUnmarshaller anUnmarshaller = ((SDOXMLHelper)xmlHelper).getXmlUnmarshaller();
        XMLAttachmentMarshaller anAttachmentMarshaller = new AttachmentMarshallerImpl("c_id0");
        XMLAttachmentUnmarshaller anAttachmentUnmarshaller = new AttachmentUnmarshallerImpl("Testing".getBytes());
        aMarshaller.setAttachmentMarshaller(anAttachmentMarshaller);
        anUnmarshaller.setAttachmentUnmarshaller(anAttachmentUnmarshaller);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/EmployeeWithBase64Attachment.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/EmployeeWithBase64Attachment.xml");
    }

    protected String getNoSchemaControlFileName() {     
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/EmployeeWithBase64Attachment.xml");
    }

    protected String getControlRootURI() {
        return "http://www.example.org";
    }

    protected String getControlRootName() {
        return "employeeType";
    }
    
    protected String getRootInterfaceName() {
        return "EmployeeType";
    }

    public void registerTypes() {
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType bytesType = (SDOType) typeHelper.getType("commonj.sdo", "Bytes");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        // create a new Type for Customers
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        customerType.set("uri", getControlRootURI());
        customerType.set("name", "EmployeeType");

        // create an idproperty
        addProperty(customerType, "id", stringType, false, false, true);

        // create a first name property
        addProperty(customerType, "name", stringType, false, false, true);

        // create a photo property
        DataObject photoProp = addProperty(customerType, "photo", bytesType, true, true, true);        

        // now define the Customer type so that customers can be made
         Type customerSDOType = typeHelper.define(customerType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", customerSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    public void testLoadFromInputStreamSaveToOutputStreamWithOptions() throws Exception {
        defineTypes();

        DataObject loadOptions = dataFactory.create(SDOConstants.ORACLE_SDO_URL, SDOConstants.XMLHELPER_LOAD_OPTIONS);
        loadOptions.set(SDOConstants.ATTACHMENT_UNMARSHALLER_OPTION, new AttachmentUnmarshallerImpl(OPTIONS_CONTROL_BYTES));
        FileInputStream inputStream = new FileInputStream(OPTIONS_CONTROL_FILE_NAME);
        XMLDocument document = xmlHelper.load(inputStream, null, loadOptions);

        assertEquals(OPTIONS_CONTROL_BYTES, document.getRootObject().get("photo"));

        DataObject saveOptions = dataFactory.create(SDOConstants.ORACLE_SDO_URL, SDOConstants.XMLHELPER_LOAD_OPTIONS);
        saveOptions.set(SDOConstants.ATTACHMENT_MARSHALLER_OPTION, new AttachmentMarshallerImpl(OPTIONS_CONTROL_ID));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xmlHelper.save(document, outputStream, saveOptions);
    
        compareXML(OPTIONS_CONTROL_FILE_NAME, outputStream.toString());
    }

    public void testLoadFromReaderSaveToWriterWithOptions() throws Exception {
        defineTypes();

        DataObject options = dataFactory.create(SDOConstants.ORACLE_SDO_URL, SDOConstants.XMLHELPER_LOAD_OPTIONS);
        options.set(SDOConstants.ATTACHMENT_UNMARSHALLER_OPTION, new AttachmentUnmarshallerImpl(OPTIONS_CONTROL_BYTES));
        FileInputStream inputStream = new FileInputStream(OPTIONS_CONTROL_FILE_NAME);
        InputStreamReader reader = new InputStreamReader(inputStream);
        XMLDocument document = xmlHelper.load(reader, null, options);

        assertEquals(OPTIONS_CONTROL_BYTES, document.getRootObject().get("photo"));

        DataObject saveOptions = dataFactory.create(SDOConstants.ORACLE_SDO_URL, SDOConstants.XMLHELPER_LOAD_OPTIONS);
        saveOptions.set(SDOConstants.ATTACHMENT_MARSHALLER_OPTION, new AttachmentMarshallerImpl(OPTIONS_CONTROL_ID));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        xmlHelper.save(document, writer, saveOptions);

        compareXML(OPTIONS_CONTROL_FILE_NAME, outputStream.toString());
    }

    public void testLoadFromSourceSaveToResultWithOptions() throws Exception {
        defineTypes();

        DataObject options = dataFactory.create(SDOConstants.ORACLE_SDO_URL, SDOConstants.XMLHELPER_LOAD_OPTIONS);
        options.set(SDOConstants.ATTACHMENT_UNMARSHALLER_OPTION, new AttachmentUnmarshallerImpl(OPTIONS_CONTROL_BYTES));
        FileInputStream inputStream = new FileInputStream(OPTIONS_CONTROL_FILE_NAME);
        StreamSource source = new StreamSource(inputStream);
        XMLDocument document = xmlHelper.load(source, null, options);

        assertEquals(OPTIONS_CONTROL_BYTES, document.getRootObject().get("photo"));

        DataObject saveOptions = dataFactory.create(SDOConstants.ORACLE_SDO_URL, SDOConstants.XMLHELPER_LOAD_OPTIONS);
        saveOptions.set(SDOConstants.ATTACHMENT_MARSHALLER_OPTION, new AttachmentMarshallerImpl(OPTIONS_CONTROL_ID));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outputStream);
        xmlHelper.save(document, result, saveOptions);

        compareXML(OPTIONS_CONTROL_FILE_NAME, outputStream.toString());
    }

}
