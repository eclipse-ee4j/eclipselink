@XmlSchema(
        namespace = "http://www.example.org/traveler", elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = { @XmlNs(prefix = "ns1", namespaceURI = "http://www.example.org/traveler") })
package org.eclipse.persistence.jpars.test.model.traveler;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

