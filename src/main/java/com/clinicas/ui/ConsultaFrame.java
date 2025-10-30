package com.clinicas.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;

import com.clinicas.dao.ConsultaDao;
import com.clinicas.dao.ConsultaDaoJdbc;
import com.clinicas.dao.DoctorDao;
import com.clinicas.dao.DoctorDaoJdbc;
import com.clinicas.dao.PacienteDao;
import com.clinicas.dao.PacienteDaoJdbc;
import com.clinicas.dao.ReservaDao;
import com.clinicas.dao.ReservaDaoJdbc;

import com.clinicas.model.Consulta;
import com.clinicas.model.Doctor;
import com.clinicas.model.Paciente;
import com.clinicas.model.Reserva;

public class ConsultaFrame extends JInternalFrame {

	private final PacienteDao pDao = new PacienteDaoJdbc();
	private final DoctorDao dDao = new DoctorDaoJdbc();
	private final ReservaDao rDao = new ReservaDaoJdbc();
	private final ConsultaDao cDao = new ConsultaDaoJdbc();

	private JComboBox<Doctor> cboDoctor;
	private JTable tbl; // reservas pendientes del doctor
	private JTextArea txtSint; // síntomas / notas
	private JComboBox<Integer> cboNivel; // nivel de emergencia (1-5)
	private JButton btnConfirmar; // confirmar consulta (finaliza reserva)

	private Integer reservaSeleccionada = null;

	public ConsultaFrame() {
		super("Consultas", true, true, true, true);
		initComponents();
		loadDoctors();
		loadPendientes();
	}

	private void initComponents() {
		setSize(900, 560);
		getContentPane().setLayout(new BorderLayout(8, 8));

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
		top.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"<html><b>Seleccionar doctor</b></html>", TitledBorder.LEFT, TitledBorder.TOP));
		top.add(new JLabel("Doctor"));
		cboDoctor = new JComboBox<Doctor>();
		top.add(cboDoctor);
		JButton btnRefrescar = new JButton("Refrescar");
		top.add(btnRefrescar);
		getContentPane().add(top, BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(6, 6));
		center.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"<html><b>Reservas pendientes</b></html>", TitledBorder.LEFT, TitledBorder.TOP));
		tbl = new JTable();
		center.add(new JScrollPane(tbl), BorderLayout.CENTER);
		getContentPane().add(center, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout(6, 6));
		bottom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"<html><b>Completar consulta</b></html>", TitledBorder.LEFT, TitledBorder.TOP));

		JPanel form = new JPanel(new GridLayout(1, 2, 8, 8));
		JPanel pLeft = new JPanel(new BorderLayout(6, 6));
		pLeft.add(new JLabel("Síntomas / Notas:"), BorderLayout.NORTH);
		txtSint = new JTextArea(5, 20);
		pLeft.add(new JScrollPane(txtSint), BorderLayout.CENTER);
		form.add(pLeft);

		JPanel pRight = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
		pRight.add(new JLabel("Nivel emergencia"));
		cboNivel = new JComboBox<Integer>(new Integer[] { 1, 2, 3, 4, 5 });
		pRight.add(cboNivel);
		btnConfirmar = new JButton("Confirmar consulta");
		pRight.add(btnConfirmar);
		form.add(pRight);

		bottom.add(form, BorderLayout.CENTER);
		getContentPane().add(bottom, BorderLayout.SOUTH);

		// Listeners
		cboDoctor.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadPendientes();
			}
		});
		btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadPendientes();
			}
		});
		btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onConfirmar();
			}
		});
		tbl.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			public void valueChanged(javax.swing.event.ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
					onPick();
			}
		});
	}

	private void loadDoctors() {
		try {
			cboDoctor.removeAllItems();
			for (Doctor d : dDao.search("")) {
				cboDoctor.addItem(d);
			}
			if (cboDoctor.getItemCount() > 0) {
				cboDoctor.setSelectedIndex(0);
			}
		} catch (Exception ignored) {
		}
	}

	private void loadPendientes() {
		try {
			Doctor d = (Doctor) cboDoctor.getSelectedItem();
			if (d == null)
				return;

			java.util.List<Reserva> list = rDao.listUpcomingByDoctorAndEstado(d.getIdDoctor(), "PENDIENTE");

			// columnas: ID (oculta), Paciente, Motivo, Inicio
			DefaultTableModel m = new DefaultTableModel(new Object[] { "ID", "Paciente", "Motivo", "Inicio" }, 0) {
				public boolean isCellEditable(int r, int c) {
					return false;
				}
			};

			for (Reserva r : list) {
				Paciente p = pDao.findById(r.getIdPaciente());
				String nombre = p != null
						? String.format("%s %s (%s)", nullSafe(p.getNom1()), nullSafe(p.getApe1()),
								nullSafe(p.getCedula()))
						: String.valueOf(r.getIdPaciente());
				String motivo = r.getMotivo() != null ? r.getMotivo() : "";
				m.addRow(new Object[] { r.getIdReserva(), nombre, motivo, r.getFechaInicio() });
			}

			tbl.setModel(m);
			if (tbl.getColumnModel().getColumnCount() > 0) {
				tbl.removeColumn(tbl.getColumnModel().getColumn(0)); // oculta ID
			}
			// aplicar renderer dd/MM/yyyy HH:mm a "Inicio" (índice visible 2 tras ocultar
			// ID)
			if (tbl.getColumnModel().getColumnCount() >= 3) {
				tbl.getColumnModel().getColumn(2).setCellRenderer(UiFormats.dateTimeRenderer());
			}

			reservaSeleccionada = null;
			txtSint.setText("");

		} catch (Exception ignored) {
		}
	}

	private void onPick() {
		int viewRow = tbl.getSelectedRow();
		if (viewRow < 0) {
			reservaSeleccionada = null;
			return;
		}
		int modelRow = tbl.convertRowIndexToModel(viewRow);
		Object idObj = tbl.getModel().getValueAt(modelRow, 0); // col 0 del modelo = ID (oculta)
		if (idObj == null) {
			reservaSeleccionada = null;
			return;
		}
		reservaSeleccionada = (idObj instanceof Integer) ? ((Integer) idObj) : Integer.valueOf(idObj.toString());
	}

	private void onConfirmar() {
		if (reservaSeleccionada == null) {
			JOptionPane.showMessageDialog(this, "Seleccione una reserva pendiente.");
			return;
		}
		try {
			Consulta c = new Consulta();
			c.setIdReserva(reservaSeleccionada);
			c.setSintomas(txtSint.getText());
			c.setNivelEmergencia((Integer) cboNivel.getSelectedItem());
			cDao.upsert(c);

			// finalizar la reserva con fecha/hora real
			rDao.finalizar(reservaSeleccionada, LocalDateTime.now());

			loadPendientes();
			JOptionPane.showMessageDialog(this, "Consulta confirmada y reserva finalizada.");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo confirmar la consulta.");
		}
	}

	private static String nullSafe(String s) {
		return s == null ? "" : s;
	}
}
