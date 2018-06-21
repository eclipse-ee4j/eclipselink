/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     01/19/2010-2.1 Guy Pelletier
//       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.FetchAttribute;
import org.eclipse.persistence.annotations.FetchGroup;
import org.eclipse.persistence.annotations.FetchGroups;

@Entity
@Table(name="JPA_PADS")
@DiscriminatorValue("PAD")
@FetchGroups({
    @FetchGroup(name="HeightAndWidth",
        attributes={
            @FetchAttribute(name="height"),
            @FetchAttribute(name="width")}),
    @FetchGroup(name="Weight", attributes={@FetchAttribute(name="weight")})
})
public class Pads extends GoalieGear {
    public Double weight;
    public Double height;
    public Double width;

    public Double getHeight() {
        return height;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getWidth() {
        return width;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setWidth(Double width) {
        this.width = width;
    }
}
