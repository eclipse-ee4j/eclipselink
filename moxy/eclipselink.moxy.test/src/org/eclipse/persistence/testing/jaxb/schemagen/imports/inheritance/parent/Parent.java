/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Denise Smith- 2.4
package org.eclipse.persistence.testing.jaxb.schemagen.imports.inheritance.parent;

import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="parentnamespace")
public class Parent {
   public String parentThing;
   public boolean equals(Object obj){
       if(obj instanceof Parent){
           return parentThing.equals(((Parent)obj).parentThing);
       }
       return false;
   }
}
