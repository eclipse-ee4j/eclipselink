package org.eclipse.persistence.jpars.test.model.multitenant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

@Entity
@Table(name="JPARS_ACCOUNT")
@Multitenant
@TenantDiscriminatorColumn(name="TENANT_ID", contextProperty="tenant.id", primaryKey=true)
public class Account {

    @Id
    @GeneratedValue
    private int id;
    private String accoutNumber;
    @Version
    private int version;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getAccoutNumber() {
        return accoutNumber;
    }
    public void setAccoutNumber(String accoutNumber) {
        this.accoutNumber = accoutNumber;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
}
