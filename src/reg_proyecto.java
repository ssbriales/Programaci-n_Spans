import java.sql.ResultSet;
import java.sql.SQLException;

public class reg_proyecto extends reg_horario {
	public String cf_proyecto = "";
	
	public void readJDBC(ResultSet rs) {
		try {
			cf_proyecto = rs.getString("cf_proyecto");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.readJDBC(rs);
	}
	
	@Override
	public String toString() {
		return "Registro Proyecto: #" + id_reg + "   Proyecto: " + cf_proyecto + " - Inicio: " + inicioT + "   Final: " + finalT +"   Tiempo: " + tiempo_total;
	}
}