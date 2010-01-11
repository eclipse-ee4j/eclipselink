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
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement;

import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

/**
 *  @version $Header: Root.java 21-jul-2005.14:29:07 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class Root {
    private List any;
    private String directMapping;

    public List getAny() {
        return any;
    }

    public void setAny(List a) {
        any = a;
    }

    public boolean equals(Object object) {
        if (object instanceof Root) {
            String directMapping1 = directMapping;
            String directMapping2 = ((Root)object).getDirectMapping();
            if ((directMapping1 == null) && (directMapping2 != null)) {
                return false;
            }
            if ((directMapping2 == null) && (directMapping1 != null)) {
                return false;
            }
            if (directMapping1 != null) {
                if (!directMapping1.equals(directMapping2)) {
                    return false;
                }
            }

            List collection1 = any;
            List collection2 = ((Root)object).getAny();
            if ((collection1 == null) && (collection2 == null)) {
                return true;
            } else if ((collection1 == null) && (collection2.size() == 0)) {
                return true;
            } else if ((collection2 == null) && (collection1.size() == 0)) {
                return true;
            } else if ((collection1 == null) && (collection2.size() > 0)) {
                return false;
            } else if ((collection2 == null) && (collection1.size() > 0)) {
                return false;
            } else if (any.size() != ((Root)object).getAny().size()) {
                return false;
            } else {
                Iterator values1 = any.iterator();
                Iterator values2 = ((Root)object).getAny().iterator();

                while (values1.hasNext()) {
                    Object value1 = values1.next();
                    Object value2 = values2.next();
                    if ((value1 instanceof XMLRoot) && (value2 instanceof XMLRoot)) {
                        XMLMappingTestCases.compareXMLRootObjects((XMLRoot)value1, (XMLRoot)value2);
                    } else if (!(value1.equals(value2))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String value = "Root:\n ";
        if (any == null) {
            return value;
        }
        for (int i = 0; i < any.size(); i++) {
            value += ("==> " + any.get(i) + "\n");
        }
        return value;
    }

    public void setDirectMapping(String directMapping) {
        this.directMapping = directMapping;
    }

    public String getDirectMapping() {
        return directMapping;
    }
}
