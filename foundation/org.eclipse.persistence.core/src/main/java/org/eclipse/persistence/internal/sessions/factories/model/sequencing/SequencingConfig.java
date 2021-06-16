/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions.factories.model.sequencing;

import java.util.Vector;

/**
 * INTERNAL:
 */
public class SequencingConfig {
    private SequenceConfig m_defaultSequenceConfig;
    private Vector m_sequenceConfigs;

    public SequencingConfig() {
        super();
    }

    public void setDefaultSequenceConfig(SequenceConfig defaultSequenceConfig) {
        m_defaultSequenceConfig = defaultSequenceConfig;
    }

    public SequenceConfig getDefaultSequenceConfig() {
        return m_defaultSequenceConfig;
    }

    public void setSequenceConfigs(Vector sequenceConfigs) {
        m_sequenceConfigs = sequenceConfigs;
    }

    public Vector getSequenceConfigs() {
        return m_sequenceConfigs;
    }

    // backward compatibility
    public void setNativeSequencing(boolean nativeSequencing) {
        Integer preallocationSize = null;
        if (getDefaultSequenceConfig() != null) {
            if (getNativeSequencing() == nativeSequencing) {
                return;
            }
            preallocationSize = getDefaultSequenceConfig().getPreallocationSize();
        }
        setDefaultSequenceConfig(nativeSequencing);
        if (preallocationSize != null) {
            getDefaultSequenceConfig().setPreallocationSize(preallocationSize);
        }
    }

    public boolean getNativeSequencing() {
        if (getDefaultSequenceConfig() == null) {
            return false;
        } else {
            return getDefaultSequenceConfig() instanceof NativeSequenceConfig;
        }
    }

    public void setSequencePreallocationSize(Integer sequencePreallocationSize) {
        if (getDefaultSequenceConfig() == null) {
            setDefaultSequenceConfig(false);
        }
        getDefaultSequenceConfig().setPreallocationSize(sequencePreallocationSize);
    }

    public Integer getSequencePreallocationSize() {
        if (getDefaultSequenceConfig() == null) {
            return null;
        } else {
            return getDefaultSequenceConfig().getPreallocationSize();
        }
    }

    public void setSequenceTable(String sequenceTable) {
        if (getDefaultSequenceConfig() == null) {
            setDefaultSequenceConfig(false);
        }
        if (getDefaultSequenceConfig() instanceof TableSequenceConfig) {
            ((TableSequenceConfig)getDefaultSequenceConfig()).setTable(sequenceTable);
        }
    }

    public String getSequenceTable() {
        if ((getDefaultSequenceConfig() != null) && getDefaultSequenceConfig() instanceof TableSequenceConfig) {
            return ((TableSequenceConfig)getDefaultSequenceConfig()).getTable();
        } else {
            return null;
        }
    }

    public void setSequenceNameField(String sequenceNameField) {
        if (getDefaultSequenceConfig() == null) {
            setDefaultSequenceConfig(false);
        }
        if (getDefaultSequenceConfig() instanceof TableSequenceConfig) {
            ((TableSequenceConfig)getDefaultSequenceConfig()).setNameField(sequenceNameField);
        }
    }

    public String getSequenceNameField() {
        if ((getDefaultSequenceConfig() != null) && getDefaultSequenceConfig() instanceof TableSequenceConfig) {
            return ((TableSequenceConfig)getDefaultSequenceConfig()).getNameField();
        } else {
            return null;
        }
    }

    public void setSequenceCounterField(String sequenceCounterField) {
        if (getDefaultSequenceConfig() == null) {
            setDefaultSequenceConfig(false);
        }
        if (getDefaultSequenceConfig() instanceof TableSequenceConfig) {
            ((TableSequenceConfig)getDefaultSequenceConfig()).setCounterField(sequenceCounterField);
        }
    }

    public String getSequenceCounterField() {
        if ((getDefaultSequenceConfig() != null) && getDefaultSequenceConfig() instanceof TableSequenceConfig) {
            return ((TableSequenceConfig)getDefaultSequenceConfig()).getCounterField();
        } else {
            return null;
        }
    }

    protected void setDefaultSequenceConfig(boolean nativeSequencing) {
        SequenceConfig sequenceConfig;
        if (nativeSequencing) {
            NativeSequenceConfig nativeSequenceConfig = new NativeSequenceConfig();
            sequenceConfig = nativeSequenceConfig;
        } else {
            TableSequenceConfig tableSequenceConfig = new TableSequenceConfig();
            tableSequenceConfig.setTable("SEQUENCE");
            tableSequenceConfig.setNameField("SEQ_NAME");
            tableSequenceConfig.setCounterField("SEQ_COUNT");
            sequenceConfig = tableSequenceConfig;
        }
        sequenceConfig.setName("");
        sequenceConfig.setPreallocationSize(Integer.valueOf(50));
        setDefaultSequenceConfig(sequenceConfig);
    }
}
