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
package org.eclipse.persistence.eis.adapters.xmlfile;

import org.eclipse.persistence.eis.EISSequence;
import org.eclipse.persistence.eis.interactions.XQueryInteraction;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 * Provides sequence support for EISPlatform
 */
public class XMLFileSequence extends EISSequence {

    /**
     * Default constructor.
     */
    public XMLFileSequence() {
        super();
    }

    public XMLFileSequence(String name) {
        super(name);
    }

    public XMLFileSequence(String name, int size) {
        super(name, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XMLFileSequence) {
            return equalNameAndSize(this, (XMLFileSequence)obj);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String n = getName();
        return 17 * (n != null ? n.hashCode() : 0) + getPreallocationSize() * 5;
    }

    /**
     * Support sequencing through sequence file.
     */
    @Override
    protected ValueReadQuery buildSelectQuery() {
        ValueReadQuery query = new ValueReadQuery();
        query.addArgument("sequence-name");
        XQueryInteraction interaction = new XQueryInteraction();
        interaction.setFunctionName("select-sequence");
        interaction.setProperty("fileName", "sequence.xml");
        interaction.setXQueryString("sequence[sequence-name='#sequence-name']/sequence-count");
        query.setCall(interaction);

        return query;
    }

    /**
     * Support sequencing through sequence file.
     */
    @Override
    protected DataModifyQuery buildUpdateQuery() {
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("sequence-name");
        query.addArgument("sequence-count");
        XQueryInteraction interaction = new XQueryInteraction();
        interaction.setFunctionName("update-sequence");
        interaction.setProperty("fileName", "sequence.xml");
        interaction.setXQueryString("sequence[sequence-name='#sequence-name']");
        interaction.setInputRootElementName("sequence");
        interaction.addArgument("sequence-name");
        interaction.addArgument("sequence-count");
        interaction.setOutputResultPath("result");
        query.setCall(interaction);

        return query;
    }
}
