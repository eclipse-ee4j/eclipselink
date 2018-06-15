/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.rs.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Foo<T> {

    private String value;
    private T ref;

    public Foo() {
    }

    public Foo(final String value, final T ref) {
        this.value = value;
        this.ref = ref;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public T getRef() {
        return ref;
    }

    public void setRef(final T ref) {
        this.ref = ref;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Foo foo = (Foo) o;

        if (ref != null ? !ref.equals(foo.ref) : foo.ref != null) {
            return false;
        }
        if (value != null ? !value.equals(foo.value) : foo.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (ref != null ? ref.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Foo{" +
                "value='" + value + '\'' +
                ", ref=" + ref +
                '}';
    }
}
