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
 *     mmacivor November 17/2009 - 1.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * <p><b>Purpose</b>:Provides a wrapper for a java type to be used when creating a JAXB context. This allows for
 * additional information (such as parameter level annotations and element tag names) to be included in addition
 * to the type itself.
 * 
 * @author mmacivor
 */
public class TypeMappingInfo {

    //Indicates if a global element should be generated for this type
    protected ElementScope elementScope = ElementScope.Local;
    
    //Root element name associated with this type;
    protected QName xmlTagName;
    
    //The type to be bound;
    protected Type type;
    
    //Representing parameter level annotations that should be applied to this type.
    protected Annotation[] annotations;

    //Eclipselink XML based overrides to the parameter level annotations
    protected Element[] oxmOverrides;

    public ElementScope getElementScope() {
        return this.elementScope;
    }
    
    public void setElementScope(ElementScope scope) {
        this.elementScope = scope;
    }
    
    public QName getXmlTagName() {
        return this.xmlTagName;
    }
    
    public void setXmlTagName(QName tagName) {
        this.xmlTagName = tagName;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public void setType(Type t) {
        this.type = t;
    }
    
    public Annotation[] getAnnotations() {
        return this.annotations;
    }
    
    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }
    
    public Element[] getOXMOverrides() {
        return this.oxmOverrides;
    }
    
    public void setOXMOverrides(Element[] overrides) {
        this.oxmOverrides = overrides;
    }
    
    public enum ElementScope {
        Global,
        Local;
    }

}
