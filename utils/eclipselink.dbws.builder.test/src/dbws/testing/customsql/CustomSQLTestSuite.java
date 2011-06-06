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
package dbws.testing.customsql;

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

//testing imports
import dbws.testing.DBWSTestSuite;

public class CustomSQLTestSuite extends DBWSTestSuite {

    @BeforeClass
    public static void setUp() throws WSDLException {
        DBWS_BUILDER_XML_USERNAME =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<properties>" +
                "<property name=\"projectName\">customSQL</property>" +
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
        DBWSTestSuite.setUp();
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
        /*
        String resultString = DBWSTestProviderHelper.documentToString(doc);
        System.out.println(resultString);
        String controlString = DBWSTestProviderHelper.documentToString(controlDoc);
        System.out.println(controlString);
        */
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }

    public static final String COUNT_CUSTOM_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<custom-info xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
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
        /*
        String resultString = DBWSTestProviderHelper.documentToString(doc);
        System.out.println(resultString);
        String controlString = DBWSTestProviderHelper.documentToString(controlDoc);
        System.out.println(controlString);
        */
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }

    public static final String COUNT_MAXSAL_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<custom-info xsi:type=\"simple-xml-format\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<aggregate-info>" +
            "<COUNT>14</COUNT>" +
            "<MAX-Salary>5000.00</MAX-Salary>" +
          "</aggregate-info>" +
        "</custom-info>";

    @Test
    public void findByPrimaryKey() {
        Invocation invocation = new Invocation("findByPrimaryKey_customType");
        invocation.setParameter("empno", 7788);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(FINDBYPK_7788_CONTROL_DOC));
        /*
        String resultString = DBWSTestProviderHelper.documentToString(doc);
        System.out.println(resultString);
        String controlString = DBWSTestProviderHelper.documentToString(controlDoc);
        System.out.println(controlString);
        */
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }

    public static final String FINDBYPK_7788_CONTROL_DOC =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7788</empno>" +
          "<ename>SCOTT</ename>" +
          "<job>ANALYST</job>" +
          "<mgr>7566</mgr>" +
          "<hiredate>1981-06-09</hiredate>" +
          "<sal>3000.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>20</deptno>" +
        "</customType>";

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void findAll() {
        Invocation invocation = new Invocation("findAll_customType");
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
        /*
        String resultString = DBWSTestProviderHelper.documentToString(doc);
        System.out.println(resultString);
        String controlString = DBWSTestProviderHelper.documentToString(controlDoc);
        System.out.println(controlString);
        */
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }

    public static final String FIND_ALL_CONTROL_DOC =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<collection>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7369</empno>" +
          "<ename>SMITH</ename>" +
          "<job>CLERK</job>" +
          "<mgr>7902</mgr>" +
          "<hiredate>1980-12-17</hiredate>" +
          "<sal>800.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>20</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7499</empno>" +
          "<ename>ALLEN</ename>" +
          "<job>SALESMAN</job>" +
          "<mgr>7698</mgr>" +
          "<hiredate>1981-02-20</hiredate>" +
          "<sal>1600.00</sal>" +
          "<comm>300.00</comm>" +
          "<deptno>30</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7521</empno>" +
          "<ename>WARD</ename>" +
          "<job>SALESMAN</job>" +
          "<mgr>7698</mgr>" +
          "<hiredate>1981-02-22</hiredate>" +
          "<sal>1250.00</sal>" +
          "<comm>500.00</comm>" +
          "<deptno>30</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7566</empno>" +
          "<ename>JONES</ename>" +
          "<job>MANAGER</job>" +
          "<mgr>7839</mgr>" +
          "<hiredate>1981-04-02</hiredate>" +
          "<sal>2975.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>20</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7654</empno>" +
          "<ename>MARTIN</ename>" +
          "<job>SALESMAN</job>" +
          "<mgr>7698</mgr>" +
          "<hiredate>1981-09-28</hiredate>" +
          "<sal>1250.00</sal>" +
          "<comm>1400.00</comm>" +
          "<deptno>30</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7698</empno>" +
          "<ename>BLAKE</ename>" +
          "<job>MANAGER</job>" +
          "<mgr>7839</mgr>" +
          "<hiredate>1981-05-01</hiredate>" +
          "<sal>2850.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>30</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7782</empno>" +
          "<ename>CLARK</ename>" +
          "<job>MANAGER</job>" +
          "<mgr>7839</mgr>" +
          "<hiredate>1981-06-09</hiredate>" +
          "<sal>2450.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>10</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7788</empno>" +
          "<ename>SCOTT</ename>" +
          "<job>ANALYST</job>" +
          "<mgr>7566</mgr>" +
          "<hiredate>1981-06-09</hiredate>" +
          "<sal>3000.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>20</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7839</empno>" +
          "<ename>KING</ename>" +
          "<job>PRESIDENT</job>" +
          "<mgr xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<hiredate>1981-11-17</hiredate>" +
          "<sal>5000.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>10</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7844</empno>" +
          "<ename>TURNER</ename>" +
          "<job>SALESMAN</job>" +
          "<mgr>7698</mgr>" +
          "<hiredate>1981-09-08</hiredate>" +
          "<sal>1500.00</sal>" +
          "<comm>0.00</comm>" +
          "<deptno>30</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7876</empno>" +
          "<ename>ADAMS</ename>" +
          "<job>CLERK</job>" +
          "<mgr>7788</mgr>" +
          "<hiredate>1987-05-23</hiredate>" +
          "<sal>1100.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>20</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7900</empno>" +
          "<ename>JAMES</ename>" +
          "<job>CLERK</job>" +
          "<mgr>7698</mgr>" +
          "<hiredate>1981-12-03</hiredate>" +
          "<sal>950.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>30</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7902</empno>" +
          "<ename>FORD</ename>" +
          "<job>ANALYST</job>" +
          "<mgr>7566</mgr>" +
          "<hiredate>1981-12-03</hiredate>" +
          "<sal>3000.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>20</deptno>" +
        "</customType>" +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7934</empno>" +
          "<ename>MILLER</ename>" +
          "<job>CLERK</job>" +
          "<mgr>7782</mgr>" +
          "<hiredate>1982-01-23</hiredate>" +
          "<sal>1300.00</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>10</deptno>" +
        "</customType>" +
      "</collection>";
}