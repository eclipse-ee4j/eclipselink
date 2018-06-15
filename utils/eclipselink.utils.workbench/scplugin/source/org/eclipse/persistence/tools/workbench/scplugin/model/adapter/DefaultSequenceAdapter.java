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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.DefaultSequenceConfig;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class DefaultSequenceConfig
 *
 * @see DefaultSequenceConfig
 *
 * @author Tran Le
 */
public class DefaultSequenceAdapter extends SequenceAdapter {

    /**
     * Creates a new DefaultSequence for the specified model object.
     */
    DefaultSequenceAdapter( SCAdapter parent, DefaultSequenceConfig scConfig) {

        super( parent, scConfig);
    }
    /**
     * Creates a new DefaultSequence.
     */
    protected DefaultSequenceAdapter( SCAdapter parent, String name, int preallocationSize) {

        super( parent, name, preallocationSize);
    }
    /**
     * Returns this Config Model Object.
     */
    private final DefaultSequenceConfig config() {

        return ( DefaultSequenceConfig)this.getModel();
    }
    /**
     * Factory method for building this model.
     */
    protected Object buildModel() {
        return new DefaultSequenceConfig();
    }

    @Override
    public SequenceType getType() {
        return SequenceType.DEFAULT;
    }
}
