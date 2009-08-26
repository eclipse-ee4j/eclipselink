/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - May 8/2009 
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;

public class CompositeObjectSelfNoRefClassNSProject extends Project {
    public CompositeObjectSelfNoRefClassNSProject() {
        addDescriptor(getEmployeeDescriptor());        
    }

    protected XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("ns0:root");

        XMLCompositeObjectMapping theObjectMapping = new XMLCompositeObjectMapping();
        theObjectMapping.setAttributeName("theObject");
        theObjectMapping.setXPath(".");                
        descriptor.addMapping(theObjectMapping);

        NamespaceResolver nr = new NamespaceResolver();
        nr.put("ns0", "namespace1");
        descriptor.setNamespaceResolver(nr);
        return descriptor;
    }
}
