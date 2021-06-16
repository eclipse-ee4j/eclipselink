/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - December 15/2009 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.lang.annotation.Annotation;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlMimeType;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class DatahandlerWithXMLTestCases extends DatahandlerWithAnnotationsTestCases {
    @XmlMimeType(value="application/binary")
    public String xmlMimeTypeField;

    @XmlElement
    public String xmlAttachementRefField;

    public DatahandlerWithXMLTestCases(String name) throws Exception {
        super(name);
        //useLogging = true;
    }

    protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
        if(typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[1];
            TypeMappingInfo tpi = new TypeMappingInfo();
            tpi.setXmlTagName(new QName("someUri","testTagname"));
            tpi.setElementScope(ElementScope.Global);
            // set annotations - should be ignored since XML wins
            Annotation[] annotations = new Annotation[2];
            annotations[0] = getClass().getField("xmlMimeTypeField").getAnnotations()[0];
            annotations[1] = getClass().getField("xmlAttachementRefField").getAnnotations()[0];
            tpi.setAnnotations(annotations);
            tpi.setXmlElement(getXmlElement("<xml-element xml-mime-type=\"image/jpeg\" xml-attachment-ref=\"true\"/>"));
            tpi.setType(DataHandler.class);
            typeMappingInfos[0] = tpi;
        }
        return typeMappingInfos;
    }
}
