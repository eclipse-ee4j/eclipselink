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
//     Matt MacIvor - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RootObject {

    @XmlElement
    private String title;

    @XmlElement
    @XmlJavaTypeAdapter(Adapter.class)
    private Map<String, Object> data;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public static class Adapter extends XmlAdapter<Object, Map<String, Object>> {

        @Override
        public Object marshal(Map<String, Object> map) throws Exception {

            // TODO

            XMLPlatform platform = XMLPlatformFactory.getInstance().getXMLPlatform();
            Document doc = platform.createDocument();
            Element elem = doc.createElement("data");

            Element foo = doc.createElement("foo");
            Text txt = doc.createTextNode("bar");
            foo.appendChild(txt);
            elem.appendChild(foo);
            doc.appendChild(elem);

            return elem;

        }

        @Override
        public Map<String, Object> unmarshal(Object obj) throws Exception {
            Element e = (Element) obj;

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("foo1", "bar1");
            map.put("foo2", "bar2");

            return map;
        }

    }

    public boolean equals(Object obj) {
        RootObject ro = (RootObject)obj;

        return this.title.equals(ro.title) && this.data.equals(ro.data);
    }

}
