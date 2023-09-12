package org.eclipse.persistence.testing.models.jpa.persistence32;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

// JPQL query and Criteria query tests
@Entity
@Table(name="PERSISTENCE32_SYNTAX_ENTITY")
public class SyntaxEntity {

    @Id
    private long id;

    @Column(name = "STR_VAL_1")
    private String strVal1;
    @Column(name = "STR_VAL_2")
    private String strVal2;

    @Column(name = "INT_VAL")
    private Integer intVal;

    @Column(name = "TIME_VAL")
    private LocalTime timeVal;

    @Column(name = "DATE_VAL")
    private LocalDate dateVal;

    @ElementCollection
    @CollectionTable(name = "PERSISTENCE32_SE_COLTABLE", joinColumns = @JoinColumn(name = "ent_id"))
    private Collection<String> colVal;

    public SyntaxEntity() {
    }

    public SyntaxEntity(Long id) {
        this.id = id;
    }

    public SyntaxEntity(Long id,
                        String strVal1,
                        String strVal2,
                        Integer intVal,
                        LocalTime timeVal,
                        LocalDate dateVal,
                        Collection<String> colVal) {
        this.id = id;
        this.strVal1 = strVal1;
        this.strVal2 = strVal2;
        this.intVal = intVal;
        this.timeVal = timeVal;
        this.dateVal = dateVal;
        this.colVal = colVal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStrVal1() {
        return strVal1;
    }

    public void setStrVal1(String strVal1) {
        this.strVal1 = strVal1;
    }

    public String getStrVal2() {
        return strVal2;
    }

    public void setStrVal2(String strVal2) {
        this.strVal2 = strVal2;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    public LocalTime getTimeVal() {
        return timeVal;
    }

    public void setTimeVal(LocalTime timeVal) {
        this.timeVal = timeVal;
    }

    public LocalDate getDateVal() {
        return dateVal;
    }

    public void setDateVal(LocalDate dateVal) {
        this.dateVal = dateVal;
    }

}
