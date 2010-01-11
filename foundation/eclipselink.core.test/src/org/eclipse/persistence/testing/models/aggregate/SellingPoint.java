/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

// CR#2896 - Made SellingPoint abstract - TW
public abstract class SellingPoint {
    private String area;
    private String description;

    public SellingPoint() {
        area = null;
        description = null;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof SellingPoint) {
            SellingPoint sellingPoint = (SellingPoint)object;
            if (description == null) {
                if (sellingPoint.getDescription() != null) {
                    return false;
                }
            } else {
                if (!description.equals(sellingPoint.getDescription())) {
                    return false;
                }
            }
            if (area == null) {
                if (sellingPoint.getArea() != null) {
                    return false;
                }
            } else {
                if (!area.equals(sellingPoint.getArea())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /* CR#2896 Moved Several Examples to RoomSellingPoint - TW */
    public String getArea() {
        return area;
    }

    public String getDescription() {
        return description;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("SELLING_POINT");

        definition.addPrimaryKeyField("AGENT_ID", java.math.BigDecimal.class, 15);
        definition.addPrimaryKeyField("LOCATION", String.class, 200);
        definition.addPrimaryKeyField("AREA", String.class, 50);
        definition.addField("DESCRIPTION", String.class, 200);
        // CR#2896 - TW
        definition.addField("TYPE", String.class, 5);
        definition.addField("SQUARE_FEET", Integer.class, 10);
        return definition;
    }
}
