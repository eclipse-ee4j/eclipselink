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
 * Object to hold onto optimistic locking metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class OptimisticLockingMetadata  {
    private boolean m_isCascaded;
    private List<String> m_selectedColumns;
    private String m_type;
    
    /**
     * INTERNAL:
     */
    public OptimisticLockingMetadata() {}
    
    /**
     * INTERNAL:
     */
    public OptimisticLockingMetadata(OptimisticLocking optimisticLocking) {        
        setType(optimisticLocking.type().name());
        setIsCascaded(optimisticLocking.cascade());
        setSelectedColumns(optimisticLocking.selectedColumns());   
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
        return m_type;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasSelectedColumns() {
        return m_selectedColumns != null && ! m_selectedColumns.isEmpty();
    }
    
    /**
     * INTERNAL:
     */
    public boolean isCascaded() {
        return m_isCascaded;
    }
    
    /**
     * INTERNAL:
     */
    public void setIsCascaded(boolean isCascaded) {
    	m_isCascaded = isCascaded;
    }
    
    /**
     * INTERNAL:
     * Called from annotation population.
     */
    protected void setSelectedColumns(Column[] selectedColumns) {
    	if (selectedColumns.length > 0) {
    		m_selectedColumns = new ArrayList<String>();
    		
    		for (Column selectedColumn : selectedColumns) {
    			m_selectedColumns.add(selectedColumn.name());
    		}
    	}
    }
    
    /**
     * INTERNAL:
     */
    public void setSelectedColumns(List<String> selectedColumns) {
        m_selectedColumns = selectedColumns;
    }
    
    /**
     * INTERNAL:
     */
    public void setType(String type) {
    	m_type = type;
    }
}
