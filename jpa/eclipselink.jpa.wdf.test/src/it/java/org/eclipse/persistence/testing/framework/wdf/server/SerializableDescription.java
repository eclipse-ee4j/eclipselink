/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation
package org.eclipse.persistence.testing.framework.wdf.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Description;

/**
 * Serializable version of org.junit.runner.Description.
 */
public class SerializableDescription implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String displayName;
    private final List<SerializableDescription> children;

    private SerializableDescription(String theDisplayName, List<SerializableDescription> theChildren) {
        displayName = theDisplayName;
        children = theChildren;
    }

    /**
     * Create a SerializableDescription objects from an org.junit.runner.Description objects
     * @param description the org.junit.runner.Description object to be converted
     * @return the SerializableDescription object created from an org.junit.runner.Description object
     */
    @SuppressWarnings("unchecked")
    public static SerializableDescription create(Description description) {
        final List<SerializableDescription> children;
        if (description.getChildren() != null) {
            children = new ArrayList<SerializableDescription>();
            for (Description child : description.getChildren()) {
                children.add(create(child));
            }
        } else {
            children = Collections.EMPTY_LIST;
        }
        return new SerializableDescription(description.getDisplayName(), children);

    }

    /**
     * Restore an org.junit.runner.Description from this SerializableDescription object
     * @return the restored org.junit.runner.Description object
     */
    public Description restore() {
        Description restored = Description.createSuiteDescription(displayName);

        if (children != null) {
            for (SerializableDescription child : children) {
                restored.addChild(child.restore());
            }
        }

        return restored;
    }

}
