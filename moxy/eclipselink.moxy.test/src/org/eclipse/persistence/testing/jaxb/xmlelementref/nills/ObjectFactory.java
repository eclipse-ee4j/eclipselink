package org.eclipse.persistence.testing.jaxb.xmlelementref.nills;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.xmlelementref.nills.Employee.Task;

@XmlRegistry
public class ObjectFactory {
	
	@XmlElementDecl(name = "task")
	public JAXBElement<Task> createTask(Task t) {
		JAXBElement<Task> task =  new JAXBElement<Employee.Task>(new QName("task"), Task.class, t);
		task.setNil(true);
		return task;
	}
	
	@XmlElementDecl(name = "address")
	public JAXBElement<Address> createAddress(Address a) {
		JAXBElement<Address> address =  new JAXBElement<Address>(new QName("address"), Address.class, a);
		address.setNil(true);
		return address;
	}

	@XmlElementDecl(name="foo")
    public JAXBElement<String> createFoo(String foo) {
        return new JAXBElement<String>(new QName("foo"), String.class, foo);
    }

    @XmlElementDecl(name="bar")
    public JAXBElement<String> createBar(String bar) {
        return new JAXBElement<String>(new QName("bar"), String.class, bar);
    }
}
