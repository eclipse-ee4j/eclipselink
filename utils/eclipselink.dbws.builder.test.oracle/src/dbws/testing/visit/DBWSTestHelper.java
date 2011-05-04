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
 *     Mike Norman - May 2008, created DBWS ORacle test package
 ******************************************************************************/
package dbws.testing.visit;

//javase imports
import java.io.StringWriter;
import org.w3c.dom.Document;

//java eXtension imports
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//EclipseLink imports
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;

public class DBWSTestHelper {

    public static final String PACKAGE_NAME = "SOMEPACKAGE";

    public static final String PROC1 = "p1";
    public static final String PROC1_TEST = PROC1 + "Test";
    public static final String PROC1_SERVICE = PROC1_TEST + "Service";
    public static final String PROC1_NAMESPACE = "urn:" + PROC1_TEST;
    public static final String PROC1_SERVICE_NAMESPACE = "urn:" + PROC1_SERVICE;
    public static final String PROC1_PORT = PROC1_SERVICE + "Port";

    public static final String PROC2 = "p2";
    public static final String PROC2_TEST = PROC2 + "Test";
    public static final String PROC2_SERVICE = PROC2_TEST + "Service";
    public static final String PROC2_NAMESPACE = "urn:" + PROC2_TEST;
    public static final String PROC2_SERVICE_NAMESPACE = "urn:" + PROC2_SERVICE;
    public static final String PROC2_PORT = PROC2_SERVICE + "Port";

    public static final String PROC3 = "p3";
    public static final String PROC3_TEST = PROC3 + "Test";
    public static final String PROC3_SERVICE = PROC3_TEST + "Service";
    public static final String PROC3_NAMESPACE = "urn:" + PROC3_TEST;
    public static final String PROC3_SERVICE_NAMESPACE = "urn:" + PROC3_SERVICE;
    public static final String PROC3_PORT = PROC3_SERVICE + "Port";

    public static final String PROC4 = "p4";
    public static final String PROC4_TEST = PROC4 + "Test";
    public static final String PROC4_SERVICE = PROC4_TEST + "Service";
    public static final String PROC4_NAMESPACE = "urn:" + PROC4_TEST;
    public static final String PROC4_SERVICE_NAMESPACE = "urn:" + PROC4_SERVICE;
    public static final String PROC4_PORT = PROC4_SERVICE + "Port";

    public static final String PROC5 = "p5";
    public static final String PROC5_TEST = PROC5 + "Test";
    public static final String PROC5_SERVICE = PROC5_TEST + "Service";
    public static final String PROC5_NAMESPACE = "urn:" + PROC5_TEST;
    public static final String PROC5_SERVICE_NAMESPACE = "urn:" + PROC5_SERVICE;
    public static final String PROC5_PORT = PROC5_SERVICE + "Port";

    public static final String PROC6 = "p6";
    public static final String PROC6_TEST = PROC6 + "Test";
    public static final String PROC6_SERVICE = PROC6_TEST + "Service";
    public static final String PROC6_NAMESPACE = "urn:" + PROC6_TEST;
    public static final String PROC6_SERVICE_NAMESPACE = "urn:" + PROC6_SERVICE;
    public static final String PROC6_PORT = PROC6_SERVICE + "Port";

    public static final String PROC7 = "p7";
    public static final String PROC7_TEST = PROC7 + "Test";
    public static final String PROC7_SERVICE = PROC7_TEST + "Service";
    public static final String PROC7_NAMESPACE = "urn:" + PROC7_TEST;
    public static final String PROC7_SERVICE_NAMESPACE = "urn:" + PROC7_SERVICE;
    public static final String PROC7_PORT = PROC7_SERVICE + "Port";

    public static final String PROC8 = "p8";
    public static final String PROC8_TEST = PROC8 + "Test";
    public static final String PROC8_SERVICE = PROC8_TEST + "Service";
    public static final String PROC8_NAMESPACE = "urn:" + PROC8_TEST;
    public static final String PROC8_SERVICE_NAMESPACE = "urn:" + PROC8_SERVICE;
    public static final String PROC8_PORT = PROC8_SERVICE + "Port";

    public static final String FUNC1 = "f1";
    public static final String FUNC1_TEST = FUNC1 + "Test";
    public static final String FUNC1_SERVICE = FUNC1_TEST + "Service";
    public static final String FUNC1_NAMESPACE = "urn:" + FUNC1_TEST;
    public static final String FUNC1_SERVICE_NAMESPACE = "urn:" + FUNC1_SERVICE;
    public static final String FUNC1_PORT = FUNC1_SERVICE + "Port";

    public static final String FUNC2 = "f2";
    public static final String FUNC2_TEST = FUNC2 + "Test";
    public static final String FUNC2_SERVICE = FUNC2_TEST + "Service";
    public static final String FUNC2_NAMESPACE = "urn:" + FUNC2_TEST;
    public static final String FUNC2_SERVICE_NAMESPACE = "urn:" + FUNC2_SERVICE;
    public static final String FUNC2_PORT = FUNC2_SERVICE + "Port";

    public static final String FUNC3 = "f3";
    public static final String FUNC3_TEST = FUNC3 + "Test";
    public static final String FUNC3_SERVICE = FUNC3_TEST + "Service";
    public static final String FUNC3_NAMESPACE = "urn:" + FUNC3_TEST;
    public static final String FUNC3_SERVICE_NAMESPACE = "urn:" + FUNC3_SERVICE;
    public static final String FUNC3_PORT = FUNC3_SERVICE + "Port";

    public static final String FUNC4 = "f4";
    public static final String FUNC4_TEST = FUNC4 + "Test";
    public static final String FUNC4_SERVICE = FUNC4_TEST + "Service";
    public static final String FUNC4_NAMESPACE = "urn:" + FUNC4_TEST;
    public static final String FUNC4_SERVICE_NAMESPACE = "urn:" + FUNC4_SERVICE;
    public static final String FUNC4_PORT = FUNC4_SERVICE + "Port";

    public static final String TBL1_COMPATIBLETYPE = "SOMEPACKAGE_TBL1";
    public static final String TBL1_DATABASETYPE = "SOMEPACKAGE.TBL1";
    public static final String TBL1_DESCRIPTOR_ALIAS = TBL1_COMPATIBLETYPE.toLowerCase();
    public static final String TBL1_DESCRIPTOR_JAVACLASSNAME = TBL1_DATABASETYPE.toLowerCase() +
        COLLECTION_WRAPPER_SUFFIX;

    public static final String TBL2_COMPATIBLETYPE = "SOMEPACKAGE_TBL2";
    public static final String TBL2_DATABASETYPE = "SOMEPACKAGE.TBL2";
    public static final String TBL2_DESCRIPTOR_ALIAS = TBL2_COMPATIBLETYPE.toLowerCase();
    public static final String TBL2_DESCRIPTOR_JAVACLASSNAME = TBL2_DATABASETYPE.toLowerCase() +
        COLLECTION_WRAPPER_SUFFIX;

    public static final String TBL3_COMPATIBLETYPE = "SOMEPACKAGE_TBL3";
    public static final String TBL3_DATABASETYPE = "SOMEPACKAGE.TBL3";
    public static final String TBL3_DESCRIPTOR_ALIAS = TBL3_COMPATIBLETYPE.toLowerCase();
    public static final String TBL3_DESCRIPTOR_JAVACLASSNAME = TBL3_DATABASETYPE.toLowerCase() +
        COLLECTION_WRAPPER_SUFFIX;

    public static final String TBL4_COMPATIBLETYPE = "SOMEPACKAGE_TBL4";
    public static final String TBL4_DATABASETYPE = "SOMEPACKAGE.TBL4";
    public static final String TBL4_DESCRIPTOR_ALIAS = TBL4_COMPATIBLETYPE.toLowerCase();
    public static final String TBL4_DESCRIPTOR_JAVACLASSNAME = TBL4_DATABASETYPE.toLowerCase() +
        COLLECTION_WRAPPER_SUFFIX;

    public static final String ARECORD_COMPATIBLETYPE = "SOMEPACKAGE_ARECORD";
    public static final String ARECORD_DATABASETYPE = "SOMEPACKAGE.ARECORD";
    public static final String ARECORD_DESCRIPTOR_ALIAS = ARECORD_COMPATIBLETYPE.toLowerCase();
    public static final String ARECORD_DESCRIPTOR_JAVACLASSNAME = ARECORD_DATABASETYPE.toLowerCase();

    public static final String BRECORD_COMPATIBLETYPE = "SOMEPACKAGE_BRECORD";
    public static final String BRECORD_DATABASETYPE = "SOMEPACKAGE.BRECORD";
    public static final String BRECORD_DESCRIPTOR_ALIAS = BRECORD_COMPATIBLETYPE.toLowerCase();
    public static final String BRECORD_DESCRIPTOR_JAVACLASSNAME = BRECORD_DATABASETYPE.toLowerCase();

    public static final String ADVJDBC_PACKAGE_NAME =
        "ADVANCED_OBJECT_DEMO";
    public static final String ADVJDBC_ECHO_EMPOBJECT =
        "echoEmpObject";
    public static final String ADVJDBC_ECHO_EMPOBJECT_TEST =
        "echoEmpObjectTest";
    public static final String ADVJDBC_ECHO_EMPOBJECT_SERVICE =
        ADVJDBC_ECHO_EMPOBJECT + "Service";
    public static final String ADVJDBC_ECHO_EMPOBJECT_NAMESPACE =
        "urn:echoEmp";
    public static final String ADVJDBC_ECHO_EMPOBJECT_SERVICE_NAMESPACE =
        "urn:echoEmpService";
    public static final String ADVJDBC_ECHO_EMPOBJECT_PORT = "echoEmpServicePort";

    public static final String ADVJDBC_ANOTHER_PACKAGE_NAME =
        "ANOTHER_ADVANCED_DEMO";
    public static final String ADVJDBC_BUILD_EMPARRAY =
        "buildEmpArray";
    public static final String ADVJDBC_BUILD_EMPARRAY_TEST =
        ADVJDBC_BUILD_EMPARRAY + "Test";
    public static final String ADVJDBC_BUILD_EMPARRAY_SERVICE =
        ADVJDBC_BUILD_EMPARRAY + "Service";
    public static final String ADVJDBC_BUILD_EMPARRAY_NAMESPACE =
        "urn:empArray";
    public static final String ADVJDBC_BUILD_EMPARRAY_SERVICE_NAMESPACE =
        ADVJDBC_BUILD_EMPARRAY_NAMESPACE + "Service";
    public static final String ADVJDBC_BUILD_EMPARRAY_PORT = ADVJDBC_BUILD_EMPARRAY_SERVICE +
            "Port";

    public static final String ADVJDBC_TOPLEVEL_PACKAGE_NAME =
        "toplevel";
    public static final String ADVJDBC_BUILD_TBL1 =
        "SF_TBL1";
    public static final String ADVJDBC_BUILDTBL1 =
        "buildTbl1";
    public static final String ADVJDBC_BUILD_TBL1_TEST =
        ADVJDBC_BUILDTBL1 + "Test";
    public static final String ADVJDBC_BUILD_TBL1_SERVICE =
        ADVJDBC_BUILDTBL1 + "Service";
    public static final String ADVJDBC_BUILD_TBL1_NAMESPACE =
        "urn:tbl1";
    public static final String ADVJDBC_BUILD_TBL1_SERVICE_NAMESPACE =
        ADVJDBC_BUILD_TBL1_NAMESPACE + "Service";
    public static final String ADVJDBC_BUILD_TBL1_PORT = ADVJDBC_BUILD_TBL1_SERVICE +
            "Port";
    public static final String ADVJDBC_BUILD_TBL2 =
        "buildTbl2";
    public static final String ADVJDBC_BUILD_TBL2_TEST =
        ADVJDBC_BUILD_TBL2 + "Test";
    public static final String ADVJDBC_BUILD_TBL2_SERVICE =
        ADVJDBC_BUILD_TBL2 + "Service";
    public static final String ADVJDBC_BUILD_TBL2_NAMESPACE =
        "urn:tbl2";
    public static final String ADVJDBC_BUILD_TBL2_SERVICE_NAMESPACE =
        ADVJDBC_BUILD_TBL2_NAMESPACE + "Service";
    public static final String ADVJDBC_BUILD_TBL2_PORT = ADVJDBC_BUILD_TBL2_SERVICE +
            "Port";

    public static final String ADVJDBC_BUILD_ARECORD =
        "buildARecord";
    public static final String ADVJDBC_BUILD_ARECORD_TEST =
        ADVJDBC_BUILD_ARECORD + "Test";
    public static final String ADVJDBC_BUILD_ARECORD_SERVICE =
        ADVJDBC_BUILD_ARECORD + "Service";
    public static final String ADVJDBC_BUILD_ARECORD_NAMESPACE =
        "urn:aRecord";
    public static final String ADVJDBC_BUILD_ARECORD_SERVICE_NAMESPACE =
        ADVJDBC_BUILD_ARECORD_NAMESPACE + "Service";
    public static final String ADVJDBC_BUILD_ARECORD_PORT = ADVJDBC_BUILD_ARECORD_SERVICE + "Port";

    public static final String ADVJDBC_BUILD_CRECORD =
        "buildCRecord";
    public static final String ADVJDBC_BUILD_CRECORD_TEST =
        ADVJDBC_BUILD_CRECORD + "Test";
    public static final String ADVJDBC_BUILD_CRECORD_SERVICE =
        ADVJDBC_BUILD_CRECORD + "Service";
    public static final String ADVJDBC_BUILD_CRECORD_NAMESPACE =
        "urn:cRecord";
    public static final String ADVJDBC_BUILD_CRECORD_SERVICE_NAMESPACE =
        ADVJDBC_BUILD_CRECORD_NAMESPACE + "Service";
    public static final String ADVJDBC_BUILD_CRECORD_PORT = ADVJDBC_BUILD_CRECORD_SERVICE + "Port";

    public static final String LTBL_PACKAGE_NAME = "LTBL_PKG";
    public static final String LTBL_PROCEDURE_NAME = "LTBL_QUERY";
    public static final String LTBL_PROJECT = "localTable";
    public static final String LTBL_PROJECT_TEST = LTBL_PROJECT + "Test";
    public static final String LTBL_PROJECT_SERVICE = LTBL_PROJECT + "Service";
    public static final String LTBL_PROJECT_NAMESPACE = "urn:" + LTBL_PROJECT;
    public static final String LTBL_PROJECT_SERVICE_NAMESPACE = "urn:" + LTBL_PROJECT_SERVICE;
    public static final String LTBL_PROJECT_PORT = LTBL_PROJECT_SERVICE + "Port";

    public static final String F17_PACKAGE_NAME = "TESMANPACK";
    public static final String PROCF17 = "f17";
    public static final String PROCF17_NAME = "TESMANPROC17";
    public static final String PROCF17_NAMESPACE = "urn:" + PROCF17;
    public static final String PROCF17_SERVICE_NAMESPACE = PROCF17_NAMESPACE + "Service";
    public static final String PROCF17_PORT = PROCF17 + "Port";
    public static final String PROCF17_SERVICE = PROCF17 + "Service";
    public static final String PROCF17_TEST = "f17Test";
    
    public static String documentToString(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(domSource, result);
            return stringWriter.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            return "<empty/>";
        }
    }
}
