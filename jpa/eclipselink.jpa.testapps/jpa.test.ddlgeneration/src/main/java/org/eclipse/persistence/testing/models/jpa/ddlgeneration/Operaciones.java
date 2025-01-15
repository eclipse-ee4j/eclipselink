/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2011, 2025 Xavier Callejas. All rights reserved.
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

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author xavier
 */
@Entity
@Table(name = "Operaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Operaciones.findAll", query = "SELECT o FROM Operaciones o")})
public class Operaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "idOperacion")
    private Integer idOperacion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FechaRegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "operaciones")
    private RoutingOrders routingOrders;

    @JoinColumn(name = "Cliente", referencedColumnName = "idCliente")
    @ManyToOne(optional = false)
    private Clientes cliente;

    public Operaciones() {
    }

    public Operaciones(Integer idOperacion) {
        this.idOperacion = idOperacion;
    }

    public Operaciones(Integer idOperacion, Date fechaRegistro) {
        this.idOperacion = idOperacion;
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIdOperacion() {
        return idOperacion;
    }

    public void setIdOperacion(Integer idOperacion) {
        this.idOperacion = idOperacion;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public RoutingOrders getRoutingOrders() {
        return routingOrders;
    }

    public void setRoutingOrders(RoutingOrders routingOrders) {
        this.routingOrders = routingOrders;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOperacion != null ? idOperacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Operaciones)) {
            return false;
        }
        Operaciones other = (Operaciones) object;
        if ((this.idOperacion == null && other.idOperacion != null) || (this.idOperacion != null && !this.idOperacion.equals(other.idOperacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.test.model.jpa.Operaciones[ idOperacion=" + idOperacion + " ]";
    }
}
