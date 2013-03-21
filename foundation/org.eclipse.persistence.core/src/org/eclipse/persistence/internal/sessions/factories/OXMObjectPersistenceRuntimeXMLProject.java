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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.factories.NamespaceResolvableProject;
import org.eclipse.persistence.internal.sessions.factories.NamespaceResolverWithPrefixes;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 *  INTERNAL:
 *  <p><b>Purpose</b>: Defines descriptors for any EclipseLink OXM objects that may 
 *  have separate classpath dependencies from the rest of TopLink.</p> 
 *  @author  mmacivor
 *  @since   10.1.3
 */

public class OXMObjectPersistenceRuntimeXMLProject extends NamespaceResolvableProject {
    
    public OXMObjectPersistenceRuntimeXMLProject() {
        super();
    }

    public OXMObjectPersistenceRuntimeXMLProject(NamespaceResolverWithPrefixes namespaceResolverWithPrefixes) {
        super(namespaceResolverWithPrefixes);
    }

    @Override
    protected void buildDescriptors() {
        addDescriptor(buildXMLBinaryDataMappingDescriptor());
        addDescriptor(buildXMLBinaryDataCollectionMappingDescriptor());
    }
    
    public ClassDescriptor buildXMLBinaryDataMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLBinaryDataMapping.class);
        
        descriptor.getInheritancePolicy().setParentClass(XMLDirectMapping.class);
        
        XMLDirectMapping swaRefMapping = new XMLDirectMapping();
        swaRefMapping.setAttributeName("isSwaRef");
        swaRefMapping.setXPath(getPrimaryNamespaceXPath() + "is-swa-ref/text()");
        descriptor.addMapping(swaRefMapping);
        
        XMLDirectMapping mimeTypeMapping = new XMLDirectMapping();
        mimeTypeMapping.setAttributeName("mimeTypePolicy");
        mimeTypeMapping.setGetMethodName("getMimeType");
        mimeTypeMapping.setSetMethodName("setMimeType");
        mimeTypeMapping.setXPath(getPrimaryNamespaceXPath() + "mime-type/text()");
        descriptor.addMapping(mimeTypeMapping);
        
        XMLDirectMapping shouldInlineMapping = new XMLDirectMapping();
        shouldInlineMapping.setAttributeName("shouldInlineBinaryData");
        shouldInlineMapping.setXPath(getPrimaryNamespaceXPath() + "should-inline-data");
        descriptor.addMapping(shouldInlineMapping);
        
        return descriptor;
    }
    
    public ClassDescriptor buildXMLBinaryDataCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLBinaryDataCollectionMapping.class);
        
        descriptor.getInheritancePolicy().setParentClass(XMLCompositeDirectCollectionMapping.class);
        
        XMLDirectMapping swaRefMapping = new XMLDirectMapping();
        swaRefMapping.setAttributeName("isSwaRef");
        swaRefMapping.setXPath(getPrimaryNamespaceXPath() + "is-swa-ref/text()");
        descriptor.addMapping(swaRefMapping);
        
        XMLDirectMapping mimeTypeMapping = new XMLDirectMapping();
        mimeTypeMapping.setAttributeName("mimeTypePolicy");
        mimeTypeMapping.setGetMethodName("getMimeType");
        mimeTypeMapping.setSetMethodName("setMimeType");
        mimeTypeMapping.setXPath(getPrimaryNamespaceXPath() + "mime-type/text()");
        descriptor.addMapping(mimeTypeMapping);
        
        XMLDirectMapping shouldInlineMapping = new XMLDirectMapping();
        shouldInlineMapping.setAttributeName("shouldInlineBinaryData");
        shouldInlineMapping.setXPath(getPrimaryNamespaceXPath() + "should-inline-data");
        descriptor.addMapping(shouldInlineMapping);
        
        return descriptor;
    }
    

}
