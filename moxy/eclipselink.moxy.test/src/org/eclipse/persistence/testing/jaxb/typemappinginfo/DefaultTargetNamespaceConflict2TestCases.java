/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith -  January 2014
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.pkg2.Thing;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.pkg3.OtherThing;

public class DefaultTargetNamespaceConflict2TestCases extends DefaultTargetNamespaceConflictTestCases{

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/conflict/thing.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/conflict/thing.json";

    public DefaultTargetNamespaceConflict2TestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setTypeMappingInfos(getTypeMappingInfos());
    }

    public TypeMappingInfo getTypeMappingInfo(){
          return getTypeMappingInfos()[1];
    }

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

    protected Object getControlObject() {
        Thing thing = new Thing();
        thing.something = 10;

        JAXBElement<Thing> jbe = new JAXBElement<Thing>(new QName("root"), Thing.class, thing);
        return jbe;

    }

}

