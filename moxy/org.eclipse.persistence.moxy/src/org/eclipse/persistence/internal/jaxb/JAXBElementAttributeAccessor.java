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
* mmacivor - June 05/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.oxm.XMLRoot;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBElementAttributeAccessor extends AttributeAccessor {
	private AttributeAccessor nestedAccessor;
	private ContainerPolicy containerPolicy;
	private boolean isContainer;
	private Map<QName, Class> qNamesToScopes;
	private final static String JAXB_ELEMENT_CLASSNAME = "javax.xml.bind.JAXBElement";
	
	public JAXBElementAttributeAccessor(AttributeAccessor nestedAccessor) {
		this.nestedAccessor = nestedAccessor;
		this.isContainer = false;
		qNamesToScopes = new HashMap<QName, Class>();
	}
	public JAXBElementAttributeAccessor(AttributeAccessor nestedAccessor, ContainerPolicy containerPolicy) {
		this.nestedAccessor = nestedAccessor;
		this.containerPolicy = containerPolicy;
		this.isContainer = true;
		qNamesToScopes = new HashMap<QName, Class>();
	}
	
	public Object getAttributeValueFromObject(Object object) {
		Object value = nestedAccessor.getAttributeValueFromObject(object);
		//Swap JAXBElements for XMLRoots
		//May need a better way to do this for perf.
		if(isContainer) {
			Object results = containerPolicy.containerInstance(containerPolicy.sizeFor(value));
			Object iterator = containerPolicy.iteratorFor(value);
			while(containerPolicy.hasNext(iterator)) {
				Object next = containerPolicy.next(iterator, null);
				if(next instanceof JAXBElement) {
					JAXBElement element = (JAXBElement)next;
					XMLRoot root = new XMLRoot();
					root.setLocalName(element.getName().getLocalPart());
					root.setNamespaceURI(element.getName().getNamespaceURI());
					root.setObject(element.getValue());
					containerPolicy.addInto(root, results, null);
				} else {
					containerPolicy.addInto(next, results, null);
				}
			}
			value = results;
		} else {
			if(value !=null && value.getClass().getName().equals(JAXB_ELEMENT_CLASSNAME)) {
				JAXBElement element = (JAXBElement)value;
				XMLRoot root = new XMLRoot();
				root.setLocalName(element.getName().getLocalPart());
				root.setNamespaceURI(element.getName().getNamespaceURI());
				root.setObject(element.getValue());
				value = root;
			}
		}
		return value;
	}
	
	public void setAttributeValueInObject(Object object, Object value) {
		Object attributeValue = value;
		if(isContainer) {
			Object results = containerPolicy.containerInstance(containerPolicy.sizeFor(attributeValue));
			Object iterator = containerPolicy.iteratorFor(attributeValue);
			while(containerPolicy.hasNext(iterator)) {
				Object next = containerPolicy.next(iterator, null);
				Object objectToAdd = unwrapObject(next);
				containerPolicy.addInto(objectToAdd, results, null);
			}
			attributeValue = results;
		} else {		
			attributeValue = unwrapObject(attributeValue);

		}
		nestedAccessor.setAttributeValueInObject(object, attributeValue);
	}
	
    public void initializeAttributes(Class theJavaClass) throws DescriptorException {
    	nestedAccessor.initializeAttributes(theJavaClass);
    }
    
    private Object unwrapObject(Object originalObject){
    	if(originalObject instanceof XMLRoot) {
			XMLRoot root = (XMLRoot)originalObject;			
			QName name = new QName(root.getNamespaceURI(), root.getLocalName());
			Object value = root.getObject();
			if(value == null){
				return createJAXBElement(name, Object.class, root.getObject());
			}else{
				return createJAXBElement(name, root.getObject().getClass(), root.getObject());
			}			
		} else if(originalObject instanceof WrappedValue){
			Object unwrappedValue = ((WrappedValue)originalObject).getWrappedValue();					
			Class theClass = ((WrappedValue)originalObject).getWrappedValueClass();
			return createJAXBElement(((WrappedValue)originalObject).getQName(), theClass, unwrappedValue);			
		} else {			
			return originalObject;		
		}
    }	    
    
    private JAXBElement createJAXBElement(QName qname, Class theClass, Object value){
    	if(value != null && value instanceof JAXBElement){
    		return (JAXBElement)value;
    	}
    	if(ClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(theClass)){
    		theClass = ClassConstants.XML_GREGORIAN_CALENDAR;
    	}else if(ClassConstants.DURATION.isAssignableFrom(theClass)){
    		theClass = ClassConstants.DURATION;
    	}
    	Class scopeClass = qNamesToScopes.get(qname);
    	return new JAXBElement(qname, theClass, scopeClass, value);
    }
	
	public Map<QName, Class> getQNamesToScopes() {
		return qNamesToScopes;
	}
	public void setQNamesToScopes(Map<QName, Class> namesToScopes) {
		qNamesToScopes = namesToScopes;
	}  
}
