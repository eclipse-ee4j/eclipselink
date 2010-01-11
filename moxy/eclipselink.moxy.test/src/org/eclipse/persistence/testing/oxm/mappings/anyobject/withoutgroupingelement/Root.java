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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

/**
 *  @version $Header: Root.java 17-jul-2006.11:19:10 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class Root {
    private Object any;
    
    public Object getAny() {
        return any;
    }
    public void setAny(Object a) {
        any = a;
    }
    public boolean equals(Object object) {
        if(object instanceof Root) {
            if(any == null && ((Root)object).getAny() == null) {
                return true;
            } else if(any == null && ((Root)object).getAny() != null) {
                return false;
            } else {
                Object value1 = any;
                Object value2 = ((Root)object).getAny();
                if ((value1 instanceof XMLRoot) && (value2 instanceof XMLRoot)) {
                    XMLMappingTestCases.compareXMLRootObjects((XMLRoot)value1, (XMLRoot)value2);
                    return true;
                } else {
                    return this.any.equals(((Root)object).getAny());
                }
            }
        }
        return false;
    }
    
}
