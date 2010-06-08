@XmlSchema(namespace="examplenamespace",
	xmlns = {@XmlNs(prefix="x", namespaceURI="examplenamespace")}
)
@XmlSchemaTypes({@XmlSchemaType(name="time", type=Calendar.class)})
package org.eclipse.persistence.testing.jaxb.schemagen.employee;

import java.util.Calendar;

import javax.xml.bind.annotation.*;