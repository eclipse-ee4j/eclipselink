/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.locking;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import org.eclipse.persistence.annotations.OptimisticLocking;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * Object to hold onto optimisic locking metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class MetadataOptimisticLocking  {
    private OptimisticLocking m_optimisticLocking;
    protected List<String> m_selectedColumns;
    
    /**
     * INTERNAL:
     */
    protected MetadataOptimisticLocking() {
        m_selectedColumns = new ArrayList<String>();
    }
    
    /**
     * INTERNAL:
     */
    public MetadataOptimisticLocking(OptimisticLocking optimisticLocking) {
        this();
        m_optimisticLocking = optimisticLocking;
        
        for (Column selectedColumn : optimisticLocking.selectedColumns()) {
            m_selectedColumns.add(selectedColumn.name());
        }
    }
    
    /**
     * INTERNAL:
     * When XMLOptimisticLocking is built, this method should be overridden
     * to return a more XML specific message.
     */
    public String getIgnoreLogMessageContext() {
        return MetadataLogger.IGNORE_OPTIMISTIC_LOCKING_ANNOTATION;
    }
    
    /**
     * INTERNAL:
     */
    public List<String> getSelectedColumns() {
        return m_selectedColumns;
    }
    
    /**
     * INTERNAL:
     */
    public String getType() {
        return m_optimisticLocking.type().name();
    }
    
    /**
     * INTERNAL:
     */
    public boolean isCascaded() {
        return m_optimisticLocking.cascade();
    }
}
