// Copyright (c) 2007 Oracle. All rights reserved.

package dbws.testing.customSQL;

// Javase imports
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// Java extension imports
import javax.wsdl.WSDLException;

// JUnit imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.oxm.XMLMarshaller;

import dbws.testing.TestDBWSFactory;

// domain-specific imports
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

public class CustomTestSuite {

    public static final String DBWS_BUILDER_XML_USERNAME =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
          "<properties>" +
              "<property name=\"projectName\">customSQL</property>" +
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
            "schemaPattern=\"%\" " +
            "tableNamePattern=\"custom\" " +
            ">" +
            "<sql " +
              "name=\"countCustom\" " +
              "isCollection=\"false\" " +
              "simpleXMLFormatTag=\"custom-info\" " +
              "xmlTag=\"aggregate-info\" " +
              ">" +
              "<text><![CDATA[select count(*) from custom]]></text> " +
            "</sql>" +
            "<sql " +
              "name=\"customInfo\" " +
              "isCollection=\"false\" " +
              "simpleXMLFormatTag=\"custom-info\" " +
              "xmlTag=\"aggregate-info\" " +
              "> " +
              "<text><![CDATA[select count(*) as \"COUNT\", max(SAL) as \"MAX-Salary\" from custom]]></text>" +
            "</sql>" +
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

        buildJar(builderString, "Custom");
    }

	// test fixture(s)
    static XRServiceAdapter xrService = null;
    @BeforeClass
    public static void setUpDBWSService() {
        TestDBWSFactory serviceFactory = new TestDBWSFactory();
        xrService = serviceFactory.buildService();
    }

    @Test
    public void countCustom() {
        Invocation invocation = new Invocation("countCustom");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(COUNT_CUSTOM_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String COUNT_CUSTOM_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<custom-info xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"any\">" +
          "<aggregate-info>" +
            "<count_x0028__x002A__x0029_>14</count_x0028__x002A__x0029_>" +
          "</aggregate-info>" +
        "</custom-info>";

    @Test
    public void countMaxSalCustomSQLInfo() {
        Invocation invocation = new Invocation("customInfo");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(COUNT_MAXSAL_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String COUNT_MAXSAL_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<custom-info xsi:type=\"any\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<aggregate-info>" +
            "<COUNT>14</COUNT>" +
            "<MAX-Salary>5000.00</MAX-Salary>" +
          "</aggregate-info>" +
        "</custom-info>";

    @Test
    public void findByPrimaryKey() {
        Invocation invocation = new Invocation("findByPrimaryKey_custom");
        invocation.setParameter("empno", 7788);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(FINDBYPK_7788_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String FINDBYPK_7788_CONTROL_DOC =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<ns1:custom xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"urn:customSQL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<ns1:empno>7788</ns1:empno>" +
          "<ns1:ename>SCOTT</ns1:ename>" +
          "<ns1:job>ANALYST</ns1:job>" +
          "<ns1:mgr>7566</ns1:mgr>" +
          "<ns1:hiredate>1981-06-09</ns1:hiredate>" +
          "<ns1:sal>3000.00</ns1:sal>" +
          "<ns1:deptno>20</ns1:deptno>" +
        "</ns1:custom>";

    @SuppressWarnings("unchecked")
    @Test
    public void findAll() {
        Invocation invocation = new Invocation("findAll_custom");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(FIND_ALL_CONTROL_DOC));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String FIND_ALL_CONTROL_DOC =
    	"<?xml version = '1.0' encoding = 'UTF-8'?>" +
    	"<collection>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7369</ns1:empno>" +
    	    "<ns1:ename>SMITH</ns1:ename>" +
    	    "<ns1:job>CLERK</ns1:job>" +
    	    "<ns1:mgr>7902</ns1:mgr>" +
    	    "<ns1:hiredate>1980-12-17</ns1:hiredate>" +
    	    "<ns1:sal>800.00</ns1:sal>" +
    	    "<ns1:deptno>20</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7499</ns1:empno>" +
    	    "<ns1:ename>ALLEN</ns1:ename>" +
    	    "<ns1:job>SALESMAN</ns1:job>" +
    	    "<ns1:mgr>7698</ns1:mgr>" +
    	    "<ns1:hiredate>1981-02-20</ns1:hiredate>" +
    	    "<ns1:sal>1600.00</ns1:sal>" +
    	    "<ns1:comm>300.00</ns1:comm>" +
    	    "<ns1:deptno>30</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7521</ns1:empno>" +
    	    "<ns1:ename>WARD</ns1:ename>" +
    	    "<ns1:job>SALESMAN</ns1:job>" +
    	    "<ns1:mgr>7698</ns1:mgr>" +
    	    "<ns1:hiredate>1981-02-22</ns1:hiredate>" +
    	    "<ns1:sal>1250.00</ns1:sal>" +
    	    "<ns1:comm>500.00</ns1:comm>" +
    	    "<ns1:deptno>30</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7566</ns1:empno>" +
    	    "<ns1:ename>JONES</ns1:ename>" +
    	    "<ns1:job>MANAGER</ns1:job>" +
    	    "<ns1:mgr>7839</ns1:mgr>" +
    	    "<ns1:hiredate>1981-04-02</ns1:hiredate>" +
    	    "<ns1:sal>2975.00</ns1:sal>" +
    	    "<ns1:deptno>20</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7654</ns1:empno>" +
    	    "<ns1:ename>MARTIN</ns1:ename>" +
    	    "<ns1:job>SALESMAN</ns1:job>" +
    	    "<ns1:mgr>7698</ns1:mgr>" +
    	    "<ns1:hiredate>1981-09-28</ns1:hiredate>" +
    	    "<ns1:sal>1250.00</ns1:sal>" +
    	    "<ns1:comm>1400.00</ns1:comm>" +
    	    "<ns1:deptno>30</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7698</ns1:empno>" +
    	    "<ns1:ename>BLAKE</ns1:ename>" +
    	    "<ns1:job>MANAGER</ns1:job>" +
    	    "<ns1:mgr>7839</ns1:mgr>" +
    	    "<ns1:hiredate>1981-05-01</ns1:hiredate>" +
    	    "<ns1:sal>2850.00</ns1:sal>" +
    	    "<ns1:deptno>30</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7782</ns1:empno>" +
    	    "<ns1:ename>CLARK</ns1:ename>" +
    	    "<ns1:job>MANAGER</ns1:job>" +
    	    "<ns1:mgr>7839</ns1:mgr>" +
    	    "<ns1:hiredate>1981-06-09</ns1:hiredate>" +
    	    "<ns1:sal>2450.00</ns1:sal>" +
    	    "<ns1:deptno>10</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7788</ns1:empno>" +
    	    "<ns1:ename>SCOTT</ns1:ename>" +
    	    "<ns1:job>ANALYST</ns1:job>" +
    	    "<ns1:mgr>7566</ns1:mgr>" +
    	    "<ns1:hiredate>1981-06-09</ns1:hiredate>" +
    	    "<ns1:sal>3000.00</ns1:sal>" +
    	    "<ns1:deptno>20</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7839</ns1:empno>" +
    	    "<ns1:ename>KING</ns1:ename>" +
    	    "<ns1:job>PRESIDENT</ns1:job>" +
    	    "<ns1:hiredate>1981-11-17</ns1:hiredate>" +
    	    "<ns1:sal>5000.00</ns1:sal>" +
    	    "<ns1:deptno>10</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7844</ns1:empno>" +
    	    "<ns1:ename>TURNER</ns1:ename>" +
    	    "<ns1:job>SALESMAN</ns1:job>" +
    	    "<ns1:mgr>7698</ns1:mgr>" +
    	    "<ns1:hiredate>1981-09-08</ns1:hiredate>" +
    	    "<ns1:sal>1500.00</ns1:sal>" +
    	    "<ns1:comm>0.00</ns1:comm>" +
    	    "<ns1:deptno>30</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7876</ns1:empno>" +
    	    "<ns1:ename>ADAMS</ns1:ename>" +
    	    "<ns1:job>CLERK</ns1:job>" +
    	    "<ns1:mgr>7788</ns1:mgr>" +
    	    "<ns1:hiredate>1987-05-23</ns1:hiredate>" +
    	    "<ns1:sal>1100.00</ns1:sal>" +
    	    "<ns1:deptno>20</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7900</ns1:empno>" +
    	    "<ns1:ename>JAMES</ns1:ename>" +
    	    "<ns1:job>CLERK</ns1:job>" +
    	    "<ns1:mgr>7698</ns1:mgr>" +
    	    "<ns1:hiredate>1981-12-03</ns1:hiredate>" +
    	    "<ns1:sal>950.00</ns1:sal>" +
    	    "<ns1:deptno>30</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7902</ns1:empno>" +
    	    "<ns1:ename>FORD</ns1:ename>" +
    	    "<ns1:job>ANALYST</ns1:job>" +
    	    "<ns1:mgr>7566</ns1:mgr>" +
    	    "<ns1:hiredate>1981-12-03</ns1:hiredate>" +
    	    "<ns1:sal>3000.00</ns1:sal>" +
    	    "<ns1:deptno>20</ns1:deptno>" +
    	  "</ns1:custom>" +
    	  "<ns1:custom xmlns:ns1=\"urn:customSQL\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
    	    "<ns1:empno>7934</ns1:empno>" +
    	    "<ns1:ename>MILLER</ns1:ename>" +
    	    "<ns1:job>CLERK</ns1:job>" +
    	    "<ns1:mgr>7782</ns1:mgr>" +
    	    "<ns1:hiredate>1982-01-23</ns1:hiredate>" +
    	    "<ns1:sal>1300.00</ns1:sal>" +
    	    "<ns1:deptno>10</ns1:deptno>" +
    	  "</ns1:custom>" +
    	"</collection>";
}