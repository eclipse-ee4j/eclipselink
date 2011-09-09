/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
@XmlSchema(		
		xmlns = {@XmlNs(prefix="ns3", namespaceURI="namespace3"),
				 @XmlNs(prefix="ns1", namespaceURI="namespace1"),
				 @XmlNs(prefix="ns0", namespaceURI="namespace0")
		}
)
		
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import javax.xml.bind.annotation.*;