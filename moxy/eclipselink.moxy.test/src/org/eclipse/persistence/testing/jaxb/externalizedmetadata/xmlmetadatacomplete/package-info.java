/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 09/2010 - 2.2 - Initial implementation
 ******************************************************************************/
@javax.xml.bind.annotation.XmlSchema(
        namespace="http://www.eclipse.org/eclipselink/xsds/persistence/oxm/junk", 
        attributeFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        elementFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        xmlns= {
            @javax.xml.bind.annotation.XmlNs(prefix="nsx", namespaceURI="http://www.example.com/xsds/fake")
        })
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete;
