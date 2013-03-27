/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * October 30, 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SimpleRootTestCases extends JAXBWithJSONTestCases{

    public SimpleRootTestCases(String name) throws Exception {
        super(name);
        setControlDocument("org/eclipse/persistence/testing/jaxb/annotations/xmltransformation/simpleRoot.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/annotations/xmltransformation/simpleRoot.json");
        setClasses(new Class[]{SimpleRoot.class});
    }

    @Override
    protected Object getControlObject() {
       SimpleRoot so = new SimpleRoot();
       return so;
    }
    
}
