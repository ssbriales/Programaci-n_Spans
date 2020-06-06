import java.sql.ResultSet;
import java.sql.SQLException;

public class usr_usuario extends spansobject {
	public Integer id_usuario = 0;;
	public String nombre = "";
	public String login = "";
	public String password = "";
	
	public usr_usuario () {
		
	}
	
	public usr_usuario (Integer id_usuario, String nombre, String login, String password) {
		
		this.id_usuario = id_usuario;
		this.nombre = nombre;
		this.login = login;
		this.password = password;
	}
	
	public void readJDBC(ResultSet rs) {
		try {
			id_usuario = rs.getInt("id_usuario");
			nombre = rs.getString("nombre");
			login = rs.getString("login");
			password = rs.getString("password");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return "Usuario: #" + id_usuario+ " - Nombre: " + nombre + "   Login: " + login;
	}
}