
package com.clinicas.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import com.clinicas.dao.*;
import com.clinicas.model.*;

public class DoctorEspecialidadFrame extends JInternalFrame {
	private JComboBox<Doctor> cboDoctor;
	private JTable tblAsign, tblDisp;
	private final DoctorDao doctorDao = new DoctorDaoJdbc();
	private final EspecializacionDao espDao = new EspecializacionDaoJdbc();
	private final DoctorEspecializacionDao docEspDao = new DoctorEspecializacionDaoJdbc();

	public DoctorEspecialidadFrame() {
		super("Especialidades por Doctor", true, true, true, true);
		initComponents();
		loadDoctors();
		load();
	}

	private void initComponents() {
		setSize(980, 540);
		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.add(new JLabel("Doctor:"));
		cboDoctor = new JComboBox<>();
		JButton bRef = new JButton("Refrescar");
		top.add(cboDoctor);
		top.add(bRef);
		add(top, BorderLayout.NORTH);
		JPanel left = new JPanel(new BorderLayout());
		left.add(new JLabel("Asignadas"), BorderLayout.NORTH);
		tblAsign = new JTable();
		left.add(new JScrollPane(tblAsign), BorderLayout.CENTER);
		JPanel right = new JPanel(new BorderLayout());
		right.add(new JLabel("Disponibles"), BorderLayout.NORTH);
		tblDisp = new JTable();
		right.add(new JScrollPane(tblDisp), BorderLayout.CENTER);
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		sp.setDividerLocation(470);
		add(sp, BorderLayout.CENTER);
		JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton bAdd = new JButton("Asignar →");
		JButton bRem = new JButton("← Quitar");
		JButton bNueva = new JButton("Nueva");
		JButton bRen = new JButton("Renombrar");
		JButton bDel = new JButton("Eliminar");
		bot.add(bAdd);
		bot.add(bRem);
		bot.add(bNueva);
		bot.add(bRen);
		bot.add(bDel);
		add(bot, BorderLayout.SOUTH);
		cboDoctor.addActionListener(e -> load());
		bRef.addActionListener(e -> load());
		bAdd.addActionListener(e -> onAdd());
		bRem.addActionListener(e -> onRem());
		bNueva.addActionListener(e -> onNueva());
		bRen.addActionListener(e -> onRen());
		bDel.addActionListener(e -> onDel());
	}

	private void loadDoctors() {
		try {
			for (Doctor d : doctorDao.search(""))
				cboDoctor.addItem(d);
		} catch (Exception ignored) {
		}
	}

	private void load() {
		try {
			Doctor sel = (Doctor) cboDoctor.getSelectedItem();
			if (sel == null)
				return;
			var asign = docEspDao.findByDoctor(sel.getIdDoctor());
			DefaultTableModel mA = new DefaultTableModel(new Object[] { "_id", "Nombre" }, 0) {
				public boolean isCellEditable(int r, int c) {
					return false;
				}
			};
			for (Especializacion e : asign)
				mA.addRow(new Object[] { e.getIdEspecializacion(), e.getNombre() });
			tblAsign.setModel(mA);
			tblAsign.removeColumn(tblAsign.getColumnModel().getColumn(0));
			var map = new LinkedHashMap<Integer, Especializacion>();
			for (Especializacion e : espDao.findAll())
				map.put(e.getIdEspecializacion(), e);
			for (Especializacion e : asign)
				map.remove(e.getIdEspecializacion());
			DefaultTableModel mD = new DefaultTableModel(new Object[] { "_id", "Nombre" }, 0) {
				public boolean isCellEditable(int r, int c) {
					return false;
				}
			};
			for (Especializacion e : map.values())
				mD.addRow(new Object[] { e.getIdEspecializacion(), e.getNombre() });
			tblDisp.setModel(mD);
			tblDisp.removeColumn(tblDisp.getColumnModel().getColumn(0));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudieron cargar las especialidades.");
		}
	}

	private void onAdd() {
		int row = tblDisp.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Seleccione una especialidad.");
			return;
		}
		int id = (Integer) ((DefaultTableModel) tblDisp.getModel()).getValueAt(row, 0);
		Doctor sel = (Doctor) cboDoctor.getSelectedItem();
		try {
			docEspDao.add(sel.getIdDoctor(), id);
			load();
		} catch (RuntimeException re) {
			JOptionPane.showMessageDialog(this, re.getMessage());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo asignar.");
		}
	}

	private void onRem() {
		int row = tblAsign.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Seleccione una especialidad.");
			return;
		}
		int id = (Integer) ((DefaultTableModel) tblAsign.getModel()).getValueAt(row, 0);
		Doctor sel = (Doctor) cboDoctor.getSelectedItem();
		try {
			docEspDao.remove(sel.getIdDoctor(), id);
			load();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo quitar.");
		}
	}

	private void onNueva() {
		String nombre = JOptionPane.showInputDialog(this, "Nombre de la especialidad:");
		if (nombre == null || nombre.isBlank())
			return;
		try {
			Especializacion e = new Especializacion();
			e.setNombre(nombre.trim());
			espDao.insert(e);
			load();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo crear.");
		}
	}

	private void onRen() {
		Integer id = selected(tblAsign);
		if (id == null)
			id = selected(tblDisp);
		if (id == null) {
			JOptionPane.showMessageDialog(this, "Seleccione una especialidad.");
			return;
		}
		try {
			Especializacion e = espDao.findById(id);
			if (e == null)
				return;
			String nuevo = JOptionPane.showInputDialog(this, "Nuevo nombre:", e.getNombre());
			if (nuevo == null || nuevo.isBlank())
				return;
			e.setNombre(nuevo.trim());
			espDao.update(e);
			load();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo renombrar.");
		}
	}

	private void onDel() {
		Integer id = selected(tblDisp);
		if (id == null)
			id = selected(tblAsign);
		if (id == null) {
			JOptionPane.showMessageDialog(this, "Seleccione una especialidad.");
			return;
		}
		int ok = JOptionPane.showConfirmDialog(this, "¿Eliminar del catálogo?", "Confirmar", JOptionPane.YES_NO_OPTION);
		if (ok != JOptionPane.YES_OPTION)
			return;
		try {
			espDao.delete(id);
			load();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
		}
	}

	private Integer selected(JTable t) {
		int r = t.getSelectedRow();
		if (r < 0)
			return null;
		return (Integer) ((DefaultTableModel) t.getModel()).getValueAt(r, 0);
	}
}
