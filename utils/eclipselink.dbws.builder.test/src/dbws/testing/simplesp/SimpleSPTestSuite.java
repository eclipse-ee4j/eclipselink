/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/
package dbws.testing.simplesp;

//javase imports
import java.io.StringReader;
import org.w3c.dom.Document;

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
import org.eclipse.persistence.platform.database.MySQLPlatform;

//testing imports
import dbws.testing.DBWSTestSuite;

public class SimpleSPTestSuite extends DBWSTestSuite {

    @BeforeClass
    public static void setUp() throws WSDLException {
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
                "procedurePattern=\"VarcharSP\" " +
                "returnType=\"xsd:int\" " +
            "/>" +
            "<procedure " +
                "name=\"NoArgsTest\" " +
                "procedurePattern=\"NoArgSP\" " +
                "returnType=\"xsd:int\" " +
            "/>" +
            "<procedure " +
                "name=\"GetAllTest\" " +
                "procedurePattern=\"GetAll\" " +
                "isCollection=\"true\" " +
                "isSimpleXMLFormat=\"true\" " +
                "simpleXMLFormatTag=\"simplesp-rows\" " +
                "xmlTag=\"simplesp-row\" " +
            "/>" +
            "<procedure " +
                "name=\"FindByJobTest\" " +
                "procedurePattern=\"FindByJob\" " +
                "isCollection=\"true\" " +
                "isSimpleXMLFormat=\"true\" " +
                "simpleXMLFormatTag=\"simplesp-rows\" " +
                "xmlTag=\"simplesp-row\" " +
            "/>" +
            "<procedure " +
                "name=\"InOutArgsTest\" " +
                "procedurePattern=\"InOutArgsSP\" " +
                "isSimpleXMLFormat=\"true\" " +
            "/>" +
          "</dbws-builder>";
        DBWSTestSuite.setUp();
    }

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
        Document controlDoc = xmlParser.parse(new StringReader(xrService.getORSession()
            .getProject().getDatasourceLogin().getPlatform() instanceof MySQLPlatform ? VALUE_0_XML
            : VALUE_1_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String VALUE_0_XML =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<value>0</value>";
    public static final String VALUE_1_XML =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<value>1</value>";

    @Test
    public void noargsTest() {
        Invocation invocation = new Invocation("NoArgsTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(xrService.getORSession()
            .getProject().getDatasourceLogin().getPlatform() instanceof MySQLPlatform ? VALUE_0_XML
            : VALUE_1_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }

    @Test
    public void getAllTest() {
        Invocation invocation = new Invocation("GetAllTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ALL_SIMPLESP_ROWS_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ALL_SIMPLESP_ROWS_XML =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simplesp-rows xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"any\">" +
        "<simplesp-row>" +
          "<EMPNO>7369</EMPNO>" +
          "<ENAME>SMITH</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7902</MGR>" +
          "<HIREDATE>1980-12-17</HIREDATE>" +
          "<SAL>800.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7499</EMPNO>" +
          "<ENAME>ALLEN</ENAME>" +
          "<JOB>SALESMAN</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-02-20</HIREDATE>" +
          "<SAL>1600.00</SAL>" +
          "<COMM>300.00</COMM>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7521</EMPNO>" +
          "<ENAME>WARD</ENAME>" +
          "<JOB>SALESMAN</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-02-22</HIREDATE>" +
          "<SAL>1250.00</SAL>" +
          "<COMM>500.00</COMM>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7566</EMPNO>" +
          "<ENAME>JONES</ENAME>" +
          "<JOB>MANAGER</JOB>" +
          "<MGR>7839</MGR>" +
          "<HIREDATE>1981-04-02</HIREDATE>" +
          "<SAL>2975.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7654</EMPNO>" +
          "<ENAME>MARTIN</ENAME>" +
          "<JOB>SALESMAN</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-09-28</HIREDATE>" +
          "<SAL>1250.00</SAL>" +
          "<COMM>1400.00</COMM>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7698</EMPNO>" +
          "<ENAME>BLAKE</ENAME>" +
          "<JOB>MANAGER</JOB>" +
          "<MGR>7839</MGR>" +
          "<HIREDATE>1981-05-01</HIREDATE>" +
          "<SAL>2850.00</SAL>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7782</EMPNO>" +
          "<ENAME>CLARK</ENAME>" +
          "<JOB>MANAGER</JOB>" +
          "<MGR>7839</MGR>" +
          "<HIREDATE>1981-06-09</HIREDATE>" +
          "<SAL>2450.00</SAL>" +
          "<DEPTNO>10</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7788</EMPNO>" +
          "<ENAME>SCOTT</ENAME>" +
          "<JOB>ANALYST</JOB>" +
          "<MGR>7566</MGR>" +
          "<HIREDATE>1981-06-09</HIREDATE>" +
          "<SAL>3000.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7839</EMPNO>" +
          "<ENAME>KING</ENAME>" +
          "<JOB>PRESIDENT</JOB>" +
          "<HIREDATE>1981-11-17</HIREDATE>" +
          "<SAL>5000.00</SAL>" +
          "<DEPTNO>10</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7844</EMPNO>" +
          "<ENAME>TURNER</ENAME>" +
          "<JOB>SALESMAN</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-09-08</HIREDATE>" +
          "<SAL>1500.00</SAL>" +
          "<COMM>0.00</COMM>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7876</EMPNO>" +
          "<ENAME>ADAMS</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7788</MGR>" +
          "<HIREDATE>1987-05-23</HIREDATE>" +
          "<SAL>1100.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7900</EMPNO>" +
          "<ENAME>JAMES</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-12-03</HIREDATE>" +
          "<SAL>950.00</SAL>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7902</EMPNO>" +
          "<ENAME>FORD</ENAME>" +
          "<JOB>ANALYST</JOB>" +
          "<MGR>7566</MGR>" +
          "<HIREDATE>1981-12-03</HIREDATE>" +
          "<SAL>3000.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7934</EMPNO>" +
          "<ENAME>MILLER</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7782</MGR>" +
          "<HIREDATE>1982-01-23</HIREDATE>" +
          "<SAL>1300.00</SAL>" +
          "<DEPTNO>10</DEPTNO>" +
        "</simplesp-row>" +
      "</simplesp-rows>";

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
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ALL_SIMPLESP_CLERK_ROWS_XML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<simplesp-rows xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"any\">" +
        "<simplesp-row>" +
          "<EMPNO>7369</EMPNO>" +
          "<ENAME>SMITH</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7902</MGR>" +
          "<HIREDATE>1980-12-17</HIREDATE>" +
          "<SAL>800.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7876</EMPNO>" +
          "<ENAME>ADAMS</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7788</MGR>" +
          "<HIREDATE>1987-05-23</HIREDATE>" +
          "<SAL>1100.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7900</EMPNO>" +
          "<ENAME>JAMES</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-12-03</HIREDATE>" +
          "<SAL>950.00</SAL>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7934</EMPNO>" +
          "<ENAME>MILLER</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7782</MGR>" +
          "<HIREDATE>1982-01-23</HIREDATE>" +
          "<SAL>1300.00</SAL>" +
          "<DEPTNO>10</DEPTNO>" +
        "</simplesp-row>" +
      "</simplesp-rows>";

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
        Document controlDoc = xmlParser.parse(new StringReader(IN_OUT_ARGS_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String IN_OUT_ARGS_XML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<simple-xml-format>" +
        "<simple-xml>" +
          "<U>barf-yuck</U>" +
          "<V>55</V>" +
        "</simple-xml>" +
      "</simple-xml-format>";
}