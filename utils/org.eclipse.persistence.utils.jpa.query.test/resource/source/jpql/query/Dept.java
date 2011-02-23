package jpql.query;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries
({
	@NamedQuery(name = "dept.and.multiple", query = "DELETE FROM Dept d WHERE d.dname = 'DEPT A' AND d.role = 'ROLE A' AND d.loc = 'LOCATION A'"),
	@NamedQuery(name = "dept.findAll",      query = "select o from Dept o"),
	@NamedQuery(name = "dept.dname",        query = "select o from Dept o where o.dname in (:dname1, :dname2, :dname3)"),
	@NamedQuery(name = "dept.floorNumber",  query = "select d.floorNumber from Dept d"),
	@NamedQuery(name = "dept.new1",         query = "SELECT NEW java.util.Vector(d.dname) FROM Dept d")
})
public class Dept implements Serializable
{
	@Id
	@Column(nullable = false)
	private Long deptno;
	@Column(length = 14)
	private String dname;
	@OneToMany(mappedBy = "dept")
	private List<Employee> empList;
	private int floorNumber;
	@Column(length = 13)
	private String loc;
	private String role;

	public Dept()
	{
		super();
	}

	public Dept(Long deptno, String dname, String loc)
	{
		super();
		this.deptno = deptno;
		this.dname = dname;
		this.loc = loc;
	}

	public Employee addEmp(Employee emp)
	{
		getEmpList().add(emp);
		emp.setDept(this);
		return emp;
	}

	public Long getDeptno()
	{
		return deptno;
	}

	public String getDname()
	{
		return dname;
	}

	public List<Employee> getEmpList()
	{
		return empList;
	}

	public int getFloorNumber()
	{
		return floorNumber;
	}

	public String getLoc()
	{
		return loc;
	}

	public String getRole()
	{
		return role;
	}

	public Employee removeEmp(Employee emp)
	{
		getEmpList().remove(emp);
		emp.setDept(null);
		return emp;
	}

	public void setDeptno(Long deptno)
	{
		this.deptno = deptno;
	}

	public void setDname(String dname)
	{
		this.dname = dname;
	}

	public void setEmpList(List<Employee> empList)
	{
		this.empList = empList;
	}

	public void setFloorNumber(int floorNumber)
	{
		this.floorNumber = floorNumber;
	}

	public void setLoc(String loc)
	{
		this.loc = loc;
	}
}