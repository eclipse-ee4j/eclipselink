/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor = 2.1 - Initial contribution
 ******************************************************************************/
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation allows a user to specify an xpath instead of an element name. It
 * can be used in conjunction with JAXB Annotations to provide a higher level of 
 * customisation to the mappings. 
 * 
 * The following types of paths are supported:
 * <ul>
 * <li>personal-info/first-name/text() - Direct Mapping</li>
 * <li>ns:personal-info/ns:first-name/text() - Direct Mapping with Namespace</li>
 * <li>street[2]/text() - Direct Mapping positional XPath</li>
 * <li>@id - Direct Mapping to Attribute</li>
 * <li>contact-info/address - Composite Mapping</li>
 * <li>contact-info/address[2] - Composite Mapping positional XPath</li>
 * <li>. - Self Composite Object Mapping</li> 
 * </ul>
 * <p><b>Example:</b><br>
 * <code>
 * &nbsp;@XmlRootElement(name="customer")<br>
 * &nbsp;public class Customer {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlPath("personal-info/first-name/text()")<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlElement<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public String firstName<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlPath("contact-info/address[1]")<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public Address homeAddress;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlPath("contact-info/address[2]")<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public Address workAddress;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;}<br><br>
 * 
 * </code>     
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlPath {
    String value();
}
