import java.sql.ResultSet;
import java.sql.SQLException;

public class pro_proyecto extends spansobject {
	public String id_proyecto = "";
	public String descripcion = "";

	public pro_proyecto() {
	}
	
	public pro_proyecto(String id_proyecto, String descripcion) {
		this.id_proyecto = id_proyecto;
		this.descripcion = descripcion;
	}
	
	public void readJDBC(ResultSet rs) throws ReadJDBCException {
		try {
			id_proyecto = rs.getString("id_proyecto");
			descripcion = rs.getString("descripcion");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new ReadJDBCException("ReadJDBC: Error en la lectura o campos mal formados.");
			//Generar excepción
		}
	}
	
	public String toString() {
		return "Proyecto: " + id_proyecto + " - " + descripcion;
	}
}