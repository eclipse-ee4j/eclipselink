/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.compiler;

import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessOrder;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType;
import org.eclipse.persistence.oxm.NamespaceResolver;

/**
 * INTERNAL:
 * Represents the the package level annotations from a specific package. 
 * If more than one package specifies the same target namespace, their respective
 * PackageInfo objects will share the same NamespaceInfo object.
 */
public class PackageInfo {
    private XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
    private XmlAccessOrder accessOrder = XmlAccessOrder.UNDEFINED;
    private NamespaceInfo namespaceInfo;
    
    public PackageInfo() {
        
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
    
    

}
