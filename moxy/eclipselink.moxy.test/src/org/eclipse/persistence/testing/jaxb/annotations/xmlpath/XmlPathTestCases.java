package org.eclipse.persistence.testing.jaxb.annotations.xmlpath;

import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlPathTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/xmlpathannotation.xml";
    
    public XmlPathTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class, Employee.class, Address.class, PhoneNumber.class});
        setControlDocument(XML_RESOURCE);
    }
    
    public Object getControlObject() {
        Employee emp = new Employee();
        emp.id = 101;
        emp.firstName = "Jane";
        emp.lastName = "Doe";
        emp.address = new Address();
        emp.address.street = "123 Fake Street";
        emp.address.city = "Ottawa";
        emp.address.id="102";

        emp.phones = new Vector<PhoneNumber>();
        
        PhoneNumber num1 = new PhoneNumber();
        num1.number = "123-4567";
        emp.phones.add(num1);
        
        PhoneNumber num2 = new PhoneNumber();
        num2.number = "234-5678";
        emp.phones.add(num2);
        
        emp.attributes = new HashMap<QName, String>();
        emp.attributes.put(new QName("attr1"), "value1");
        emp.attributes.put(new QName("http://myns.com/myns", "attr2"), "value2");
        
        Root root = new Root();
        root.employees = new Vector<Employee>();
        root.addresses = new Vector<Address>();
        
        root.employees.add(emp);
        root.addresses.add(emp.address);
        
        return root;
    }
    
    
}
