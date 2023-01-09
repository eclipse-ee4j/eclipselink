/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.asm.internal.platform.eclipselink;

import org.eclipse.persistence.internal.libraries.asm.Attribute;

public class AttributeImpl implements org.eclipse.persistence.asm.Attribute {

    private class ElAttribute extends Attribute {

        public ElAttribute(String type) {
            super(type);
        }
    }

    private Attribute attribute;

    public AttributeImpl(Attribute attribute) {
        this.attribute = attribute;
    }

    public AttributeImpl(String type) {
        this.attribute = new ElAttribute(type);
    }

    @Override
    public <T> T unwrap() {
        return (T)this.attribute;
    }

}
