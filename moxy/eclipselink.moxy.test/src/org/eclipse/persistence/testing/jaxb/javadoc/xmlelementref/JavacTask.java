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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementref;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="javac")
public class JavacTask extends Task{
    @XmlAttribute
    public String name;


 public String getName(){
     return name;
 }
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
