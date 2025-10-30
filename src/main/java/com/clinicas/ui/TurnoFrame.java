package com.clinicas.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.clinicas.dao.DoctorDao;
import com.clinicas.dao.DoctorDaoJdbc;
import com.clinicas.dao.DoctorTurnoDao;
import com.clinicas.dao.DoctorTurnoDaoJdbc;
import com.clinicas.dao.TurnoDao;
import com.clinicas.dao.TurnoDaoJdbc;
import com.clinicas.model.Doctor;
import com.clinicas.model.Turno;

public class TurnoFrame extends JInternalFrame {

	private final DoctorDao docDao = new DoctorDaoJdbc();
	private final TurnoDao turnoDao = new TurnoDaoJdbc();
	private final DoctorTurnoDao dtDao = new DoctorTurnoDaoJdbc();

	private JComboBox<Doctor> cboDoctor;
	private JTable tbl;

	// Lista que mapea la fila visible con su Turno real
	private java.util.List<Turno> currentTurnos = new ArrayList<Turno>();

	public TurnoFrame() {
		super("Turnos por Doctor (N:M)", true, true, true, true);
		initComponents();
		loadDoctors();
	}

	private void initComponents() {
		setSize(520, 360);

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.add(new JLabel("Doctor:"));
		cboDoctor = new JComboBox<Doctor>();
		top.add(cboDoctor);

		JButton bAdd = new JButton("Agregar turno");
		JButton bDel = new JButton("Quitar turno");
		top.add(bAdd);
		top.add(bDel);

		add(top, BorderLayout.NORTH);
		tbl = new JTable();
		add(new JScrollPane(tbl), BorderLayout.CENTER);

		cboDoctor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});

		bAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAdd();
			}
		});

		bDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onDel();
			}
		});
	}

	private void loadDoctors() {
		try {
			cboDoctor.removeAllItems();
			for (Doctor d : docDao.search("")) {
				cboDoctor.addItem(d);
			}
			if (cboDoctor.getItemCount() > 0) {
				cboDoctor.setSelectedIndex(0);
				load();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudieron cargar los doctores.");
		}
	}

	private void load() {
		Doctor d = (Doctor) cboDoctor.getSelectedItem();
		if (d == null)
			return;
		try {
			currentTurnos = dtDao.findByDoctor(d.getIdDoctor());

			DefaultTableModel m = new DefaultTableModel(new Object[] { "Turno" }, 0) {
				public boolean isCellEditable(int r, int c) {
					return false;
				}

				public Class<?> getColumnClass(int columnIndex) {
					return String.class;
				}
			};
			for (Turno t : currentTurnos) {
				m.addRow(new Object[] { t.getNombre() });
			}
			tbl.setModel(m);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudieron cargar los turnos del doctor.");
		}
	}

	private void onAdd() {
		Doctor d = (Doctor) cboDoctor.getSelectedItem();
		if (d == null) {
			JOptionPane.showMessageDialog(this, "Seleccione un doctor.");
			return;
		}
		try {
			java.util.List<Turno> todos = new ArrayList<Turno>(turnoDao.findAll());

			java.util.List<String> predef = Arrays.asList("Matutino", "Diurno", "Nocturno");
			java.util.List<String> existentes = new ArrayList<String>();
			for (Turno t : todos) {
				if (t != null && t.getNombre() != null && !t.getNombre().trim().isEmpty()) {
					existentes.add(t.getNombre().trim());
				}
			}
			for (String p : predef) {
				boolean ya = false;
				for (String e : existentes) {
					if (e.equalsIgnoreCase(p)) {
						ya = true;
						break;
					}
				}
				if (!ya)
					existentes.add(p);
			}
			Collections.sort(existentes, new Comparator<String>() {
				public int compare(String a, String b) {
					return a.compareToIgnoreCase(b);
				}
			});
			existentes.add("Otros…");

			String sel = (String) JOptionPane.showInputDialog(this, "Seleccione un turno", "Agregar turno",
					JOptionPane.PLAIN_MESSAGE, null, existentes.toArray(new String[0]), existentes.get(0));
			if (sel == null)
				return;

			String elegido = sel.trim();
			if ("Otros…".equalsIgnoreCase(elegido)) {
				String nuevo = pedirNuevoTurnoNombre();
				if (nuevo == null)
					return;
				elegido = nuevo.trim();
			}

			Turno turno = buscarPorNombreIgnoreCase(todos, elegido);
			if (turno == null) {
				turno = new Turno();
				turno.setNombre(elegido);
				turnoDao.insert(turno);
			}

			dtDao.add(d.getIdDoctor(), turno.getIdTurno());
			load();

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo agregar el turno.");
		}
	}

	private String pedirNuevoTurnoNombre() {
		while (true) {
			String nombre = JOptionPane.showInputDialog(this, "Nombre del turno:");
			if (nombre == null)
				return null;
			nombre = nombre.trim();
			if (nombre.isEmpty()) {
				JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
				continue;
			}
			if (nombre.length() > 30) {
				JOptionPane.showMessageDialog(this, "Máximo 30 caracteres.");
				continue;
			}
			return nombre;
		}
	}

	private Turno buscarPorNombreIgnoreCase(java.util.List<Turno> lista, String nombre) {
		for (Turno t : lista) {
			if (t != null && t.getNombre() != null && t.getNombre().equalsIgnoreCase(nombre)) {
				return t;
			}
		}
		return null;
	}

	private void onDel() {
		int row = tbl.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Seleccione un turno.");
			return;
		}
		Doctor d = (Doctor) cboDoctor.getSelectedItem();
		if (d == null) {
			JOptionPane.showMessageDialog(this, "Seleccione un doctor.");
			return;
		}
		if (row >= currentTurnos.size()) {
			JOptionPane.showMessageDialog(this, "Selección inválida.");
			return;
		}
		int idTurno = currentTurnos.get(row).getIdTurno();
		try {
			dtDao.remove(d.getIdDoctor(), idTurno);
			load();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo quitar el turno.");
		}
	}
}
