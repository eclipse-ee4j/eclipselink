/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2011, 2024 Xavier Callejas. All rights reserved.
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
//     07/11/2011-2.4 Xavier Callejas
//       - 343632: Can't map a compound constraint because of exception:
//                 The reference column name [y] mapped on the element [field x]
//                 does not correspond to a valid field on the mapping reference
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author xavier
 */
@Entity
@Table(name = "RoutingOrders", uniqueConstraints =
@UniqueConstraint(columnNames = {"Cliente", "Operacion"}))
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RoutingOrders.findAll", query = "SELECT r FROM RoutingOrders r")})
@AttributeOverride(name = "Cliente", column =
@Column(name = "Cliente"))
public class RoutingOrders implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "idRoutingOrder")
    private Integer idRoutingOrder;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FechaRegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    @JoinColumns({
        @JoinColumn(name = "Cliente", referencedColumnName = "Cliente"),
        @JoinColumn(name = "Operacion", referencedColumnName = "idOperacion")})
    @OneToOne(optional = false, targetEntity=Operaciones.class)
    private Operaciones operaciones;

    public RoutingOrders() {
    }

    public RoutingOrders(Integer idRoutingOrder) {
        this.idRoutingOrder = idRoutingOrder;
    }

    public RoutingOrders(Integer idRoutingOrder, Date fechaRegistro) {
        this.idRoutingOrder = idRoutingOrder;
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIdRoutingOrder() {
        return idRoutingOrder;
    }

    public void setIdRoutingOrder(Integer idRoutingOrder) {
        this.idRoutingOrder = idRoutingOrder;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Operaciones getOperaciones() {
        return operaciones;
    }

    public void setOperaciones(Operaciones operaciones) {
        this.operaciones = operaciones;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRoutingOrder != null ? idRoutingOrder.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoutingOrders other)) {
            return false;
        }
        if ((this.idRoutingOrder == null && other.idRoutingOrder != null) || (this.idRoutingOrder != null && !this.idRoutingOrder.equals(other.idRoutingOrder))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.test.model.jpa.RoutingOrders[ idRoutingOrder=" + idRoutingOrder + " ]";
    }
}
