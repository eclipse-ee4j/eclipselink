/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Wrap the value inside a CDATA section.  Normally JAXB will escape certain
 * characters in a string during a marshal operation:
 * <ul>
 * <li>{@code & (as &amp;)}</li>
 * <li>{@code < (as &lt;)}</li>
 * <li>{@code " (as &quot;)}</li>
 * </ul>
 * This means a property foo with string value {@code "1 < 2"} without
 * {@code @XmlCDATA} will be marshalled as {@code <foo>1 &lt; 2</foo>}. When
 * {@code @XmlCDATA} is used the content is marshalled as
 * {@code <foo><![CDATA[1 < 2]]></foo>}.<p>
 * <b>Example</b>
 * <pre>
 * import javax.xml.bind.annotation.XmlRootElement;
 * import org.eclipse.persistence.oxm.annotations.XmlCDATA;
 *
 * &#64;XmlRootElement()
 * public class Root {
 *     private String foo;
 *
 *     &#64;XmlCDATA
 *     public String getFoo() {
 *         return foo;
 *     }
 *
 *     public void setFoo(String foo) {
 *         this.foo = foo;
 *     }
 * }
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlCDATA {}
