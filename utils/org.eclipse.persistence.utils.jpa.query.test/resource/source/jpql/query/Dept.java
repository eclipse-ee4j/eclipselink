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
	@NamedQuery(name = "dept.findAll",     query = "select o from Dept o"),
	@NamedQuery(name = "dept.dname",       query = "select o from Dept o where o.dname in (:dname1, :dname2, :dname3)"),
	@NamedQuery(name = "dept.floorNumber", query = "select Dept.floorNumber from Dept Dept")
})
public class Dept implements Serializable
{
	@Id
	@Column(nullable = false)
	private Long deptno;
	@Column(length = 14)
	private String dname;
	@Column(length = 13)
	private String loc;
	@OneToMany(mappedBy = "dept")
	private List<Employee> empList;
	private int floorNumber;

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

	public Long getDeptno()
	{
		return deptno;
	}

	public void setDeptno(Long deptno)
	{
		this.deptno = deptno;
	}

	public String getDname()
	{
		return dname;
	}

	public void setDname(String dname)
	{
		this.dname = dname;
	}

	public String getLoc()
	{
		return loc;
	}

	public void setLoc(String loc)
	{
		this.loc = loc;
	}

	public List<Employee> getEmpList()
	{
		return empList;
	}

	public void setEmpList(List<Employee> empList)
	{
		this.empList = empList;
	}

	public Employee addEmp(Employee emp)
	{
		getEmpList().add(emp);
		emp.setDept(this);
		return emp;
	}

	public Employee removeEmp(Employee emp)
	{
		getEmpList().remove(emp);
		emp.setDept(null);
		return emp;
	}

	public int getFloorNumber()
	{
		return floorNumber;
	}

	public void setFloorNumber(int floorNumber)
	{
		this.floorNumber = floorNumber;
	}
}