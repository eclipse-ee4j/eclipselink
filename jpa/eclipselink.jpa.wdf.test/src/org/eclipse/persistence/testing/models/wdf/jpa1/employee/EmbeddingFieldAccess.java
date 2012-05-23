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

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "TMP_EMBEDD_FA")
public class EmbeddingFieldAccess {

    @Transient
    private boolean postUpdateCalled;

    public EmbeddingFieldAccess() {
    }

    public EmbeddingFieldAccess(int anId) {
        id = anId;
    }

    @Id
    protected int id;

    @Embedded
    @AttributeOverrides( { @AttributeOverride(name = "date", column = @Column(name = "EMB_FA_DATE")),
            @AttributeOverride(name = "time", column = @Column(name = "EMB_FA_LONG")) })
    protected EmbeddedFieldAccess fieldAccess;

    /*
     * Nesting different access types is currently not supported (PFD EJB-Persistence 21.01.2006) @Embedded @AttributeOverrides(
     * {
     * 
     * @AttributeOverride(name = "date", column = @Column(name = "EMB_PA_DATE")), @AttributeOverride(name = "time", column =
     * 
     * @Column(name = "EMB_PA_TIME")) })
     */
    @Transient
    public EmbeddedPropertyAccess propertyAccess;

    public void fill() {
        final long theTime = 123456789012345L;
        setFieldAccess(new EmbeddedFieldAccess());
        getFieldAccess().changeDate(new Date(theTime));
        getFieldAccess().changeTime(theTime);
        propertyAccess = new EmbeddedPropertyAccess();
        propertyAccess.setDate(new Date(theTime));
        propertyAccess.setTime(theTime);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setFieldAccess(EmbeddedFieldAccess fieldAccess) {
        this.fieldAccess = fieldAccess;
    }

    public EmbeddedFieldAccess getFieldAccess() {
        return fieldAccess;
    }
}
