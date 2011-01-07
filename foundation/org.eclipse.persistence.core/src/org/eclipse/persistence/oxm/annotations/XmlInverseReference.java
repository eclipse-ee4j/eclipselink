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
 *     rbarkhouse - 2009-11-25 14:23:25 - v2.0 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to map a back-pointer.  When configuring an 
 * XmlInverseReference, the "mappedBy" attribute must be set to the field
 * on the reference class that maps to this field.  For example:<br><br>
 * 
 * <code>
 * &nbsp;@XmlRootElement(name="employee")<br>
 * &nbsp;public class Employee {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlElementWrapper(name="phone-numbers")<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlElement(name="number")<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public List&lt;PhoneNumber&gt; phoneNumbers;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;}<br><br>
 * 
 * &nbsp;public class PhoneNumber {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlInverseReference(mappedBy="phoneNumbers")<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public Employee owningEmployee;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;}<br>
 * </code>     
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlInverseReference {

    String mappedBy();
    
}