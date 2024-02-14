/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     rbarkhouse - 2009-11-25 14:23:25 - v2.0 - initial implementation
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to map a back-pointer during the unmarshal operation.
 * When configuring an {@literal @XmlInverseReference}, the "<em>mappedBy</em>" attribute must
 * be set to the field on the reference class that maps to this field.
 *
 * <p><b>Example:</b>
 * {@snippet :
 *  @XmlRootElement
 *  public class Employee {
 *      ...
 *      @XmlElementWrapper(name="phone-numbers")
 *      @XmlElement(name="number")
 *      public List<PhoneNumber> phoneNumbers;
 *      ...
 *  }
 *
 *  public class PhoneNumber {
 *      ...
 *      @XmlInverseReference(mappedBy="phoneNumbers")
 *      public Employee owningEmployee;
 *      ...
 *  }
 * }
 *
 * <p>By default, using {@literal @XmlInverseReference} will make the property act the
 * same as {@literal @XmlTransient} for the marshal operation. You can make the
 * property writeable by combining it with {@literal @XmlElement}:</p>

 * {@snippet :
 *  public class PhoneNumber {
 *     ...
 *     @XmlInverseReference(mappedBy="phoneNumbers")
 *     @XmlElement
 *     public Employee owningEmployee;
 *     ...
 *  }
 * }
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlInverseReference {

    String mappedBy();

}
