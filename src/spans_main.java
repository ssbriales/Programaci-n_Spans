import java.awt.EventQueue;

import javax.swing.JFrame;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JTextArea;

public class spans_main {

	private JFrame frame;
	private JTextField txtLogin;
	private JPasswordField txtPassword;

	private spans myApp = new spans();
	private JButton btnLogin;
	private JList<pro_proyecto> listProyectos;
	private JLabel lblNuevoProyecto;
	private JLabel lblProyectoActual;
	private JButton btnNuevoProyecto;
	private JButton btnRegHorIniciar;
	private JButton btnRegHorFinalizar;
	private JLabel lblContadorHorario;
	private JButton btnPararProyectos;
	private JPanel panelReporting;
	private JLabel lblReportingTitle;
	private JTextArea txtReport;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					spans_main window = new spans_main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public spans_main() {
		initialize();
		myApp.Iniciar();
	}

	private void goLogin() {
		String _login = txtLogin.getText();
		String _password = txtPassword.getText();
		
		boolean login_ok = myApp.Login(_login, _password);
		
		if (login_ok) {
			txtLogin.setEnabled(false);
			txtPassword.setEnabled(false);
			btnLogin.setEnabled(false);
			goRecargarProyectos();
			this.btnRegHorIniciar.setEnabled(true);
			this.btnRegHorFinalizar.setEnabled(false);
		} else {
			//btnLogin.setText("Reintentar");
			JOptionPane.showMessageDialog(null, "Login incorrecto. Reintentar, por favor.");
			this.btnRegHorIniciar.setEnabled(false);
			this.btnRegHorFinalizar.setEnabled(false);
		}
	}
	
	private void goRecargarProyectos() {
		/* Visualizar los proyectos */
		
		ArrayList<pro_proyecto> alProyectos = myApp.getProyectos();
		
		if (alProyectos != null) {
			DefaultListModel<pro_proyecto> listModel = new DefaultListModel<pro_proyecto>();
			for (int i = 0; i < alProyectos.size(); i++)
			{
			    listModel.addElement(alProyectos.get(i));
			}
			listProyectos.setModel(listModel);
		}
	}
	
	private void goHorarioIniciar() {
		myApp.IniciarHorario();
		this.btnRegHorIniciar.setEnabled(false);
		this.btnRegHorFinalizar.setEnabled(true);
		this.lblContadorHorario.setText("En jornada laboral...");
	}
	
	private void goHorarioFinalizar() {
		myApp.FinalizarHorario();
		this.btnRegHorIniciar.setEnabled(true);
		this.btnRegHorFinalizar.setEnabled(false);
		this.lblContadorHorario.setText("Horario detenido.");
		goPararProyectos();
	}
	
	private void goSeleccionProyecto() {
		/* Cambia la selección del proyecto, para poder ser inciado*/
		if (listProyectos.getSelectedValue() != null) {
			pro_proyecto newProyecto = listProyectos.getSelectedValue();
			this.lblNuevoProyecto.setText(newProyecto.id_proyecto + " - " + newProyecto.descripcion);
			this.btnNuevoProyecto.setText("Iniciar " + newProyecto.descripcion);
		}
	}
	
	private void goIniciarProyecto() {
		/* Cambia la selección del proyecto, para poder ser inciado*/
		if (listProyectos.getSelectedValue() != null) {
			pro_proyecto newProyecto = listProyectos.getSelectedValue();
			this.lblProyectoActual.setText(newProyecto.id_proyecto + " - " + newProyecto.descripcion);
			this.lblNuevoProyecto.setText("");
			this.btnNuevoProyecto.setText("Seleccione y Pulse para Iniciar");
			
			//Finalizar Proyectos Anteriores del usuario.
			myApp.FinalizarTodosProyectos();
			
			//Inciar Nuevo Proyecto
			myApp.IniciarProyecto(newProyecto.id_proyecto);
		}
	}
	
	private void goPararProyectos() {
		//Finalizar Proyectos Anteriores del usuario.
		myApp.FinalizarTodosProyectos();
		this.lblProyectoActual.setText("");
		this.lblNuevoProyecto.setText("");
		this.btnNuevoProyecto.setText("Seleccione y Pulse para Iniciar");
	}
	
	private void goReporteUsuario() {
		ArrayList<reg_horario> miLista = myApp.obtenerMarcajesHorarios();
		rep_usuario miReport = new rep_usuario();
		
		//Recorro la lista y acumulo datos
		usr_usuario usr = null;
		Integer _cache_last_id = -1;
		for (Integer i = 0; i < miLista.size(); i++) {
			reg_horario rh = miLista.get(i);
			
			if ((usr == null) || (_cache_last_id != rh.cf_usuario)) {
				usr = myApp.obtenerUsuario(rh.cf_usuario);	
				_cache_last_id = usr.id_usuario;
			}
			
			miReport.setValor(rh.cf_usuario.toString(), rh.tiempo_total, usr.nombre);
		}
		
		//Presentar resultados
		try {
			this.txtReport.setText(miReport.obtenerResultados());
		} catch (NoResultsException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "No hay resultados a mostrar.");
			e.printStackTrace();
		}
	}
	
	private void goReporteProyecto() {
		ArrayList<reg_proyecto> miLista = myApp.obtenerMarcajesProyectos();
		rep_proyecto miReport = new rep_proyecto();
		
		//Recorro la lista y acumulo datos
		pro_proyecto proy = null;
		String _cache_last_id = "-1";
		for (Integer i = 0; i < miLista.size(); i++) {
			reg_proyecto rp = miLista.get(i);
			
			if ((proy == null) || (!_cache_last_id.contentEquals(rp.cf_proyecto))) {
				proy = myApp.obtenerProyecto(rp.cf_proyecto);	
				_cache_last_id = proy.id_proyecto;
			}
			
			miReport.setValor(rp.cf_proyecto, rp.tiempo_total, proy.toString());
		}
		
		//Presentar resultados
		try {
			this.txtReport.setText(miReport.obtenerResultados());
		} catch (NoResultsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "No hay resultados a mostrar.");
		}
	}
	
	
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1296, 590);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panelRegHorario = new JPanel();
		panelRegHorario.setBackground(new Color(253, 245, 230));
		panelRegHorario.setBounds(340, 13, 316, 126);
		frame.getContentPane().add(panelRegHorario);
		panelRegHorario.setLayout(null);
		
		JLabel lblRegJorTitulo = new JLabel("Registro de la Jornada Laboral");
		lblRegJorTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		lblRegJorTitulo.setFont(new Font("Tahoma", Font.BOLD, 19));
		lblRegJorTitulo.setBounds(12, 13, 292, 23);
		panelRegHorario.add(lblRegJorTitulo);
		
		btnRegHorIniciar = new JButton("Iniciar");
		btnRegHorIniciar.setEnabled(false);
		btnRegHorIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnRegHorIniciar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				goHorarioIniciar();
			}
		});
		btnRegHorIniciar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnRegHorIniciar.setBounds(12, 49, 108, 29);
		panelRegHorario.add(btnRegHorIniciar);
		
		btnRegHorFinalizar = new JButton("Finalizar");
		btnRegHorFinalizar.setEnabled(false);
		btnRegHorFinalizar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				goHorarioFinalizar();
			}
		});
		btnRegHorFinalizar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnRegHorFinalizar.setBounds(197, 49, 108, 29);
		panelRegHorario.add(btnRegHorFinalizar);
		
		lblContadorHorario = new JLabel("(contador horario)");
		lblContadorHorario.setHorizontalAlignment(SwingConstants.CENTER);
		lblContadorHorario.setBounds(12, 91, 292, 20);
		panelRegHorario.add(lblContadorHorario);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(255, 248, 220));
		panel.setBounds(12, 152, 644, 380);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblRegProyTitulo = new JLabel("Registro de Tiempo de Proyecto");
		lblRegProyTitulo.setHorizontalAlignment(SwingConstants.LEFT);
		lblRegProyTitulo.setFont(new Font("Tahoma", Font.BOLD, 19));
		lblRegProyTitulo.setBounds(12, 13, 312, 23);
		panel.add(lblRegProyTitulo);
		
		listProyectos = new JList<pro_proyecto>();
		listProyectos.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				goSeleccionProyecto();
			}
		});
		listProyectos.setBounds(12, 127, 620, 242);
		panel.add(listProyectos);
		
		JLabel lblNewLabel_1 = new JLabel("Proyecto en Ejecuci\u00F3n:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(12, 49, 136, 16);
		panel.add(lblNewLabel_1);
		
		lblProyectoActual = new JLabel("-");
		lblProyectoActual.setBounds(160, 49, 241, 16);
		panel.add(lblProyectoActual);
		
		JLabel lblNewLabel_1_1 = new JLabel("Proyecto a Iniciar:");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1_1.setBounds(12, 78, 136, 16);
		panel.add(lblNewLabel_1_1);
		
		lblNuevoProyecto = new JLabel("-");
		lblNuevoProyecto.setBounds(160, 78, 284, 16);
		panel.add(lblNuevoProyecto);
		
		btnNuevoProyecto = new JButton("Iniciar y Parar anteriores");
		btnNuevoProyecto.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				goIniciarProyecto();
			}
		});
		btnNuevoProyecto.setBounds(12, 100, 482, 25);
		panel.add(btnNuevoProyecto);
		
		btnPararProyectos = new JButton("Parar Todo");
		btnPararProyectos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				goPararProyectos();
			}
		});
		btnPararProyectos.setBounds(535, 15, 97, 25);
		panel.add(btnPararProyectos);
		
		JPanel panelLogIn = new JPanel();
		panelLogIn.setLayout(null);
		panelLogIn.setBackground(new Color(253, 245, 230));
		panelLogIn.setBounds(12, 13, 316, 126);
		frame.getContentPane().add(panelLogIn);
		
		JLabel lblIntroduccinDeCredenciales = new JLabel("Introducci\u00F3n de Credenciales");
		lblIntroduccinDeCredenciales.setHorizontalAlignment(SwingConstants.CENTER);
		lblIntroduccinDeCredenciales.setFont(new Font("Tahoma", Font.BOLD, 19));
		lblIntroduccinDeCredenciales.setBounds(12, 13, 292, 23);
		panelLogIn.add(lblIntroduccinDeCredenciales);
		
		btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				goLogin();
			}
		});
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnLogin.setBounds(110, 84, 108, 29);
		panelLogIn.add(btnLogin);
		
		txtLogin = new JTextField();
		txtLogin.setBounds(37, 49, 116, 22);
		panelLogIn.add(txtLogin);
		txtLogin.setColumns(10);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(184, 49, 108, 22);
		panelLogIn.add(txtPassword);
		
		panelReporting = new JPanel();
		panelReporting.setBackground(new Color(240, 248, 255));
		panelReporting.setBounds(668, 13, 598, 517);
		frame.getContentPane().add(panelReporting);
		panelReporting.setLayout(null);
		
		lblReportingTitle = new JLabel("Panel de Reportes");
		lblReportingTitle.setFont(new Font("Tahoma", Font.BOLD, 19));
		lblReportingTitle.setBounds(12, 13, 190, 23);
		panelReporting.add(lblReportingTitle);
		
		txtReport = new JTextArea();
		txtReport.setForeground(new Color(0, 0, 128));
		txtReport.setBounds(12, 49, 574, 455);
		panelReporting.add(txtReport);
		
		JButton btnReportUsuario = new JButton("Totales por Trabajador");
		btnReportUsuario.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				goReporteUsuario();
			}
		});
		btnReportUsuario.setBounds(244, 15, 165, 25);
		panelReporting.add(btnReportUsuario);
		
		JButton btnReportProyecto = new JButton("Totales por Proyecto");
		btnReportProyecto.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				goReporteProyecto();				
			}
		});
		btnReportProyecto.setBounds(421, 15, 165, 25);
		panelReporting.add(btnReportProyecto);

	}
}
