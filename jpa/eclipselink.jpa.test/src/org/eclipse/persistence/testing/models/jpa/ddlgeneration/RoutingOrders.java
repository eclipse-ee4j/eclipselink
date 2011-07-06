/*******************************************************************************
 * Copyright (c) 2011  Xavier Callejas. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/11/2011-2.4 Xavier Callejas  
 *       - 343632: Can't map a compound constraint because of exception: 
 *                 The reference column name [y] mapped on the element [field x] 
 *                 does not correspond to a valid field on the mapping reference
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

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
        if (!(object instanceof RoutingOrders)) {
            return false;
        }
        RoutingOrders other = (RoutingOrders) object;
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
