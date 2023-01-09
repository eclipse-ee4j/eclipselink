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
package org.eclipse.persistence.asm.internal.platform.ow2;

import org.objectweb.asm.Label;

public class LabelImpl implements org.eclipse.persistence.asm.Label {

    private class OW2Label extends Label {

        private org.eclipse.persistence.asm.Label outerObject;

        public OW2Label(org.eclipse.persistence.asm.Label outerObject) {
            this.outerObject = outerObject;
        }
    }

    private Label ow2Label;

    public LabelImpl() {
        this.ow2Label = new OW2Label(this);
    }

    public LabelImpl(Label label) {
        this.ow2Label = label;
    }

        @Override
    public <T> T unwrap() {
        return (T)this.ow2Label;
    }

}
