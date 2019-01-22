/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 */
// Contributors:
//     Sureshkumar Balakrishnan
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name = "MaterialHistSEQ", initialValue = 100, allocationSize = 100)
public class MaterialHist {

    @Id
    @GeneratedValue(generator = "MaterialHistSEQ")
    private Long id;

    @Version
    private Long version;

    @ManyToOne
    private Material original;

    @OneToMany(mappedBy = "material", cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    private List<PlanArbeitsgangHist> restproduktionsweg = new ArrayList<>();

    private String ident;

    private String wert;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getWert() {
        return wert;
    }

    public void setWert(String wert) {
        this.wert = wert;
    }

    public Material getOriginal() {
        return original;
    }

    public void setOriginal(Material original) {
        this.original = original;
    }

    public List<PlanArbeitsgangHist> getRestproduktionsweg() {
        return restproduktionsweg;
    }

    public void setRestproduktionsweg(List<PlanArbeitsgangHist> restproduktionsweg) {
        this.restproduktionsweg = restproduktionsweg;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, ident=%s]", getClass().getSimpleName(), id, ident);
    }
}
