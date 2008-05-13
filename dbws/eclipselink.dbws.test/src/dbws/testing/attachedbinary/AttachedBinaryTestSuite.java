package dbws.testing.attachedbinary;

//Javase imports
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//Java extension imports
import javax.activation.DataHandler;
import javax.wsdl.WSDLException;

//JUnit imports
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.dbws.SOAPAttachmentHandler;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.oxm.XMLMarshaller;

//domain-specific imports
import dbws.testing.TestDBWSFactory;
import static dbws.testing.TestDBWSFactory.buildJar;
import static dbws.testing.TestDBWSFactory.comparer;
import static dbws.testing.TestDBWSFactory.DATABASE_DRIVER_KEY;
import static dbws.testing.TestDBWSFactory.DATABASE_PASSWORD_KEY;
import static dbws.testing.TestDBWSFactory.DATABASE_PLATFORM_KEY;
import static dbws.testing.TestDBWSFactory.DATABASE_URL_KEY;
import static dbws.testing.TestDBWSFactory.DATABASE_USERNAME_KEY;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_DRIVER;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_PASSWORD;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_PLATFORM;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_URL;
import static dbws.testing.TestDBWSFactory.DEFAULT_DATABASE_USERNAME;
import static dbws.testing.TestDBWSFactory.xmlParser;
import static dbws.testing.TestDBWSFactory.xmlPlatform;

public class AttachedBinaryTestSuite {

    public static final String DBWS_BUILDER_XML_USERNAME =
     "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
     "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
       "<properties>" +
           "<property name=\"projectName\">attachedbinary</property>" +
           "<property name=\"targetNamespacePrefix\">ab</property>" +
           "<property name=\"logLevel\">off</property>" +
           "<property name=\"username\">";
    public static final String DBWS_BUILDER_XML_PASSWORD =
           "</property><property name=\"password\">";
    public static final String DBWS_BUILDER_XML_URL =
           "</property><property name=\"url\">";
    public static final String DBWS_BUILDER_XML_DRIVER =
           "</property><property name=\"driver\">";
    public static final String DBWS_BUILDER_XML_PLATFORM =
           "</property><property name=\"platformClassname\">";
    public static final String DBWS_BUILDER_XML_MAIN =
           "</property>" +
       "</properties>" +
       "<table " +
         "tableNamePattern=\"attachedbinary\" " +
         ">" +
	     "<procedure " +
	       "name=\"getBLOBById\" " +
	       "isCollection=\"false\" " +
	       "returnType=\"ab:attachedbinary\" " +
	       "procedurePattern=\"getBLOBById\" " +
	       "binaryAttachment=\"true\" " +
	     "/>" +
       "</table>" +
     "</dbws-builder>";

    public static void main(String[] args) throws IOException, WSDLException {
		String username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
		String password = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
		String url = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
		String driver = System.getProperty(DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER);
		String platform = System.getProperty(DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);
		
		String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
		password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + driver +
		DBWS_BUILDER_XML_PLATFORM + platform + DBWS_BUILDER_XML_MAIN;
		
		buildJar(builderString, "AttachedBinary");
	}

	// test fixture(s)
    static XRServiceAdapter xrService = null;
    @BeforeClass
    public static void setUpDBWSService() {
    	TestDBWSFactory serviceFactory = new TestDBWSFactory();
    	xrService = serviceFactory.buildService();
    }

    public static SOAPAttachmentHandler attachmentHandler = new SOAPAttachmentHandler();
    
    @SuppressWarnings("unchecked")
    @Test
    public void findAll() {
        Invocation invocation = new Invocation("findAll_attachedbinary");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.setAttachmentMarshaller(attachmentHandler);
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("attachedbinary-collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(ATTACHED_BINARY_COLLECTION_XML));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ATTACHED_BINARY_COLLECTION_XML = 
        "<?xml version = \"1.0\" encoding = \"UTF-8\"?>" +
        "<attachedbinary-collection>" +
	        "<ns1:attachedbinary xmlns:ns1=\"urn:attachedbinary\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
		        "<ns1:id>1</ns1:id>" +
		        "<ns1:name>one</ns1:name>" +
		        "<ns1:b>cid:ref1</ns1:b>" +
	        "</ns1:attachedbinary>" +
	        "<ns1:attachedbinary xmlns:ns1=\"urn:attachedbinary\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
		        "<ns1:id>2</ns1:id>" +
		        "<ns1:name>two</ns1:name>" +
		        "<ns1:b>cid:ref2</ns1:b>" +
	        "</ns1:attachedbinary>" +
	        "<ns1:attachedbinary xmlns:ns1=\"urn:attachedbinary\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
		        "<ns1:id>3</ns1:id>" +
		        "<ns1:name>three</ns1:name>" +
		        "<ns1:b>cid:ref3</ns1:b>" +
	        "</ns1:attachedbinary>" +
        "</attachedbinary-collection>";


    @Test
    public void getAttachments() throws IOException {
        DataHandler dataHandler = attachmentHandler.getAttachments().get("cid:ref1");
        ByteArrayInputStream bais = (ByteArrayInputStream)dataHandler.getInputStream();
        byte[] ref = new byte[bais.available()];
        int count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 1 == ref[i]);
        }
        dataHandler = attachmentHandler.getAttachments().get("cid:ref2");
        bais = (ByteArrayInputStream)dataHandler.getInputStream();
        ref = new byte[bais.available()];
        count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 2 == ref[i]);
        }
        dataHandler = attachmentHandler.getAttachments().get("cid:ref3");
        bais = (ByteArrayInputStream)dataHandler.getInputStream();
        ref = new byte[bais.available()];
        count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 3 == ref[i]);
        }
    }

    @Ignore
    public void getBLOBById() throws IOException {
        Invocation invocation = new Invocation("getBLOBById");
        Operation op = xrService.getOperation(invocation.getName());
        invocation.setParameter("pk", 1);
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        DataHandler dataHandler = (DataHandler)result;
        ByteArrayInputStream bais = (ByteArrayInputStream)dataHandler.getInputStream();
        byte[] ref = new byte[bais.available()];
        int count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 1 == ref[i]);
        }
    }

}
