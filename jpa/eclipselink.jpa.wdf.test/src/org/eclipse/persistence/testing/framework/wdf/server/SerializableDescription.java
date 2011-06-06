/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/
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
