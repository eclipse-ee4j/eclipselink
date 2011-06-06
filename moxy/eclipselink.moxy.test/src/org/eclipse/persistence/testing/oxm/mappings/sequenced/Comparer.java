/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

import java.util.List;
import org.w3c.dom.Node;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.sequenced.Setting;
import org.eclipse.persistence.platform.xml.XMLComparer;

public class Comparer {
    
    private static final XMLComparer xmlComparer = new XMLComparer();
        
    public static boolean equals(Object control, Object test) {
        if(null == control) {
            return control == test;
        } else {
            if(control instanceof Node && test instanceof Node) {
                return xmlComparer.isNodeEqual((Node)control, (Node)test);
            } else if(control instanceof XMLRoot && test instanceof XMLRoot) {
                return equals((XMLRoot) control, (XMLRoot) test);
            } else {
                return control.equals(test);
            }
        }
    }

    public static boolean equals(List<Setting> control, List<Setting> test) {
        if(control == test) {
            return true;
        }
        if(null == test) {
            return false;
        }
        int controlSize = control.size();
        if(controlSize != test.size()) {
            return false;
        }
        for(int x=0; x<controlSize; x++) {
            if(!equals(control.get(x), test.get(x))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean equals(Setting control, Setting test) {
        if(!Comparer.equals(control.getName(), test.getName())) {
            return false;
        }
        if(!Comparer.equals(control.getNamespaceURI(), test.getNamespaceURI())) {
            return false;
        } 
        try {
            if(!Comparer.equals((List<Setting>) control.getChildren(), (List<Setting>)test.getChildren())) {
                return false;
            }
        } catch(ClassCastException e) {
            return false;
        }
        if(!Comparer.equals(control.getValue(), test.getValue())) {
            return false;
        }
        return true;
    }
    
    public static boolean equals(XMLRoot control, XMLRoot test) {
        if(!Comparer.equals(control.getLocalName(), test.getLocalName())) {
            return false;
        }
        if(!Comparer.equals(control.getNamespaceURI(), test.getNamespaceURI())) {
            return false;
        } 
        return equals(control.getObject(), test.getObject());
    }
    
}
