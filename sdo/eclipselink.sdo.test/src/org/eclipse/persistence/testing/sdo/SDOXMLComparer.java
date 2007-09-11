/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo;

import java.util.List;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Attr;

public class SDOXMLComparer extends XMLComparer {
    public SDOXMLComparer() {
      super();
    }

    protected boolean isAttributeEqual(Attr control, Attr test) {
        String name = control.getLocalName();
        if (name.equals("delete") || name.equals("create") || name.equals("unset")) {
            String controlValue = control.getNodeValue();
            String testValue = test.getNodeValue();
            if (controlValue.length() == testValue.length()) {
                try {
                    List controlList = (List)XMLConversionManager.getDefaultXMLManager().convertObject(controlValue, List.class);
                    List testList = (List)XMLConversionManager.getDefaultXMLManager().convertObject(testValue, List.class);
                    if (controlList.size() == testList.size()) {
                        if ((controlList.containsAll(testList)) && testList.containsAll(controlList)) {
                            return true;
                        }
                    }
                    return super.isAttributeEqual(control, test);
                } catch (ConversionException ce) {                    
                    ce.printStackTrace();
                    return false;
                }
            } else {
                return super.isAttributeEqual(control, test);
            }
        } else {
            return super.isAttributeEqual(control, test);
        }    
    }
}