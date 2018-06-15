/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Vikram Bhatia - initial implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.annotations.WriteTransformer;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

@Entity
@Table(name="CMP3_DOOR")
public class Door implements Serializable, Cloneable {
    @Id
    private int id;
    private int width;
    private int height;

    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Room room;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Transient
    public Date getSaleDate() {

        Calendar cal = Calendar.getInstance();
        cal.setTime(getWarrantyDate());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR) - 1;
        return Helper.dateFromYearMonthDate(year, month, day);
    }

    public void setSaleDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR) + 1;
        setWarrantyDate(Helper.dateFromYearMonthDate(year, month, day));
    }


    // Bug#391251 : Test for @Column annotation given outside @WriteTransformer annotation
    @Mutable(false)
    @ReadTransformer(method="calcWarrantyDate")
    @WriteTransformer(method="getSaleDate")
    @Column(name="SALE_DATE")
    private Date WarrantyDate;

    public Date getWarrantyDate() {
        return this.WarrantyDate;
    }

    public void setWarrantyDate(Date date) {
        this.WarrantyDate = date;
    }

    public Date calcWarrantyDate(Record row, Session session) {
        Date date = (Date) ( session.getDatasourcePlatform().convertObject(row.get("SALE_DATE"), java.sql.Date.class ));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR) + 1;
        return Helper.dateFromYearMonthDate(year, month, day);
    }

    public int hashCode() {
        if (this.room != null) {
            return this.room.hashCode();
        } else {
            return super.hashCode();
        }
    }

    public boolean isRoomInstanceInitialized() {
        try {
            Field f = this.getClass().getDeclaredField("room");
            f.setAccessible(true);

            return f.get(this) == null;
        } catch (Exception ex) {
            return false;
        }
    }
}
