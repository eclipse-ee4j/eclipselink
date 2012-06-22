/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.collections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Location {
    private String area;
    private String city;
    private java.math.BigDecimal id;
    public PropertyChangeListener listener;

    /**
     * Location constructor comment.
     */
    public Location() {
        super();
    }

    public PropertyChangeListener getTrackedPropertyChangeListener() {
        return listener;
    }

    public void setTrackedPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listener != null) {
            if (oldValue != newValue) {
                listener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
            }
        }
    }

    public static Location example1() {
        Location example1 = new Location();
        example1.setArea("Rideau Centre");
        example1.setCity("Ottawa");
        return example1;
    }

    public static Location example2() {
        Location example2 = new Location();
        example2.setArea("West Edmonton Mall");
        example2.setCity("Edmonton");
        return example2;
    }

    public static Location example3() {
        Location example3 = new Location();
        example3.setArea("Sparks Street Mall");
        example3.setCity("Ottawa");
        return example3;
    }

    public static Location example4() {
        Location example4 = new Location();
        example4.setArea("Eaton's Centre");
        example4.setCity("Toronto");
        return example4;
    }

    public static Location example5() {
        Location example5 = new Location();
        example5.setArea("Old City");
        example5.setCity("Montreal");
        return example5;
    }

    public static Location example6() {
        Location example6 = new Location();
        example6.setArea("Waterfront");
        example6.setCity("Halifax");
        return example6;
    }

    public String getArea() {
        return area;
    }

    public String getCity() {
        return city;
    }

    public java.math.BigDecimal getId() {
        return id;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition relationTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_R_LO");

        definition.addPrimaryKeyField("LOCA_ID", java.math.BigDecimal.class);
        definition.addPrimaryKeyField("REST_ID", java.math.BigDecimal.class);

        return definition;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition relation2TableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_R_LO2");

        definition.addPrimaryKeyField("LOCA_ID", java.math.BigDecimal.class);
        definition.addPrimaryKeyField("REST_ID", java.math.BigDecimal.class);

        return definition;
    }

    public void setArea(String newValue) {
        propertyChange("area", this.area, newValue);
        this.area = newValue;
    }

    public void setCity(String newValue) {
        propertyChange("city", this.city, newValue);
        this.city = newValue;
    }

    public void setId(java.math.BigDecimal newValue) {
        this.id = newValue;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_LOCA");

        definition.addIdentityField("ID", java.math.BigDecimal.class);
        definition.addField("AREA", String.class, 40);
        definition.addField("CITY", String.class, 40);

        return definition;
    }

    /**
     * Returns a String that represents the value of this object.
     * @return a string representation of the receiver
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()));
        buf.append("(");
        if (getArea() != null) {
            buf.append(getArea());
        }
        buf.append(",");
        if (getCity() != null) {
            buf.append(getCity());
        }
        buf.append(")");
        return buf.toString();
    }
}
