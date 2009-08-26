package dbws.testing.relationships;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;

public class RelationshipsEmployee {

    public int empId;
    public String firstName;
    public String lastName;
    public int version;
    public Date startDate;
    public Time startTime;
    public Date endDate;
    public Time endTime;
    public String gender;
    public BigDecimal salary;
    public RelationshipsAddress address;
    public Collection<RelationshipsPhone> phones = new ArrayList<RelationshipsPhone>();
    public Collection<String> responsibilities = new ArrayList<String>();

    public RelationshipsEmployee() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(empId);
        sb.append("]");

        return sb.toString();
    }
}