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
 *     mmacivor November 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.w3c.dom.Element;

/**
 * <p><b>Purpose</b>:  Provides a wrapper for a java type to be used when creating a JAXB context. This allows for
 * additional information (such as parameter level annotations and element tag names) to be included in addition
 * to the type itself.
 * 
 * @author mmacivor
 */
public class TypeMappingInfo {

    private ElementScope elementScope = ElementScope.Local;
    private QName xmlTagName;
    private Type type;
    private Annotation[] annotations;
    private Element xmlElement;
    private boolean nillable;
    private Descriptor xmlDescriptor;
  
    private QName schemaType;

    /**
     * INTERNAL:
     * Indicates the schema type to be used during marshal
     */
   	public QName getSchemaType() {
   		return schemaType;
   	}    
    
	/**
     * Indicates if a global element should be generated for this type.
     */
    public ElementScope getElementScope() {
        return this.elementScope;
    }

    public void setElementScope(ElementScope scope) {
        this.elementScope = scope;
    }

    /**
     * Return if the global element will be marked as nillable
     * @return
     */
    public boolean isNillable() {
		return nillable;
	}

    /**
     * Set if the global element should be marked as nillable
     * @param nillable
     */
	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}
    
    /**
     * Root element name associated with this type;
     */
    public QName getXmlTagName() {
        return this.xmlTagName;
    }

    public void setXmlTagName(QName tagName) {
        this.xmlTagName = tagName;
    }

    /**
     * The type to be bound.
     */
    public Type getType() {
        return this.type;
    }

    public void setType(Type t) {
        this.type = t;
        if(type instanceof Class){
            if (((Class)type) == CoreClassConstants.ABYTE || ((Class)type) == CoreClassConstants.APBYTE || 
        	   ((Class)type).getCanonicalName().equals("javax.activation.DataHandler")) {
        	   schemaType = Constants.BASE_64_BINARY_QNAME;
        	}
        }
    }

    /**
     * Representing parameter level annotations that should be applied to this 
     * type.
     */
    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public enum ElementScope {
        Global,
        Local;
    }

    /**
     * INTERNAL
     * Returns the Descriptor associated with this TypeMappingInfo.  This is
     * set when the metadata is initialized.
     */
    public Descriptor getXmlDescriptor() {
        return xmlDescriptor;
    }

    /**
     * INTERNAL
     * Sets the Descriptor associated with this TypeMappingInfo.  This is
     * set when the metadata is initialized.
     */
    public void setXmlDescriptor(Descriptor xmlDescriptor) {
        this.xmlDescriptor = xmlDescriptor;
    }

    /**
     * Returns the xml-element as a <code>Element</code> which represents the
     * parameter level annotations that should be applied to this type.
     * 
     * @return <code>Element</code> which represents the parameter level
     *         annotations that should be applied to this type if set, otherwise
     *         null
     */
    public Element getXmlElement() {
        return xmlElement;
    }

    /**
     * Set the xml-element <code>Element</code> representing parameter level
     * annotations that should be applied to this type. If
     * <code>xmlElement</code> is non-null, any annotations set on this instance
     * will be completely ignored.
     * 
     * The following XML representations of parameter level annotations will be
     * supported:
     * <ul>
     * <li>xml-element</li> <li>xml-attachment-ref</li> <li>xml-list</li> <li>
     * xml-mime-type</li> <li>xml-java-type-adapter</li>
     * </ul>
     * 
     * @param xmlElement
     *            <code>Element</code> created from an xml-element
     * 
     * @see org.w3c.dom.Element
     * @see org.eclipse.persistence.jaxb.xmlmodel.XmlElement
     */
    public void setXmlElement(Element xmlElement) {
        this.xmlElement = xmlElement;
    }
}