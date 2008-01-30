package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.UniqueConstraint;

/**
 * INTERNAL:
 * Object to hold onto a unique constraint metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class UniqueConstraintMetadata {
	private List<String> m_columnNames;
	
	/**
     * INTERNAL:
     */
	public UniqueConstraintMetadata() {}
	
	/**
     * INTERNAL:
     */
	public UniqueConstraintMetadata(UniqueConstraint uniqueConstraint) {
		m_columnNames = new ArrayList<String>();
		
		for (String columnName : uniqueConstraint.columnNames()) {
			m_columnNames.add(columnName);
		}
	}
	
	/**
     * INTERNAL:
     */
	public List<String> getColumnNames() {
		return m_columnNames;
	}
	
	/**
     * INTERNAL:
     */
	public void setColumnNames(List<String> columnNames) {
		m_columnNames = columnNames;
	}
}

