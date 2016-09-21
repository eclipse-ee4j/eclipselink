/*******************************************************************************
 * Copyright (c) 2012, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class WithXmlRootElementRoot {//implements Comparable<WithXmlRootElementRoot>{

    private String name;
    private UUID uuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if(null == o || o.getClass() != this.getClass()) {
            return false;
        }
        WithXmlRootElementRoot test = (WithXmlRootElementRoot) o;
        if(null == name) {
            return null == test.getName();
        } else if (!name.equals(test.getName())){
            return false;
        }
        if(null == uuid) {
            return null == test.getUuid();
        } else {
            return uuid.equals(test.getUuid());
        }
    }
/*
    @Override
    public int compareTo(WithXmlRootElementRoot o) {
        boolean isEqual = this.equals(o);
        if(name.equals("FOO")){
            return -1;
        }else if(name.equals("BAR")){
            return 1;
        }
        return 1;
    }
*/
}
