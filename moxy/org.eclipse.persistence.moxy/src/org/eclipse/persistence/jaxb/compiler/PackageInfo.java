/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.compiler;

import java.util.HashMap;

import org.eclipse.persistence.internal.jaxb.AccessorFactoryWrapper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessOrder;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLNameTransformer;

/**
 * INTERNAL:
 * Represents the the package level annotations from a specific package. 
 * @author mmacivor
 *
 */
public class PackageInfo {
    private XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
    private XmlAccessOrder accessOrder = XmlAccessOrder.UNDEFINED;
    private XMLNameTransformer xmlNameTransformer;
    private NamespaceInfo namespaceInfo;
    private HashMap<String, JavaClass> packageLevelAdaptersByClass;
    private AccessorFactoryWrapper accessorFactory;

    public PackageInfo() {
    	packageLevelAdaptersByClass = new HashMap<String, JavaClass>();
    }
    
    public HashMap<String, JavaClass> getPackageLevelAdaptersByClass() {
        return packageLevelAdaptersByClass;
    }

    public void setPackageLevelAdaptersByClass(
        HashMap<String, JavaClass> packageLevelAdaptersByClass) {
        this.packageLevelAdaptersByClass = packageLevelAdaptersByClass;
    }
	
    public void setAccessType(XmlAccessType accessType) {
        this.accessType = accessType;
    }
    public XmlAccessType getAccessType() {
        return accessType;
    }
    public void setAccessOrder(XmlAccessOrder accessOrder) {
        this.accessOrder = accessOrder;
    }
    public XmlAccessOrder getAccessOrder() {
        return accessOrder;
    }
    public void setXmlNameTransformer(XMLNameTransformer xmlNameTransformer) {
        this.xmlNameTransformer = xmlNameTransformer;
    }
    public XMLNameTransformer getXmlNameTransformer() {
        return xmlNameTransformer;
    }
    public void setNamespaceInfo(NamespaceInfo namespaceInfo) {
        this.namespaceInfo = namespaceInfo;
    }
    public NamespaceInfo getNamespaceInfo() {
        return namespaceInfo;
    }
    
    public String getNamespace() {
        return namespaceInfo.getNamespace();
    }
    
    public void setNamespace(String ns) {
        this.namespaceInfo.setNamespace(ns);
    }
    
    public boolean isAttributeFormQualified() {
        return this.namespaceInfo.isAttributeFormQualified();
    }
    
    public void setAttributeFormQualified(boolean b) {
        this.namespaceInfo.setAttributeFormQualified(b);
    }

    public boolean isElementFormQualified() {
        return this.namespaceInfo.isElementFormQualified();
    }
    
    public void setElementFormQualified(boolean b) {
        this.namespaceInfo.setElementFormQualified(b);
    }
    
    public NamespaceResolver getNamespaceResolver() {
        return this.namespaceInfo.getNamespaceResolver();
    }
    
    public void setNamespaceResolver(NamespaceResolver resolver) {
        this.namespaceInfo.setNamespaceResolver(resolver);
    }
    
    public String getLocation() {
        return this.namespaceInfo.getLocation();
    }

    public void setLocation(String location) {
        this.namespaceInfo.setLocation(location);
    }

    public AccessorFactoryWrapper getAccessorFactory() {
        return accessorFactory;
    }

    public void setAccessorFactory(AccessorFactoryWrapper accessorFactory) {
        this.accessorFactory = accessorFactory;
    }    
    
    

}
