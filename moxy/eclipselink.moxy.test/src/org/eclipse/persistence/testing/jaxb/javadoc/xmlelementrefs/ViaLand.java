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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementrefs;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="land-transport")
public class ViaLand extends TransportType{
	
	@XmlAttribute
	public String truckCompany;
	
	public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ViaLand)) {
            return false;
        }
        ViaLand t = (ViaLand) obj;
        
        return t.transportTypeID == this.transportTypeID && t.transportCost == this.transportCost && t.truckCompany.equals(this.truckCompany);
	}
}
