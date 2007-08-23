/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.xml.tables;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;
import org.eclipse.persistence.internal.jpa.xml.XMLConstants;
import org.eclipse.persistence.internal.helper.DatabaseTable;

import java.util.List;
import java.util.ArrayList;

/**
 * Object to hold onto an XML table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLTableHelper {
    /**
     * INTERNAL:
     * Process the unique-constraints for a given table node.
     */
    public static void processUniqueConstraints(Node node, XMLHelper helper, DatabaseTable table) {
        NodeList uniqueConstraintNodes = helper.getNodes(node, XMLConstants.UNIQUE_CONSTRAINTS);
        
        if (uniqueConstraintNodes != null) {
            for (int i = 0; i < uniqueConstraintNodes.getLength(); i++) {
                NodeList columnNameNodes = helper.getTextColumnNodes(uniqueConstraintNodes.item(i));
                
                if (columnNameNodes != null) {
                    List<String> columnNames = new ArrayList<String>(columnNameNodes.getLength());
                    for (int k = 0; k < columnNameNodes.getLength(); k++) {
                        String columnName = columnNameNodes.item(k).getNodeValue();
                        
                        if (columnName != null && !columnName.equals("")) {
                            columnNames.add(columnName);
                        }
                    }
                    if (columnNames.size() > 0) {
                        table.addUniqueConstraints(columnNames.toArray(new String[0]));
                    }
                }
            }
        }
    }
}
