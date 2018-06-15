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
package org.eclipse.persistence.sdo;

import org.eclipse.persistence.internal.oxm.XMLSetting;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import commonj.sdo.Property;

/**
  * <p><b>Purpose</b>: A setting encapsulates a Property and a corresponding single value of the property's type.
* <p>As a Setting used by a Sequence object, this class Implements the XMLSetting interface and provides a container
 * for a Property/Value pair as part of the sequence.
 * <p><b>Responsibilities:</b><ul>
 * <li>Provide get/set access to the property and value instance variables of a {@link javax.sound.midi.Sequence sequence} object.</li>
 * </ul>
   *
  * @see org.eclipse.persistence.sdo.SDOChangeSummary
  * @see org.eclipse.persistence.internal.oxm.XMLSetting
  * @since Oracle TopLink 11.1.1.0.0
  */
public class SDOSetting implements commonj.sdo.ChangeSummary.Setting, XMLSetting {
    private SDOProperty property;
    private Object value;
    private boolean isSet;

    public SDOSetting() {
    }

    public SDOSetting(Property aProperty, Object aValue) {
        property = (SDOProperty) aProperty;
        value = aValue;
        // isSet is false by default
    }

    /**
     * @return The TopLink OXM mapping associated with this setting
     */
    @Override
    public XMLMapping getMapping() {
        return (XMLMapping)property.getXmlMapping();
    }

    /**
     * Returns the property of the setting.
     * @return the setting property.
     */
    @Override
    public SDOProperty getProperty() {
        return property;
    }

    /**
     * INTERNAL:
     * Set the property of this setting.
     * @param property     The property to set on this setting.
     */
    public void setProperty(Property property) {
        this.property = (SDOProperty) property;
    }

    /**
     * Returns the value of the setting.
     * @return the setting value.
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * INTERNAL:
     * Set the value of this setting.
     * @param object     The value to set on this setting.
     */
    public void setValue(Object object) {
        value = object;
    }

    /**
     * Returns whether or not the property is set.
     * @return <code>true</code> if the property is set.
     */
    @Override
    public boolean isSet() {
        return isSet;
    }

    /**
     * INTERNAL:
     * Set if the value of this setting is set.
     * @param isSet If the value is set on this Setting.
     */
    public void setIsSet(boolean isSet) {
        this.isSet = isSet;
    }

    /**
     * INTERNAL:
     * Print out a String representation of this object
     */
    @Override
    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(getClass().getName());
        aBuffer.append("@");
        aBuffer.append(hashCode());
        aBuffer.append("(");
        aBuffer.append(property);
        aBuffer.append(",");
        aBuffer.append(value);
        aBuffer.append(")");
        return aBuffer.toString();
    }
}
