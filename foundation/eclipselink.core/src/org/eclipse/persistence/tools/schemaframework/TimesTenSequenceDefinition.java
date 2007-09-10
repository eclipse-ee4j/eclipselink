/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;
import org.eclipse.persistence.sequencing.NativeSequence;

public class TimesTenSequenceDefinition extends OracleSequenceDefinition {

    public TimesTenSequenceDefinition(String name, int preallocationSize) {
        super(name, preallocationSize);
    }
    
    public TimesTenSequenceDefinition(String name, int preallocationSize, int start) {
        super(name, preallocationSize, start);
    }    
    public TimesTenSequenceDefinition(String name) {
        super(name);
    }
    
    public TimesTenSequenceDefinition(NativeSequence sequence) {
        super(sequence.getName(), sequence.getPreallocationSize(), sequence.getInitialValue());
    }

    /**
     * INTERNAL:
     * Indicates whether alterIncrement is supported
     */
    public boolean isAlterSupported() {
        return false;
    }

}