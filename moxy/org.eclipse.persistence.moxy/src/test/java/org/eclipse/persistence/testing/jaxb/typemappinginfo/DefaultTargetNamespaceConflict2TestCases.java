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
//     Denise Smith -  January 2014
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.pkg2.Thing;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.pkg3.OtherThing;

public class DefaultTargetNamespaceConflict2TestCases extends DefaultTargetNamespaceConflictTestCases{

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/conflict/thing.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/conflict/thing.json";

    public DefaultTargetNamespaceConflict2TestCases(String name) throws Exception {
        super(name);
        init();
    }

    @Override
    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setTypeMappingInfos(getTypeMappingInfos());
    }

    @Override
    public TypeMappingInfo getTypeMappingInfo(){
          return getTypeMappingInfos()[1];
    }

    @Override
    protected TypeMappingInfo[] getTypeMappingInfos() {
        if(typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[2];

            TypeMappingInfo tmi2 = new TypeMappingInfo();
            tmi2.setType(OtherThing.class);
            typeMappingInfos[0] = tmi2;

            TypeMappingInfo tmi = new TypeMappingInfo();
            tmi.setType(Thing.class);
            typeMappingInfos[1] = tmi;
        }
        return typeMappingInfos;
    }

    @Override
    protected Object getControlObject() {
        Thing thing = new Thing();
        thing.something = 10;

        JAXBElement<Thing> jbe = new JAXBElement<Thing>(new QName("root"), Thing.class, thing);
        return jbe;

    }

}

