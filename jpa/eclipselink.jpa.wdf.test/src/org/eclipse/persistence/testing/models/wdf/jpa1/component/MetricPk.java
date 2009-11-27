/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.component;

public class MetricPk {

    private Long componentId;
    private Long inspectionId;
    private String name;

    public MetricPk(Long componentId, Long inspectionId, String name) {
        super();
        this.componentId = componentId;
        this.inspectionId = inspectionId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public Long getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(Long inspectionId) {
        this.inspectionId = inspectionId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((componentId == null) ? 0 : componentId.hashCode());
        result = prime * result + ((inspectionId == null) ? 0 : inspectionId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MetricPk other = (MetricPk) obj;
        if (componentId == null) {
            if (other.componentId != null)
                return false;
        } else if (!componentId.equals(other.componentId))
            return false;
        if (inspectionId == null) {
            if (other.inspectionId != null)
                return false;
        } else if (!inspectionId.equals(other.inspectionId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
