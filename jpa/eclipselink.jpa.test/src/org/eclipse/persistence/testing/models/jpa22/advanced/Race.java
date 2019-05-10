/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.testing.models.jpa22.advanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.testing.models.jpa22.advanced.converters.ResponsibilityConverter;

@Entity
@Table(name="JPA22_RACE")
public class Race {
    @Id
    @GeneratedValue
    private Integer id;

    @Basic
    private String name;

    @ManyToMany(mappedBy="races")
    private List<Runner> runners;

    @OneToMany(mappedBy="race")
    // Add this convert to avoid the auto apply setting to a Long.
    @Convert(attributeName="key.uniqueIdentifier", disableConversion=true)
    @Convert(attributeName="key.description", converter=ResponsibilityConverter.class)
    protected Map<Responsibility, Organizer> organizers;

    public Race() {
        runners = new ArrayList<>();
        organizers = new HashMap<>();
    }

    public void addOrganizer(Organizer organizer, Responsibility responsibility) {
        organizers.put(responsibility, organizer);
    }

    public void addRunner(Runner runner) {
        runners.add(runner);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<Responsibility, Organizer> getOrganizers() {
        return organizers;
    }

    public List<Runner> getRunners() {
        return runners;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganizers(Map<Responsibility, Organizer> organizers) {
        this.organizers = organizers;
    }

    public void setRunners(List<Runner> runners) {
        this.runners = runners;
    }
}
