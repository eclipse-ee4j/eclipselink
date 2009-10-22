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
