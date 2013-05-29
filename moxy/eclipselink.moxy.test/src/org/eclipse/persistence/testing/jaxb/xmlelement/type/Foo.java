/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - May 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement.type;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Kohsuke Kawaguchi
 */
@XmlRootElement(name="foo")
class Foo {
    @XmlElement(type=BigDecimal.class)
    Object field;
    
    public boolean equals(Object obj){
		if(obj instanceof Foo){
			return (field == null && (((Foo)obj).field) == null) || ( field.equals(((Foo)obj).field));
		}
		return false;
	}
}
