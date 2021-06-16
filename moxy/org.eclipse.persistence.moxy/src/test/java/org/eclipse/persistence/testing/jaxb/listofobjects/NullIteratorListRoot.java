/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NullIteratorListRoot {

    private NullIteratorList<NullIteratorListRoot> elementList = new NullIteratorList<NullIteratorListRoot>();
    private NullIteratorList<String> elementSimpleList = new NullIteratorList<String>();
    private NullIteratorList anyList = new NullIteratorList();
    private NullIteratorList choiceList = new NullIteratorList();
    private NullIteratorList listList = new NullIteratorList();

    @XmlElement
    public NullIteratorList getElementList() {
        return elementList;
    }

    public void setElementList(NullIteratorList nullIteratorList) {
        this.elementList = nullIteratorList;
    }

    @XmlElement
    public NullIteratorList<String> getElementSimpleList() {
        return elementSimpleList;
    }

    public void setElementSimpleList(NullIteratorList<String> elementSimpleList) {
        this.elementSimpleList = elementSimpleList;
    }

    @XmlAnyElement
    public NullIteratorList getAnyList() {
        return anyList;
    }

    public void setAnyList(NullIteratorList anyList) {
        this.anyList = anyList;
    }

    @XmlElements({
        @XmlElement(name="foo", type=String.class),
        @XmlElement(name="bar", type=Integer.class)
    })
    public NullIteratorList getChoiceList() {
        return choiceList;
    }

    public void setChoiceList(NullIteratorList choiceList) {
        this.choiceList = choiceList;
    }

    @XmlList
    public NullIteratorList getListList() {
        return listList;
    }

    public void setListList(NullIteratorList listList) {
        this.listList = listList;
    }

}
