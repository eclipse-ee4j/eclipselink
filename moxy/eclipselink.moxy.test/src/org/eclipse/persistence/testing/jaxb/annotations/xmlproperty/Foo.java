/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Oracle = 2.2 - Initial implementation
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
