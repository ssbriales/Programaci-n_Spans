import java.util.ArrayList;

public abstract class rep_acumulados {
	ArrayList<bloque> bloques = new ArrayList<bloque>();
	
	protected String _titulo = ""; 
	protected abstract void asignarTitulo();
	
	public rep_acumulados() {
		//Constructor
		asignarTitulo();
	}
	
	protected class bloque {
		String key = "";
		Integer valor = 0;
		String descripcion = "";
		
		public bloque(String _key, String _descripcion) {
			key = _key;
			descripcion = _descripcion;
		}
		
		public void setValorAdd(Integer nuevovalor) {
			valor = valor + nuevovalor;
		}
	}

	public void setValor(String key, Integer valor, String descripcion) {
	
		//Buscar si existe la clave _key
		Integer posicionClave = -1;
		for (Integer i = 0; i < bloques.size(); i++) {
			bloque miBloque = bloques.get(i);
			if (miBloque.key.equals(key)) {
				//Encontrado
				posicionClave = i;
				i = bloques.size();  //Forzar la salida del For.
			}
		}
		
		//Actualizar datos
		if (posicionClave >= 0) {
			//Lo hemos encontrado.
			bloques.get(posicionClave).setValorAdd(valor);
		} else {
			//No encontrado
			bloque miBloque = new bloque(key, descripcion);
			miBloque.setValorAdd(valor);
			bloques.add(miBloque);
		}
		
	}
	
	public String obtenerResultados() throws NoResultsException {
		String res = "";
		
		res = _titulo + "\n\n";
		
		if (bloques != null) {
			//Recorrer la lista y componer String de Resultados
			if (bloques.size() > 0 ) {
				for (Integer i = 0; i < bloques.size(); i++) {
					bloque miBloque = bloques.get(i);
					res = res + miBloque.descripcion +"    Tiempo: " + miBloque.valor.toString() +" segundos. \n";
				}
			} else {
				//Hay 0 registros
				throw new NoResultsException("No hay resultados a mostrar.");
			}
		} else {
			throw new NoResultsException();
		}
			
		
		return res;
	}
}


