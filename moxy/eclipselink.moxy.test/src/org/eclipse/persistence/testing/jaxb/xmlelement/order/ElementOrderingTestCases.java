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
//     Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.xmlelement.order;

import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ElementOrderingTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/order/album.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/order/album.json";

    public ElementOrderingTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[]{AlbumInfo.class};
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    public Class getUnmarshalClass(){
        return AlbumInfo.class;
    }

    protected Object getJSONReadControlObject() {
        JAXBElement elem = new JAXBElement<AlbumInfo>(new QName(""), AlbumInfo.class, (AlbumInfo) getWriteControlObject());
        return elem;
    }

    protected Object getControlObject() {
        JAXBElement elem = new JAXBElement<AlbumInfo>(new QName("albumInfo"), AlbumInfo.class, (AlbumInfo) getWriteControlObject());
        return elem;
    }


    @Override
    public Object getWriteControlObject() {

        AlbumInfo albumInfo = new AlbumInfo();
        albumInfo.setResultCount(45);

        ArrayList<Album> albums = new ArrayList<Album>();
        Album album = new Album();
        album.setWrapperType("first");
        albums.add(album);
        Album album2 = new Album();
        album2.setWrapperType("second");
        albums.add(album2);
        albumInfo.setAlbums(albums);

        return albumInfo;
    }

    //
    public void testUnmarshallerHandler(){}
}
