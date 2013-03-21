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
 *     Denise Smith - EclipseLink 2.3 - Initial Implementation
 ******************************************************************************/  
package org.eclipse.persistence.oxm;

/**
 * Interface used when converting from XML to Java names.
 */
public interface XMLNameTransformer {
	/**
	 * Method called when creating a simpletype or complextype from a class
	 * @param name - The fully qualified class name as taken from theClass.getName()
	 */
	public String transformTypeName(String name);
	
	/**
	 * Method called when creating an element from a Java field or method
	 * @param name - unmodified field name or if this was from a getter or setter method 
	 * the "get" or "set" will be automatically removed and the first letter will be made lowercase
	 * 
	 * Example: if the method getFirstName was annotated with @XmlElement the name passed in to this method would be "firstName"
	 */
	public String transformElementName(String name);
	
	/**
     * Method called when creating an attribute from a Java field
	 * @param name - attribute name from the class
	 */
	public String transformAttributeName(String name);
	
	/**
	 * Method called when creating a simpletype or complextype from a class
	 * @param name - The fully qualified class name as taken from theClass.getName()
	 */
	public String transformRootElementName(String name);	

}
