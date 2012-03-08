package jpql.query;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries
({
	@NamedQuery(name="employee.?1",                query="SELECT e FROM Employee e WHERE e.name = ?1 ORDER BY e.name"),
	@NamedQuery(name="employee.addition1",         query="SELECT 2 + 2.2F FROM Employee e"),
	@NamedQuery(name="employee.addition2",         query="SELECT AVG(e.salary) + 2E2 FROM Employee e"),
	@NamedQuery(name="employee.addition3",         query="SELECT e.salary + 2 FROM Employee e"),
	@NamedQuery(name="employee.addition4",         query="SELECT e.name + 2 FROM Employee e"),
	@NamedQuery(name="employee.and",               query="SELECT OBJECT(e) FROM Employee e WHERE e.phoneNumbers IS EMPTY and e.name like 'testFlushModeOnUpdateQuery'"),
	@NamedQuery(name="employee.case1",             query="SELECT CASE WHEN e.name = 'Java Persistence Query Language' THEN 'Java Persistence Query Language' WHEN e.salary BETWEEN 1 and 2 THEN SUBSTRING(e.name, 0, 2) ELSE e.name END FROM Employee e"),
	@NamedQuery(name="employee.case2",             query="SELECT CASE WHEN e.name = 'JPQL' THEN e.working WHEN e.salary BETWEEN 1 and 2 THEN TRUE ELSE p.completed END FROM Employee e, Project p"),
	@NamedQuery(name="employee.case3",             query="SELECT CASE WHEN e.name = 'JPQL' THEN e.working WHEN e.salary BETWEEN 1 and 2 THEN SUBSTRING(e.name, 0, 2) ELSE e.dept END FROM Employee e"),
	@NamedQuery(name="employee.caseInsensitive",   query="SELECT e FROM Employee E"),
	@NamedQuery(name="employee.collection",        query="SELECT e.name, d.dname FROM Employee e, Dept d"),
	@NamedQuery(name="employee.date1",             query="SELECT CURRENT_DATE FROM Employee e"),
	@NamedQuery(name="employee.date2",             query="SELECT {d '2008-12-31'} FROM Employee e"),
	@NamedQuery(name="employee.delete",            query="DELETE FROM Employee e"),
	@NamedQuery(name="employee.delete.dept",       query="DELETE FROM Employee e WHERE e.department = :dept"),
	@NamedQuery(name="employee.dept",              query="SELECT e FROM Employee e WHERE e.salary = (SELECT MAX(e.salary) FROM Employee a WHERE a.department = :dept)"),
	@NamedQuery(name="employee.deptBase",          query="SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :base"),
	@NamedQuery(name="employee.deptno",            query="select e from Employee e where e.dept.deptno in (:deptno)"),
	@NamedQuery(name="employee.division1",         query="SELECT 2 / 2.2F FROM Employee e"),
	@NamedQuery(name="employee.division2",         query="SELECT AVG(e.salary) / 2E2 FROM Employee e"),
	@NamedQuery(name="employee.division3",         query="SELECT e.salary / 2 FROM Employee e"),
	@NamedQuery(name="employee.division4",         query="SELECT e.name / 2 FROM Employee e"),
	@NamedQuery(name="employee.enum",              query="SELECT CASE WHEN e.name = 'Pascal' THEN jpql.query.EnumType.FIRST_NAME WHEN e.name = 'JPQL' THEN jpql.query.EnumType.LAST_NAME ELSE jpql.query.EnumType.NAME END FROM Employee e"),
	@NamedQuery(name="employee.lower",             query="SELECT LOWER(e.name) FROM Employee e"),
	@NamedQuery(name="employee.false",             query="SELECT FALSE FROM Employee e"),
	@NamedQuery(name="employee.findAll",           query="SELECT e FROM Employee e"),
	@NamedQuery(name="employee.func1",             query="SELECT FUNC('toString', e.name) FROM Employee e"),
	@NamedQuery(name="employee.func2",             query="SELECT FUNC('age', e.empId, e.salary) FROM Employee e"),
	@NamedQuery(name="employee.func3",             query="SELECT FUNC('age', e.empId, e.name) FROM Employee e"),
	@NamedQuery(name="employee.func4",             query="SELECT FUNC('age', e.empId, :name) FROM Employee e"),
	@NamedQuery(name="employee.in",                query="SELECT e FROM Employee e WHERE e.name IN :type"),
	@NamedQuery(name="employee.join.fetch1",       query="SELECT e FROM Employee e JOIN FETCH e.address WHERE e.empId = :ID"),
	@NamedQuery(name="employee.join.fetch2",       query="SELECT e, e.name FROM Employee e JOIN FETCH e.address WHERE e.empId = :ID"),
	@NamedQuery(name="employee.join.left1",        query="SELECT a FROM Employee e LEFT JOIN e.address a"),
	@NamedQuery(name="employee.join.left2",        query="SELECT m, e FROM Employee e LEFT JOIN e.managerEmployee m"),
	@NamedQuery(name="employee.max",               query="SELECT MAX(e.salary) FROM Employee e"),
	@NamedQuery(name="employee.min",               query="SELECT MIN(e.salary) FROM Employee e"),
	@NamedQuery(name="employee.mod",               query="SELECT MOD(e.salary, e.empId) FROM Employee e"),
	@NamedQuery(name="employee.multiplication1",   query="SELECT 2 * 2.2F FROM Employee e"),
	@NamedQuery(name="employee.multiplication2",   query="SELECT AVG(e.salary) * 2E2 FROM Employee e"),
	@NamedQuery(name="employee.multiplication3",   query="SELECT e.salary * 2 FROM Employee e"),
	@NamedQuery(name="employee.multiplication4",   query="SELECT e.name * 2 FROM Employee e"),
	@NamedQuery(name="employee.nullif1",           query="SELECT NULLIF(e.name, 'JPQL') FROM Employee e"),
	@NamedQuery(name="employee.nullif2",           query="SELECT NULLIF(2 + 2, 'JPQL') FROM Employee e"),
	@NamedQuery(name="employee.object1",           query="SELECT OBJECT(e) FROM Employee e"),
	@NamedQuery(name="employee.object2",           query="SELECT OBJECT(e) FROM Employee e WHERE e.name = ?1"),
	@NamedQuery(name="employee.resultVariable1",   query="SELECT e.name AS n From Employee e"),
	@NamedQuery(name="employee.resultVariable2",   query="SELECT e.name n From Employee e"),
	@NamedQuery(name="employee.resultVariable3",   query="SELECT e.salary / 1000D n From Employee e"),
	@NamedQuery(name="employee.select",            query="SELECT e FROM Employee e"),
	@NamedQuery(name="employee.sum",               query="SELECT SUM(e.salary) FROM Employee e"),
	@NamedQuery(name="employee.subquery1",         query="SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM Project p JOIN p.employees emp WHERE emp = e AND p.name = :name)"),
	@NamedQuery(name="employee.subquery2",         query="SELECT e FROM Employee e WHERE e.empId in (SELECT MIN(ee.empId) FROM Employee ee)"),
	@NamedQuery(name="employee.subquery.code_1",   query="SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)"),
	@NamedQuery(name="employee.substraction1",     query="SELECT 2 - 2.2F FROM Employee e"),
	@NamedQuery(name="employee.substraction2",     query="SELECT AVG(e.salary) - 2E2 FROM Employee e"),
	@NamedQuery(name="employee.substraction3",     query="SELECT e.salary - 2 FROM Employee e"),
	@NamedQuery(name="employee.substraction4",     query="SELECT e.name - 2 FROM Employee e"),
	@NamedQuery(name="employee.trim",              query="SELECT TRIM(e.name) FROM Employee e"),
	@NamedQuery(name="employee.true",              query="SELECT TRUE FROM Employee e"),
	@NamedQuery(name="employee.update1",           query="UPDATE Employee e SET e.name = ?1"),
	@NamedQuery(name="employee.update2",           query="UPDATE Employee e set e.salary = e.roomNumber, e.roomNumber = e.salary, e.address = null where e.name = 'testUpdateUsingTempStorage'"),
	@NamedQuery(name="employee.update.positional", query="UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2"),
	@NamedQuery(name="employee.upper",             query="SELECT UPPER(e.name) FROM Employee e")
})
@SuppressWarnings("unused")
public class Employee implements Serializable {

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinColumn(name = "ADDR_ID")
	private Address address;
	private String department;
	@ManyToOne
	@JoinColumn(name = "DEPTNO")
	private Dept dept;
	@Embedded
	private EmbeddedAddress embeddedAddress;
	@Id
	@Column(name = "EMP_ID", nullable = false)
	private Long empId;
	private String manager;
	@ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private Employee managerEmployee;
	private String name;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
	private Collection<Phone> phoneNumbers;
	private int roomNumber;
	private Long salary;
	private Boolean working;

	public Employee() {
		super();
	}

	public Employee(Long empId) {
		super();
		this.empId = empId;
	}

	@Embeddable
	public static class EmbeddedAddress {
		private String city;
		private String streetName;
		private String zipCode;
	}
}