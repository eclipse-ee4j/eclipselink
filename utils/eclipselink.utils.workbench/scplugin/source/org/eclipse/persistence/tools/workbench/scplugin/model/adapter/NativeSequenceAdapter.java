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

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.NativeSequenceConfig;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class NativeSequenceConfig
 *
 * @see NativeSequenceConfig
 *
 * @author Tran Le
 */
public class NativeSequenceAdapter extends SequenceAdapter {

    /**
     * Creates a new NativeSequence for the specified model object.
     */
    NativeSequenceAdapter( SCAdapter parent, NativeSequenceConfig scConfig) {

        super( parent, scConfig);
    }
    /**
     * Creates a new NativeSequence.
     */
    protected NativeSequenceAdapter( SCAdapter parent, String name, int preallocationSize) {

        super( parent, name, preallocationSize);
    }
    /**
     * Returns this Config Model Object.
     */
    private final NativeSequenceConfig config() {

        return ( NativeSequenceConfig)this.getModel();
    }
    /**
     * Factory method for building this model.
     */
    protected Object buildModel() {
        return new NativeSequenceConfig();
    }

    public boolean isNative() {

        return true;
    }

    @Override
    public SequenceType getType() {
        return SequenceType.NATIVE;
    }
}
