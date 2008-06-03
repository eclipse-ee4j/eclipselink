/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.oxm;

import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.mappings.*;

/**
 *  INTERNAL:
 *  <p><b>Purpose</b>: Defines descriptors for any TopLink OXM objects that may 
 *  have separate classpath dependencies from the rest of TopLink.</p> 
 *  @author  mmacivor
 *  @since   10.1.3
 */

public class OXMObjectPersistenceRuntimeXMLProject extends Project {
    public OXMObjectPersistenceRuntimeXMLProject() {
        addDescriptor(buildXMLBinaryDataMappingDescriptor());

        // Set the namespaces on all descriptors.
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaceResolver.put("xsd", "http://www.w3.org/2001/XMLSchema");
        namespaceResolver.put("eclipselink", "http://xmlns.oracle.com/ias/xsds/eclipselink");

        for (Iterator descriptors = getDescriptors().values().iterator(); descriptors.hasNext();) {
            XMLDescriptor descriptor = (XMLDescriptor)descriptors.next();
            descriptor.setNamespaceResolver(namespaceResolver);
        }
    }
    public ClassDescriptor buildXMLBinaryDataMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLBinaryDataMapping.class);
        
        descriptor.getInheritancePolicy().setParentClass(XMLDirectMapping.class);
        
        XMLDirectMapping swaRefMapping = new XMLDirectMapping();
        swaRefMapping.setAttributeName("isSwaRef");
        swaRefMapping.setXPath("toplink:is-swa-ref/text()");
        descriptor.addMapping(swaRefMapping);
        
        XMLDirectMapping mimeTypeMapping = new XMLDirectMapping();
        mimeTypeMapping.setAttributeName("mimeTypePolicy");
        mimeTypeMapping.setGetMethodName("getMimeType");
        mimeTypeMapping.setSetMethodName("setMimeType");
        mimeTypeMapping.setXPath("toplink:mime-type/text()");
        descriptor.addMapping(mimeTypeMapping);
        
        XMLDirectMapping shouldInlineMapping = new XMLDirectMapping();
        shouldInlineMapping.setAttributeName("shouldInlineBinaryData");
        shouldInlineMapping.setXPath("toplink:should-inline-data");
        descriptor.addMapping(shouldInlineMapping);
        
        return descriptor;
    }
    
}
