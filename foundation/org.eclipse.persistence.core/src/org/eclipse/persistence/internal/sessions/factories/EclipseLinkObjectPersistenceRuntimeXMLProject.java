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
package org.eclipse.persistence.internal.sessions.factories;

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.DatabaseLogin;

/**
 * INTERNAL: Define the EclipseLInk OX project and descriptor information to read an EclipseLink
 * project from an XML file. Note any changes must be reflected in the EclipseLink XML schema.
 */
public class EclipseLinkObjectPersistenceRuntimeXMLProject extends ObjectPersistenceRuntimeXMLProject_11_1_1 {

    /**
     * INTERNAL: Return a new descriptor project.
     */
    public EclipseLinkObjectPersistenceRuntimeXMLProject() {
        super();
    }

    @Override
    protected void buildNamespaceResolver() {
        ns = new NamespaceResolverWithPrefixes();
        ns.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        ns.put("xsd", "http://www.w3.org/2001/XMLSchema");
        ns.put(ECLIPSELINK_PREFIX, getPrimaryNamespace());
        ns.setDefaultNamespaceURI(getPrimaryNamespace());
    }

    @Override
    public void buildDescriptors() {
        super.buildDescriptors();
        
        // Any new mappings go after call to super.buildDescriptors();
    }

    @Override
    public String getPrimaryNamespacePrefix() {
        return null;
    }

    @Override
    public String getPrimaryNamespace() {
        return ECLIPSELINK_NAMESPACE;
    }

    @Override
    public String getSecondaryNamespacePrefix() {
        return null;
    }

    @Override
    public String getSecondaryNamespace() {
        return ECLIPSELINK_NAMESPACE;
    }

    @Override
    protected ClassDescriptor buildProjectDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildProjectDescriptor();        
        descriptor.setSchemaReference(new XMLSchemaClassPathReference("xsd/eclipse_persistence_map_1_0.xml"));

        return descriptor;
    }

    @Override
    protected ConstantTransformer getConstantTransformerForProjectVersionMapping() {
        return new ConstantTransformer(DatabaseLogin.getVersion());
    }
    
    protected ClassDescriptor buildXMLChoiceFieldToClassAssociationDescriptor() {
        ClassDescriptor descriptor = super.buildXMLChoiceFieldToClassAssociationDescriptor();
        
        XMLCompositeObjectMapping converterMapping = new XMLCompositeObjectMapping();
        converterMapping.setAttributeName("converter");
        converterMapping.setXPath(getPrimaryNamespacePrefix() + "value-converter");
        converterMapping.setReferenceClass(Converter.class);
        
        descriptor.addMapping(converterMapping);
        
        return descriptor;
    }
    

}