import java.sql.ResultSet;
import java.sql.SQLException;

public class pro_permiso extends spansobject {

	public Integer cf_usuario = 0;
	public String cf_proyecto = "";
	public String permiso = "";

	public pro_permiso (Integer cf_usuario, String cf_proyecto, String permiso) {

	}
	
	public void SetPermiso (Integer id_usuario) {
		
		String SQL = "SELECT * FROM pro_proyectos WHERE id_proyecto IN (SELECT cf_proyecto FROM pro_proyectos WHERE cf_usuario=?)";
	}
	
	public void readJDBC(ResultSet rs) {
		try {
			cf_usuario = rs.getInt("cf_usuario");
			cf_proyecto = rs.getString("cf_proyecto");
			permiso = rs.getString("permiso");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String myStr = "Permiso - Usr: " + cf_usuario + "  Proyecto: " + cf_proyecto;
		
		return myStr;
	}
}