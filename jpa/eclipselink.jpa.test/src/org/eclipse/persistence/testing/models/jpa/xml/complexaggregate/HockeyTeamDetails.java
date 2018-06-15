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
//     04/02/2008-1.0M6 Guy Pelletier
//       - 224155: embeddable-attributes should be extended in the EclipseLink ORM.XML schema
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.io.Serializable;

public class HockeyTeamDetails implements Serializable {
    private String level;
    private String homeColor;
    private String awayColor;

    public HockeyTeamDetails() {}

    public String getAwayColor() {
        return awayColor;
    }

    public String getHomeColor() {
        return homeColor;
    }

    public String getLevel() {
        return level;
    }

    public void setAwayColor(String awayColor) {
        this.awayColor = awayColor;
    }

    public void setHomeColor(String homeColor) {
        this.homeColor = homeColor;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}

