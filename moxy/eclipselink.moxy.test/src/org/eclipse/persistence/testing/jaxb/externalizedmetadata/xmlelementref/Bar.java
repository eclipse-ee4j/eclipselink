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
// dmccann - December 03/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref;

//@javax.xml.bind.annotation.XmlRootElement(name="foobar")
public class Bar {
    public int id;

    public boolean equals(Object obj){
        if(obj instanceof Bar){
            return id == ((Bar)obj).id;
        }
        return false;
    }

}
