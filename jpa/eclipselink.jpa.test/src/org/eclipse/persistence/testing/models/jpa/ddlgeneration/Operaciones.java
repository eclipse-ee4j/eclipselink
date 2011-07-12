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
 *     07/11/2011-2.2.1 Xavier Callejas  
 *       - 343632: Can't map a compound constraint because of exception: 
 *                 The reference column name [y] mapped on the element [field x] 
 *                 does not correspond to a valid field on the mapping reference
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

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
