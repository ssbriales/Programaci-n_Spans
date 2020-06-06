

public class rep_usuario extends rep_acumulados {
	public void agregar_reg_horario(reg_horario reg, String descripcion) {
		this.setValor(reg.cf_usuario.toString(), reg.tiempo_total, descripcion);
	}
	
	protected void asignarTitulo() {
		_titulo = "Reporte de Tiempo por Usuario";
	}
}
