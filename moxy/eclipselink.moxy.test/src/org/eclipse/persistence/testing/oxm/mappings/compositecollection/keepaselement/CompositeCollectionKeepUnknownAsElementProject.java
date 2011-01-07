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
 *     rbarkhouse - 2009-04-14 - 2.0 - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.oxm.mappings.compositecollection.keepaselement;

import java.util.ArrayList;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.RootKeepAsElement;

public class CompositeCollectionKeepUnknownAsElementProject extends Project {

    public CompositeCollectionKeepUnknownAsElementProject() {
        this.addDescriptor(buildDocDescriptor());
        this.addDescriptor(buildElemDescriptor());        
    }

    public ClassDescriptor buildDocDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Doc.class);
        descriptor.setDefaultRootElement("doc");

        XMLCompositeCollectionMapping elemMapping = new XMLCompositeCollectionMapping();
        elemMapping.setAttributeName("elem");
        elemMapping.setGetMethodName("getElem");
        elemMapping.setSetMethodName("setElem");
        elemMapping.setXPath("elem");        
        elemMapping.useCollectionClass(ArrayList.class);
        elemMapping.setReferenceClass(Elem.class);
        
        XMLCompositeCollectionMapping elem1Mapping = new XMLCompositeCollectionMapping();
        elem1Mapping.setAttributeName("elem1");
        elem1Mapping.setGetMethodName("getElem1");
        elem1Mapping.setSetMethodName("setElem1");
        elem1Mapping.setXPath("elem1");        
        ((XMLField)elem1Mapping.getField()).setIsTypedTextField(true);
        elem1Mapping.setReferenceClass(null);
        elem1Mapping.setReferenceClassName(null); 
        elem1Mapping.useCollectionClass(ArrayList.class);
        elem1Mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        
        descriptor.addMapping(elemMapping);
        descriptor.addMapping(elem1Mapping);        
        
        return descriptor;
    }

    public ClassDescriptor buildElemDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Elem.class);

        XMLCompositeCollectionMapping elemMapping = new XMLCompositeCollectionMapping();
        elemMapping.setAttributeName("elem");
        elemMapping.setGetMethodName("getElem");
        elemMapping.setSetMethodName("setElem");
        elemMapping.setXPath("elem");
        elemMapping.setReferenceClass(null);
        elemMapping.setReferenceClassName(null);
        elemMapping.useCollectionClass(ArrayList.class);
        elemMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);        
        
        XMLCompositeCollectionMapping elem1Mapping = new XMLCompositeCollectionMapping();
        elem1Mapping.setAttributeName("elem1");
        elem1Mapping.setGetMethodName("getElem1");
        elem1Mapping.setSetMethodName("setElem1");
        elem1Mapping.setXPath("elem1");        
        elem1Mapping.setReferenceClass(null);
        elem1Mapping.setReferenceClassName(null);
        elem1Mapping.useCollectionClass(ArrayList.class);
        elem1Mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);

        descriptor.addMapping(elemMapping);
        descriptor.addMapping(elem1Mapping);        
        
        return descriptor;
    }
    
}