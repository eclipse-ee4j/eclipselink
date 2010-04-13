package org.eclipse.persistence.testing.jaxb.xmlrootelement;

import java.util.ArrayList;
import java.util.Calendar;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlRootElementNilTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_nil.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/employee_nil_write.xml";
    private final static int CONTROL_ID = 10;

    public XmlRootElementNilTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);    
        setWriteControlDocument(XML_WRITE_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeNoNamespace.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeNoNamespace employee = new EmployeeNoNamespace();
        return employee;
    }
}
