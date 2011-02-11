package jpql.query;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries
({
   @NamedQuery(name="Employee.findAll",           query="SELECT e FROM Employee e"),
   @NamedQuery(name="employee.deptBase",          query="SELECT e FROM Employee e WHERE e.department = :dept AND e.salary > :base"),
   @NamedQuery(name="employee.dept",              query="SELECT e FROM Employee e WHERE e.salary = (SELECT MAX(e.salary) FROM Employee e WHERE e.department = :dept)"),
   @NamedQuery(name="employee.?1",                query="SELECT e FROM Employee e WHERE e.name = ?1 ORDER BY e.name"),
   @NamedQuery(name="employee.update.positional", query="UPDATE Employee e SET e.manager = ?1 WHERE e.department = ?2"),
   @NamedQuery(name="employee.delete.dept",       query="DELETE FROM Employee e WHERE e.department = :dept"),
   @NamedQuery(name="employee.deptno",            query="select e from Employee e where e.dept.deptno in (:deptno)"),
   @NamedQuery(name="employee.collection",        query="SELECT e.name, d.dname FROM Employee e, Dept d"),
   @NamedQuery(name="employee.sum",               query="SELECT SUM(e.salary) FROM Employee e"),
   @NamedQuery(name="employee.lower",             query="SELECT LOWER(e.name) FROM Employee e"),
   @NamedQuery(name="employee.upper",             query="SELECT UPPER(e.name) FROM Employee e"),
   @NamedQuery(name="employee.max",               query="SELECT MAX(e.salary) FROM Employee e"),
   @NamedQuery(name="employee.min",               query="SELECT MIN(e.salary) FROM Employee e"),
   @NamedQuery(name="employee.mod",               query="SELECT MOD(e.salary, e.empId) FROM Employee e"),
   @NamedQuery(name="employee.trim",              query="SELECT TRIM(e.name) FROM Employee e"),
   @NamedQuery(name="employee.subquery",          query="SELECT e FROM Employee e WHERE EXISTS (SELECT p FROM Project p JOIN p.employees emp WHERE emp = e AND p.name = :name)"),
   @NamedQuery(name="employee.caseInsensitive",   query="SELECT e FROM Employee E"),
   @NamedQuery(name="employee.update1",           query="UPDATE Employee AS e SET e."),
   @NamedQuery(name="employee.update2",           query="UPDATE Alias a"),
   @NamedQuery(name="employee.update3",           query="UPDATE A"),
   @NamedQuery(name="employee.func1",             query="SELECT FUNC('toString', e.name) FROM Employee e"),
   @NamedQuery(name="employee.func2",             query="SELECT FUNC('age', e.empId, e.salary) FROM Employee e"),
   @NamedQuery(name="employee.func3",             query="SELECT FUNC('age', e.empId, e.name) FROM Employee e"),
   @NamedQuery(name="employee.func4",             query="SELECT FUNC('age', e.empId, ?name) FROM Employee e"),
   @NamedQuery(name="employee.resultVariable1",   query="SELECT e.name AS n From Employee e"),
   @NamedQuery(name="employee.resultVariable2",   query="SELECT e.name n From Employee e"),
   @NamedQuery(name="employee.resultVariable3",   query="SELECT e.salary / 1000D n From Employee e"),
   @NamedQuery(name="employee.index",             query="SELECT INDEX(e) FROM Employee e"),
   @NamedQuery(name="employee.subquery.code_1",   query="SELECT e FROM Employee e WHERE e.salary > (SELECT AVG(f.salary) FROM Employee f)")
})
public class Employee implements Serializable
{
	private String department;
	@Id
	@Column(name="EMP_ID", nullable = false)
	private Long empId;
	private String manager;
	private String name;
	private Long salary;
   @ManyToOne
   @JoinColumn(name="DEPTNO")
	private Dept dept;
   private Boolean working;

	public Employee()
	{
		super();
	}

	public Employee(Long empId)
	{
		super();
		this.empId = empId;
	}

	public String getDepartment()
	{
		return department;
	}

	public Dept getDept()
	{
		return dept;
	}

	public Long getEmpId()
	{
		return empId;
	}

	public String getManager()
	{
		return manager;
	}

	public String getName()
	{
		return name;
	}

	public Long getSalary()
	{
		return salary;
	}

	public void setDepartment(String department)
	{
		this.department = department;
	}

	public void setDept(Dept dept)
	{
		this.dept = dept;
	}

	public void setEmpId(Long empId)
	{
		this.empId = empId;
	}

	public void setManager(String manager)
	{
		this.manager = manager;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setSalary(Long salary)
	{
		this.salary = salary;
	}
}