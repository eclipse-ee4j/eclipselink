/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - Novemner 11/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource;

public class Foo {
    public String id;
    
    public Foo() {}
    public Foo(String id) {
        this.id = id;
    }
    
    public boolean equals(Object o) {
        Foo f;
        try {
            f = (Foo) o;
        } catch (ClassCastException e) {
            return false;
        }
        return this.id.equals(f.id);
    }
}
