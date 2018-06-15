/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/20/2009-2.0  mobrien - JPA 2.0 Metadata API test model
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
package org.eclipse.persistence.testing.models.jpa.metamodel;

import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;

@Embeddable
public class Observation {
    // This class is embedded inside a GalacticPosition

    // nested embeddable
    @Embedded
    private ObservationDetail detail;

/*    @ManyToMany
    @MapKey(name = "data")
    private Map<String, ObservationDetail> details;

    public Map<String, ObservationDetail> getDetails() {
        return details;
    }

    public void setDetails(Map<String, ObservationDetail> details) {
        this.details = details;
    }
*/

    public ObservationDetail getDetail() {
        return detail;
    }

    public void setDetail(ObservationDetail detail) {
        this.detail = detail;
    }


    private String date;

    private String text;

    @ElementCollection
    private Map<ObservationDetail, String> details;

    @ElementCollection
    private List<String> locations;

    public Observation() {
    }

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

    public Map<ObservationDetail, String> getDetails() {
        return details;
    }

    public void setDetails(Map<ObservationDetail, String> details) {
        this.details = details;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

}

