/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmltype;

import javax.xml.bind.annotation.*;

/**
 * Mapping: Class to Complex Type Definition
 * {base type definition} Component
 * if the class contains a mapped property or field
 * annotated with @XmlValue, then the schema
 * type to which mapped property or field's type is mapped.
 */
@XmlRootElement
public class BaseType001c {
    @XmlValue
    public boolean b01;
    
    public BaseType001c() {}
}
