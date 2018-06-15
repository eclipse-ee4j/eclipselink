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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;


public class XmlSchemaDisplayableAdapter
    extends AbstractModel
    implements Displayable
{
    private MWXmlSchema schema;

    private ResourceRepository resourceRepository;


    // **************** Construction / Initialization *************************

    XmlSchemaDisplayableAdapter(MWXmlSchema schema, ResourceRepository resourceRepository) {
        super();
        this.initialize(schema, resourceRepository);
    }

    private void initialize(MWXmlSchema schema, ResourceRepository resourceRepository) {
        this.schema = schema;
        this.schema.addPropertyChangeListener(MWXmlSchema.NAME_PROPERTY, this.buildPropertyChangeListener());
        this.resourceRepository = resourceRepository;
    }

    private PropertyChangeListener buildPropertyChangeListener() {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChanged(DISPLAY_STRING_PROPERTY, displayString((String) evt.getOldValue()), displayString((String) evt.getNewValue()));
            }
        };
    }


    // **************** Displayable contract **********************************

    public String displayString() {
        return this.displayString(this.schema.getName());
    }

    public Icon icon() {
        return this.resourceRepository.getIcon("file.xml");
    }


    // **************** Comparable contract ***********************************

    public int compareTo(Object o) {
        return DEFAULT_COMPARATOR.compare(this, o);
    }


    // **************** Internal **********************************************

    private String displayString(String schemaName) {
        return schemaName;
    }
}
