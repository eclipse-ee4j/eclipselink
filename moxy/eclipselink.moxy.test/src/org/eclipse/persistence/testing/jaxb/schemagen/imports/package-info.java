@XmlSchema(namespace="employeeNamespace",
	xmlns = {@XmlNs(prefix="x", namespaceURI="employeeNamespace"),@XmlNs(prefix="y", namespaceURI="addressNamespace")}
)
@XmlSchemaTypes({@XmlSchemaType(name="time", type=Calendar.class)})
package org.eclipse.persistence.testing.jaxb.schemagen.imports;

import java.util.Calendar;

import javax.xml.bind.annotation.*;