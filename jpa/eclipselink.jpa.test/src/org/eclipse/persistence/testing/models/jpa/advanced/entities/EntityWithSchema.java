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
 *     dminsky - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="ENTITY_WITH_SCHEMA", schema=EntityWithSchema.CUSTOM_SCHEMA_NAME)
public class EntityWithSchema implements Serializable {

    public static final String CUSTOM_SCHEMA_NAME = "CUSTOM_SCHEMA";
    public static final String CUSTOM_SEQUENCE_NAME = "CUSTOM_SEQUENCE";

    // Table Sequence with a schema
    @Id
    @TableGenerator(
            name="CustomSequenceGenerator",
            schema=CUSTOM_SCHEMA_NAME,
            table=CUSTOM_SEQUENCE_NAME,
            pkColumnName="SEQ_NAME",
            valueColumnName="SEQ_VALUE",
            pkColumnValue="EntityASequence",
            allocationSize=50)
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CustomSequenceGenerator")
    private Long id;
    private String name;

    public EntityWithSchema() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
