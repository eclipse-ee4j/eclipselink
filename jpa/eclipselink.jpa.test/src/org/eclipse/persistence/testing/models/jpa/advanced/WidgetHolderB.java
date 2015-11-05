/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="JPA_WIDGET_HOLDER_B")
public class WidgetHolderB {

    @Id
    @Column(name="B_ID")
    private long id;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(nullable=true, name="HOLDER_B_ID")
    private List<Widget> widgets = new ArrayList<Widget>();

    public WidgetHolderB() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }
    
}
