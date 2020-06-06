
public class rep_proyecto  extends rep_acumulados {
	public void agregar_reg_proyecto(reg_proyecto reg, String descripcion) {
		this.setValor(reg.cf_proyecto.toString(), reg.tiempo_total, descripcion);
	}

	protected void asignarTitulo() {
		_titulo = "Reporte de Tiempo por Proyecto";
	}
}
