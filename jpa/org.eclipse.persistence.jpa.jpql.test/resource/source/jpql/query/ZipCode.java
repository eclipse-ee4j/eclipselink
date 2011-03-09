package jpql.query;

import javax.persistence.Embeddable;

@Embeddable
public class ZipCode
{
	private int mainCode;
	private int code;

	public ZipCode()
	{
		super();
	}

	public int getMainCode()
	{
		return mainCode;
	}

	public void setMainCode(int mainCode)
	{
		this.mainCode = mainCode;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}
}