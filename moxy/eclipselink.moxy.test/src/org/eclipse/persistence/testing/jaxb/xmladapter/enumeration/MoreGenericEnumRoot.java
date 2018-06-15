/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="root")
public class MoreGenericEnumRoot {

    @XmlJavaTypeAdapter(ByteToExampleEnumAdapter.class)
    public Byte single;

    @XmlJavaTypeAdapter(ByteToExampleEnumAdapter.class)
    public List<Byte> multi;

    public MoreGenericCardSuit cardSuit;

    public List<MoreGenericCardSuit> cardSuits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoreGenericEnumRoot enumRoot = (MoreGenericEnumRoot) o;

        if (cardSuit != enumRoot.cardSuit) return false;
        if (cardSuits != null ? !cardSuits.equals(enumRoot.cardSuits) : enumRoot.cardSuits != null) return false;
        if (multi != null ? !multi.equals(enumRoot.multi) : enumRoot.multi != null) return false;
        if (single != null ? !single.equals(enumRoot.single) : enumRoot.single != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = single != null ? single.hashCode() : 0;
        result = 31 * result + (multi != null ? multi.hashCode() : 0);
        result = 31 * result + (cardSuit != null ? cardSuit.hashCode() : 0);
        result = 31 * result + (cardSuits != null ? cardSuits.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnumRoot{" +
                "single=" + single +
                ", multi=" + multi +
                ", cardSuit=" + cardSuit +
                ", cardSuits=" + cardSuits +
                '}';
    }
}
