import java.sql.ResultSet;

public abstract class spansobject {
	public abstract String toString();
	public abstract void readJDBC(ResultSet rs) throws ReadJDBCException; 
}
