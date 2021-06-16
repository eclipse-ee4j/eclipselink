/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.util.Date;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "TMP_EMBEDD_PA")
public class EmbeddingPropertyAccess {

    private boolean postUpdateCalled;

    public EmbeddingPropertyAccess() {
    }

    public EmbeddingPropertyAccess(int anId) {
        _id = anId;
    }

    private int _id;

    private EmbeddedFieldAccess _fieldAccess;

    private EmbeddedPropertyAccess _propertyAccess;

    public void fill() {
        final long theTime = 123456789012345L;
        _fieldAccess = new EmbeddedFieldAccess();
        _fieldAccess.changeDate(new Date(theTime));
        _fieldAccess.changeTime(theTime);
        _propertyAccess = new EmbeddedPropertyAccess();
        _propertyAccess.setDate(new Date(theTime));
        _propertyAccess.setTime(theTime);
    }

    public void clearPostUpdate() {
        postUpdateCalled = false;
    }

    @PostUpdate
    public void postUpdate() {
        postUpdateCalled = true;

    }

    public boolean postUpdateWasCalled() {
        return postUpdateCalled;
    }

    /*
     * Nesting different access types is currently not supported (PFD EJB-Persistence 21.01.2006)
     *
     * @Embedded
     *
     * @AttributeOverrides( { @AttributeOverride(name = "date", column = @Column(name = "EMB_FA_DATE")),
     *
     * @AttributeOverride(name = "time", column = @Column(name = "EMB_FA_TIME")) })
     */
    @Transient
    public EmbeddedFieldAccess getFieldAccess() {
        return _fieldAccess;
    }

    public void setFieldAccess(final EmbeddedFieldAccess fieldAccess) {
        _fieldAccess = fieldAccess;
    }

    @Embedded
    @AttributeOverrides( { @AttributeOverride(name = "date", column = @Column(name = "EMB_PA_DATE")),
            @AttributeOverride(name = "time", column = @Column(name = "EMB_PA_LONG")) })
    public EmbeddedPropertyAccess getPropertyAccess() {
        return _propertyAccess;
    }

    public void setPropertyAccess(final EmbeddedPropertyAccess propertyAccess) {
        _propertyAccess = propertyAccess;
    }

    @Id
    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }
}
