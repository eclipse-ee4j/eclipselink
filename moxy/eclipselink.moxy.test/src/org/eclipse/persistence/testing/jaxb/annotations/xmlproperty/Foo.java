/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlproperty;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlProperties;
import org.eclipse.persistence.oxm.annotations.XmlProperty;

@XmlRootElement(name="foo")
@XmlProperties({@XmlProperty(name="property1", value="value1"), @XmlProperty(name="property2", value="121", valueType=Integer.class)})
public class Foo {
    
    @XmlProperty(name="barProp", value="barValue")
    public String bar;
    
    @XmlProperties({@XmlProperty(name="bar2Prop", value="bar2Value")})
    public String bar2;

}
