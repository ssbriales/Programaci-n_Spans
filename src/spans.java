import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import com.mysql.jdbc.PreparedStatement;

public class spans {
	private Connection dbcon;
	private boolean _conectado = false;
	private usr_usuario _usr = null; 
	private ArrayList<pro_proyecto> _proyectos; 
	
	private boolean dbConectar() {
		_conectado = false;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/spans_db", "root","");
			System.out.println("Conexión ok");
			_conectado = true;
		} catch (ClassNotFoundException e) {
			System.out.println("Error al cargar el controlador");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Error en la conexión");
			e.printStackTrace();
		}

		return _conectado;
	}
	
	private void dbDesconectar() {
		try {
			dbcon.close();
			System.out.println("dbDesconectar: BBDD cerrada correctamente.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//**** Métodos Públicos
	public void Iniciar() {
		dbConectar();
	}
	
	public void Finalizar() {
		dbDesconectar();
	}
	
	public boolean Login(String _login, String _password) {
		boolean ret = false;
		
		if (_conectado) {
			//Verificar el log_in
			Integer idu = verificarLogin(_login, _password);
			if (idu > 0) {
				_usr = USR_cargarUsr(idu);	
				_proyectos = PRO_cargarProUsuario(idu);
				ret = true;
			} else {
				_usr = null;
				_proyectos = null;
				ret = false;
			}
		}
				
		return ret;
	}
	
	public void RefrescarDatos() {
		if (_conectado) {
			if (_usr != null) {
				_usr = USR_cargarUsr(_usr.id_usuario);
				_proyectos = PRO_cargarProUsuario(_usr.id_usuario);
			}
		}
	}
	
	public ArrayList<pro_proyecto> getProyectos() {
		return _proyectos;
	}
	
	public boolean IniciarHorario() {
		//Finalizar algún horario anterior.
		FinalizarHorario();
		boolean res = this.REG_iniciarHorario(_usr.id_usuario);
		return res;
	}
	
	public boolean FinalizarHorario() {
		boolean res = false;
		reg_horario hor_activo = this.REG_leerHorarioActivo(_usr.id_usuario);
		if (hor_activo != null) {
			res = this.REG_finalizarHorario(hor_activo);
		} 
		
		return res;
	}
	
	public ArrayList<reg_horario> obtenerMarcajesHorarios() {
		return this.REG_leerTodosHorarios();
	}
	
	public ArrayList<reg_proyecto> obtenerMarcajesProyectos() {
		return this.REG_leerTodosProyectos();
	}
	
	public usr_usuario obtenerUsuario(Integer id_usuario) {
		return this.USR_cargarUsr(id_usuario);
	}
	
	public pro_proyecto obtenerProyecto(String id_proyecto) {
		return this.PRO_cargarProyecto(id_proyecto);
	}
	
	public boolean IniciarProyecto(String cf_proyecto) {
		boolean res = false;
		
		res = this.REG_iniciarProyecto(_usr.id_usuario, cf_proyecto);
		
		return res;
	}
	
	public boolean FinalizarTodosProyectos() {
		boolean res = false;
		
		ArrayList<reg_proyecto> al = this.REG_leerTodosProyectosActivos(_usr.id_usuario);
		if (al != null) {
			res = true;
			for (int i = 0; i < al.size(); i++) {
				reg_proyecto p = al.get(i);
				res = res && this.REG_finalizarProyecto(p);
			}
		}
		
		return res;
	}
	
	private Integer verificarLogin(String login, String password) {
		Integer id_usuario = 0;
		String SQL = "SELECT id_usuario FROM usr_usuarios WHERE login=? AND password=?";
		PreparedStatement stmt;
		try {
			stmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			stmt.setString(1, login);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			 
			if (rs.next()) {
				id_usuario = rs.getInt(1);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id_usuario;
	}
		
	//TODO: Log...

	//USR_Usuario
	private usr_usuario USR_cargarUsr(Integer id_usuario) {
		usr_usuario miUsuario = new usr_usuario();
		
		String SQL = "SELECT * FROM usr_usuarios WHERE id_usuario=?";
		PreparedStatement stmt;
		try {
			stmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			stmt.setInt(1, id_usuario);

			ResultSet rs = stmt.executeQuery();
			 
			if (rs.next()) {
				miUsuario.readJDBC(rs);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return miUsuario;
	}
	
	
	//PRO_Proyectos
	private ArrayList<pro_proyecto> PRO_cargarProUsuario(Integer id_usuario) {
		ArrayList<pro_proyecto> miLista = new ArrayList<pro_proyecto>();
		
		String SQL = "SELECT * FROM pro_proyectos WHERE id_proyecto IN (SELECT cf_proyecto FROM pro_permisos WHERE cf_usuario=?)";
		PreparedStatement stmt;
		try {
			stmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			stmt.setInt(1, id_usuario);
			
			ResultSet rs = stmt.executeQuery();
			 
			while (rs.next()) {
				pro_proyecto miPro = new pro_proyecto();
				miPro.readJDBC(rs);
				miLista.add(miPro);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadJDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return miLista;
	}
	
	private pro_proyecto PRO_cargarProyecto(String id_proyecto) {
		pro_proyecto miProyecto = new pro_proyecto();
		
		String SQL = "SELECT * FROM pro_proyectos WHERE id_proyecto=?";
		PreparedStatement stmt;
		try {
			stmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			stmt.setString(1, id_proyecto);

			ResultSet rs = stmt.executeQuery();
			 
			if (rs.next()) {
				miProyecto.readJDBC(rs);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadJDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return miProyecto;
	}
	
	//REG Registro Horario
	private reg_horario REG_leerHorarioActivo(Integer id_usuario) {
		reg_horario ret = null;
		
		String SQL = "SELECT * FROM reg_horario WHERE finalizado = false AND cf_usuario=?";
		PreparedStatement stmt;
		try {
			stmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			stmt.setInt(1, id_usuario);
			
			ResultSet rs = stmt.executeQuery();
			 
			if (rs.next()) {
				ret = new reg_horario();
				ret.readJDBC(rs);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private boolean REG_iniciarHorario(Integer id_usuario) {
		boolean ret = false;
		Integer filasAfectadas = 0;
		
		LocalDateTime inicioT = LocalDateTime.now();
		LocalDateTime finalT = LocalDateTime.of(1980, 1, 1, 0, 0);		
		
		Timestamp tsInicioT = Timestamp.valueOf(inicioT);
		Timestamp tsFinalT = Timestamp.valueOf(finalT);
		
		try {
			String SQL = "INSERT INTO reg_horario (cf_usuario, inicioT, finalT, tiempo_total, finalizado) VALUES (?, ?, ?, 0, false)";
			PreparedStatement prepStmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			prepStmt.setInt(1, id_usuario);
			prepStmt.setTimestamp(2, tsInicioT);
			prepStmt.setTimestamp(3, tsFinalT);
			filasAfectadas = prepStmt.executeUpdate();
		}  catch (SQLException e) {
			
		};
		
		if (filasAfectadas == 1) {
			//Ha funcionado
			ret = true;
		}
		
		return ret;
	}
	
	private boolean REG_finalizarHorario(reg_horario myReg) {
		boolean ret = false;
		Integer filasAfectadas = 0; 
		
		if (myReg != null) {
			myReg.fijarFinalT();
		
			String SQL = "UPDATE reg_horario SET finalT=?, tiempo_total=?, finalizado=true WHERE id_reg=?";
			PreparedStatement prepStmt;
			try {
				prepStmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			
				// Datos a Grabar
				Timestamp tsDate = Timestamp.valueOf(myReg.inicioT);
				
				prepStmt.setTimestamp(1, tsDate);
				prepStmt.setInt(2, myReg.tiempo_total);
				
				//Dato de localización
				prepStmt.setInt(3, myReg.id_reg);
				
				filasAfectadas = prepStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (filasAfectadas == 1) {
				//Ha funcionado
				ret = true;
			}
		}
		
		return ret;
	}
	
	private ArrayList<reg_horario> REG_leerTodosHorarios() {
		ArrayList<reg_horario> ret = new ArrayList<reg_horario>();
		
		String SQL = "SELECT * FROM reg_horario ORDER BY cf_usuario";
		PreparedStatement stmt;
		try {
			stmt = (PreparedStatement) dbcon.prepareStatement(SQL);
						
			ResultSet rs = stmt.executeQuery();
			 
			while (rs.next()) {
				reg_horario proy = new reg_horario();
				proy.readJDBC(rs);
				ret.add(proy);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	//REG Registro Proyecto
	private reg_proyecto REG_leerProyectoActivo(Integer id_usuario) {
		reg_proyecto ret = null;
		
		String SQL = "SELECT * FROM reg_proyecto WHERE finalizado=false AND cf_usuario=?";
		PreparedStatement stmt;
		try {
			stmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			stmt.setInt(1, id_usuario);
			
			ResultSet rs = stmt.executeQuery();
			 
			if (rs.next()) {
				ret = new reg_proyecto();
				ret.readJDBC(rs);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private ArrayList<reg_proyecto> REG_leerTodosProyectosActivos(Integer id_usuario) {
		ArrayList<reg_proyecto> ret = new ArrayList<reg_proyecto>();
		
		
		String SQL = "SELECT * FROM reg_proyecto WHERE finalizado=false AND cf_usuario=?";
		PreparedStatement stmt;
		try {
			stmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			stmt.setInt(1, id_usuario);
			
			ResultSet rs = stmt.executeQuery();
			 
			while (rs.next()) {
				reg_proyecto proy = new reg_proyecto();
				proy.readJDBC(rs);
				ret.add(proy);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private boolean REG_iniciarProyecto(Integer id_usuario, String cf_proyecto) {
		boolean ret = false;
		Integer filasAfectadas = 0;
		
		LocalDateTime inicioT = LocalDateTime.now();
		LocalDateTime finalT = LocalDateTime.of(1980, 1, 1, 0, 0);		
		
		Timestamp tsInicioT = Timestamp.valueOf(inicioT);
		Timestamp tsFinalT = Timestamp.valueOf(finalT);
		
		String SQL = "INSERT INTO reg_proyecto (cf_usuario, inicioT, finalT, tiempo_total, finalizado, cf_proyecto) VALUES (?, ?, ?, 0, false, ?)";
		PreparedStatement prepStmt;
		try {
			prepStmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			prepStmt.setInt(1, id_usuario);
			prepStmt.setTimestamp(2, tsInicioT);
			prepStmt.setTimestamp(3, tsFinalT);
			prepStmt.setString(4, cf_proyecto);
			filasAfectadas = prepStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (filasAfectadas == 1) {
			//Ha funcionado
			ret = true;
		}
		
		return ret;
	}
	
	private boolean REG_finalizarProyecto(reg_proyecto myReg) {
		boolean ret = false;
		Integer filasAfectadas = 0; 
		
		if (myReg != null) {
			myReg.fijarFinalT();
		
			String SQL = "UPDATE reg_proyecto SET finalT=?, tiempo_total=?, finalizado=true WHERE id_reg=?";
			PreparedStatement prepStmt;
			try {
				prepStmt = (PreparedStatement) dbcon.prepareStatement(SQL);
			
				// Datos a Grabar
				Timestamp tsDate = Timestamp.valueOf(myReg.finalT);
				
				prepStmt.setTimestamp(1, tsDate);
				prepStmt.setInt(2, myReg.tiempo_total);
				
				//Dato de localización
				prepStmt.setInt(3, myReg.id_reg);
				
				filasAfectadas = prepStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (filasAfectadas == 1) {
				//Ha funcionado
				ret = true;
			}
		}
		
		return ret;
	}
	
	private ArrayList<reg_proyecto> REG_leerTodosProyectos() {
		ArrayList<reg_proyecto> ret = new ArrayList<reg_proyecto>();
		
		String SQL = "SELECT * FROM reg_proyecto ORDER BY cf_proyecto";
		PreparedStatement stmt;
		try {
			stmt = (PreparedStatement) dbcon.prepareStatement(SQL);
						
			ResultSet rs = stmt.executeQuery();
			 
			while (rs.next()) {
				reg_proyecto proy = new reg_proyecto();
				proy.readJDBC(rs);
				ret.add(proy);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	java.util.Date convertToDateViaInstant(LocalDateTime dateToConvert) {
	    return java.util.Date
	      .from(dateToConvert.atZone(ZoneId.systemDefault())
	      .toInstant());
	}
}
