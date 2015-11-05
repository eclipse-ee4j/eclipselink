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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="JPA_WIDGET")
public class Widget {

    @Id
    @Column(name="W_ID")
    private long id;

    @OneToOne(fetch=FetchType.LAZY, mappedBy="widget")
    @JoinColumn(name="WIDGET_PART_ID")
    private WidgetPart widgetPart;
    
    public Widget() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public WidgetPart getWidgetPart() {
        return widgetPart;
    }

    public void setWidgetPart(WidgetPart widgetPart) {
        this.widgetPart = widgetPart;
    }

}
