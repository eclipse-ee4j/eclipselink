/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - June 10 2011, created DDL parser package
 *     David McCann - July 2011, visit tests
 ******************************************************************************/
package dbws.testing.simplesp;

//javase imports
import java.io.StringReader;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.DBWSTestSuite;

public class SimpleSPTestSuite extends DBWSTestSuite {
    
    @BeforeClass
    public static void setUp() throws WSDLException, SecurityException, NoSuchFieldException,
        IllegalArgumentException, IllegalAccessException {
        DBWS_BUILDER_XML_USERNAME =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<properties>" +
                "<property name=\"projectName\">simpleSP</property>" +
                "<property name=\"logLevel\">off</property>" +
                "<property name=\"username\">";
        DBWS_BUILDER_XML_PASSWORD =
                "</property><property name=\"password\">";
        DBWS_BUILDER_XML_URL =
                "</property><property name=\"url\">";
        DBWS_BUILDER_XML_DRIVER =
                "</property><property name=\"driver\">";
        DBWS_BUILDER_XML_PLATFORM =
                "</property><property name=\"platformClassname\">";
        DBWS_BUILDER_XML_MAIN =
                "</property>" +
            "</properties>" +
            "<procedure " +
                "name=\"VarcharTest\" " +
                "catalogPattern=\"TOPLEVEL\" " +
                "procedurePattern=\"VarcharSP\" " +
                "returnType=\"xsd:int\" " +
            "/>" +
            "<procedure " +
                "name=\"NoArgsTest\" " +
                "catalogPattern=\"TOPLEVEL\" " +
                "procedurePattern=\"NoArgSP\" " +
                "returnType=\"xsd:int\" " +
            "/>" +
            "<procedure " +
                "name=\"InOutArgsTest\" " +
                "catalogPattern=\"TOPLEVEL\" " +
                "procedurePattern=\"InOutArgsSP\" " +
                "isSimpleXMLFormat=\"true\" " +
            "/>" +
            "<procedure " +
                "name=\"FindByJobTest\" " +
                "catalogPattern=\"TOPLEVEL\" " +
                "procedurePattern=\"FindByJob\" " +
                "isCollection=\"true\" " +
                "isSimpleXMLFormat=\"true\" " +
                "simpleXMLFormatTag=\"simplesp-rows\" " +
                "xmlTag=\"simplesp-row\" " +
            "/>" +
            "<table " +
               "schemaPattern=\"%\" " +
               "tableNamePattern=\"SIMPLESP\" " +
               ">" +
               "<procedure " +
                  "name=\"GetAllTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GetAll\" " +
                  "isCollection=\"true\" " +
                  "returnType=\"simplespType\" " +
               "/>" +
            "</table>" +
          "</dbws-builder>";        
          builder = new DBWSBuilder();
          DBWSTestSuite.setUp(".");
    }

    public static final String VALUE_1_XML =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<value>1</value>";
    @Test
    public void varcharTest() {
        Invocation invocation = new Invocation("VarcharTest");
        invocation.setParameter("X", "this is a test");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VALUE_1_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    @Test
    public void noargsTest() {
        Invocation invocation = new Invocation("NoArgsTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VALUE_1_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void inOutArgsTest() {
        Invocation invocation = new Invocation("InOutArgsTest");
        invocation.setParameter("T", "yuck");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        //marshaller.marshal(result, System.out);
        Document controlDoc = xmlParser.parse(new StringReader(IN_OUT_ARGS_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String IN_OUT_ARGS_XML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<simple-xml-format>" +
        "<simple-xml>" +
          "<U>barf-yuck</U>" +
          "<V>55</V>" +
        "</simple-xml>" +
      "</simple-xml-format>";

    @Test
    public void findByJobTest() {
        Invocation invocation = new Invocation("FindByJobTest");
        invocation.setParameter("J", "CL%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ALL_SIMPLESP_CLERK_ROWS_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ALL_SIMPLESP_CLERK_ROWS_XML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<simplesp-rows xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
        "<simplesp-row>" +
          "<EMPNO>7369</EMPNO>" +
          "<ENAME>SMITH</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7902</MGR>" +
          "<HIREDATE>1980-12-17T00:00:00.0</HIREDATE>" +
          "<SAL>800.92</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7876</EMPNO>" +
          "<ENAME>ADAMS</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7788</MGR>" +
          "<HIREDATE>1987-05-23T00:00:00.0</HIREDATE>" +
          "<SAL>1100</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7900</EMPNO>" +
          "<ENAME>JAMES</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-12-03T00:00:00.0</HIREDATE>" +
          "<SAL>950</SAL>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7934</EMPNO>" +
          "<ENAME>MILLER</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7782</MGR>" +
          "<HIREDATE>1982-01-23T00:00:00.0</HIREDATE>" +
          "<SAL>1300</SAL>" +
          "<DEPTNO>10</DEPTNO>" +
        "</simplesp-row>" +
      "</simplesp-rows>";
    
    @SuppressWarnings("rawtypes")
    @Test
    public void getAllTest() {
        Invocation invocation = new Invocation("GetAllTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("all");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(ALL_SIMPLESP_ROWS_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ALL_SIMPLESP_ROWS_XML =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<all>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7369</empno>" +
              "<ename>SMITH</ename>" +
              "<job>CLERK</job>" +
              "<mgr>7902</mgr>" +
              "<hiredate>1980-12-17</hiredate>" +
              "<sal>800.92</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7499</empno>" +
              "<ename>ALLEN</ename>" +
              "<job>SALESMAN</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-02-20</hiredate>" +
              "<sal>1600</sal>" +
              "<comm>300</comm>" +
              "<deptno>30</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7521</empno>" +
              "<ename>WARD</ename>" +
              "<job>SALESMAN</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-02-22</hiredate>" +
              "<sal>1250</sal>" +
              "<comm>500</comm>" +
              "<deptno>30</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7566</empno>" +
              "<ename>JONES</ename>" +
              "<job>MANAGER</job>" +
              "<mgr>7839</mgr>" +
              "<hiredate>1981-04-02</hiredate>" +
              "<sal>2975</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7654</empno>" +
              "<ename>MARTIN</ename>" +
              "<job>SALESMAN</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-09-28</hiredate>" +
              "<sal>1250</sal>" +
              "<comm>1400</comm>" +
              "<deptno>30</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7698</empno>" +
              "<ename>BLAKE</ename>" +
              "<job>MANAGER</job>" +
              "<mgr>7839</mgr>" +
              "<hiredate>1981-05-01</hiredate>" +
              "<sal>2850</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>30</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7782</empno>" +
              "<ename>CLARK</ename>" +
              "<job>MANAGER</job>" +
              "<mgr>7839</mgr>" +
              "<hiredate>1981-06-09</hiredate>" +
              "<sal>2450</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>10</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7788</empno>" +
              "<ename>SCOTT</ename>" +
              "<job>ANALYST</job>" +
              "<mgr>7566</mgr>" +
              "<hiredate>1981-06-09</hiredate>" +
              "<sal>3000</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7839</empno>" +
              "<ename>KING</ename>" +
              "<job>PRESIDENT</job>" +
              "<mgr xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<hiredate>1981-11-17</hiredate>" +
              "<sal>5000</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>10</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7844</empno>" +
              "<ename>TURNER</ename>" +
              "<job>SALESMAN</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-09-08</hiredate>" +
              "<sal>1500</sal>" +
              "<comm>0</comm>" +
              "<deptno>30</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7876</empno>" +
              "<ename>ADAMS</ename>" +
              "<job>CLERK</job>" +
              "<mgr>7788</mgr>" +
              "<hiredate>1987-05-23</hiredate>" +
              "<sal>1100</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7900</empno>" +
              "<ename>JAMES</ename>" +
              "<job>CLERK</job>" +
              "<mgr>7698</mgr>" +
              "<hiredate>1981-12-03</hiredate>" +
              "<sal>950</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>30</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7902</empno>" +
              "<ename>FORD</ename>" +
              "<job>ANALYST</job>" +
              "<mgr>7566</mgr>" +
              "<hiredate>1981-12-03</hiredate>" +
              "<sal>3000</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>20</deptno>" +
          "</simplespType>" +
          "<simplespType xmlns=\"urn:simpleSP\">" +
              "<empno>7934</empno>" +
              "<ename>MILLER</ename>" +
              "<job>CLERK</job>" +
              "<mgr>7782</mgr>" +
              "<hiredate>1982-01-23</hiredate>" +
              "<sal>1300</sal>" +
              "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
              "<deptno>10</deptno>" +
          "</simplespType>" +
      "</all>";

}