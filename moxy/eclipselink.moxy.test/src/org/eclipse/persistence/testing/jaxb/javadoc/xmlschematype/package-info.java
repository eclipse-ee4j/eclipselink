@XmlSchema(namespace="myns")
@XmlSchemaTypes({@XmlSchemaType(name="time", type=java.util.Calendar.class)})
package org.eclipse.persistence.testing.jaxb.javadoc.xmlschematype;

import javax.xml.bind.annotation.*;