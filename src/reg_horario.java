import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class reg_horario extends spansobject {
	public Integer id_reg = 0;
	public Integer cf_usuario = 0;
	public LocalDateTime inicioT = LocalDateTime.of(1980, 1, 1, 0, 0);
	public LocalDateTime finalT = LocalDateTime.of(1980, 1, 1, 0, 0);
	public Integer tiempo_total = 0;
	public Boolean finalizado = false;

	public reg_horario() {
	}
	
	public void readJDBC(ResultSet rs) {
		try {
			id_reg = rs.getInt("id_reg");
			cf_usuario = rs.getInt("cf_usuario");
			Timestamp tsI = rs.getTimestamp("inicioT");
			inicioT = tsI.toLocalDateTime();
			Timestamp tsF = rs.getTimestamp("finalT");
			finalT = tsF.toLocalDateTime();
			tiempo_total = rs.getInt("tiempo_total");
			finalizado = rs.getBoolean("finalizado");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return "Registro Horario: #" + id_reg+ " - Inicio: " + inicioT + "   Final: " + finalT +"   Tiempo: " + tiempo_total;
	}
	
	private Integer segundos() {
		Integer res = 0;
		
		Long resL = ChronoUnit.SECONDS.between(inicioT, finalT);
		res = resL.intValue();
		
		return res;
	}
	
	public void fijarInicioT() {
		inicioT = LocalDateTime.now();
	}
	
	public void fijarFinalT() {
		finalT = LocalDateTime.now();
		tiempo_total = segundos();
	}

}