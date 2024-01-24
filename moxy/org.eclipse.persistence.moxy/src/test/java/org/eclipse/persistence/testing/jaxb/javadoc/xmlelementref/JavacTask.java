/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementref;


import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="javac")
public class JavacTask extends Task{
    @XmlAttribute
    public String name;


 @Override
 public String getName(){
     return name;
 }
 @Override
 public void setName(String name){
     this.name = name;
 }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JavacTask)) {
            return false;
        }
        JavacTask javacTask = (JavacTask) obj;

        return javacTask.name.equals(this.name);
    }

}
