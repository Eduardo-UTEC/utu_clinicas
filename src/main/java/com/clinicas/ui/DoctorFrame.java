
package com.clinicas.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import com.clinicas.dao.*;
import com.clinicas.model.*;
import com.clinicas.util.Sanitizer;

public class DoctorFrame extends JInternalFrame {
	private JTextField txtFiltro;
	private JTable table;
	private final DoctorDao dao = new DoctorDaoJdbc();

	public DoctorFrame() {
		super("Doctores", true, true, true, true);
		initComponents();
		load();
	}

	private void initComponents() {
		setSize(900, 520);
		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.add(new JLabel("Buscar (Matrícula):"));
		txtFiltro = new JTextField(20);
		top.add(txtFiltro);
		JButton bBus = new JButton("Buscar");
		JButton bNew = new JButton("Nuevo");
		JButton bEd = new JButton("Editar");
		top.add(bBus);
		top.add(bNew);
		top.add(bEd);
		add(top, BorderLayout.NORTH);
		table = new JTable();
		add(new JScrollPane(table), BorderLayout.CENTER);
		txtFiltro.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					load();
			}
		});
		bBus.addActionListener(e -> load());
		bNew.addActionListener(e -> edit(null));
		bEd.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row >= 0) {
				String mat = (String) table.getValueAt(row, 0);
				try {
					edit(dao.findByMatricula(mat));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage());
				}
			}
		});
	}

	private void load() {
		try {
			var list = dao.search(txtFiltro.getText().trim());
			DefaultTableModel m = new DefaultTableModel(new Object[] { "Matrícula", "Nombre 1", "Apellido 1" }, 0) {
				public boolean isCellEditable(int r, int c) {
					return false;
				}
			};
			for (Doctor d : list)
				m.addRow(new Object[] { d.getMatricula(), d.getNom1(), d.getApe1() });
			table.setModel(m);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage());
		}
	}

	private void edit(Doctor d) {
		while (true) {
			JTextField nom1 = new JTextField(d != null ? d.getNom1() : "");
			JTextField nom2 = new JTextField(d != null ? d.getNom2() : "");
			JTextField ape1 = new JTextField(d != null ? d.getApe1() : "");
			JTextField ape2 = new JTextField(d != null ? d.getApe2() : "");
			JTextField mat = new JTextField(d != null ? d.getMatricula() : "");
			JPanel f = new JPanel(new GridLayout(0, 2, 8, 8));
			f.add(new JLabel("Nombre 1"));
			f.add(nom1);
			f.add(new JLabel("Nombre 2"));
			f.add(nom2);
			f.add(new JLabel("Apellido 1"));
			f.add(ape1);
			f.add(new JLabel("Apellido 2"));
			f.add(ape2);
			f.add(new JLabel("Matrícula"));
			f.add(mat);
			int ok = JOptionPane.showConfirmDialog(this, f, d == null ? "Nuevo Doctor" : "Editar Doctor",
					JOptionPane.OK_CANCEL_OPTION);
			if (ok != JOptionPane.OK_OPTION)
				return;
			try {
				if (d == null)
					d = new Doctor();
				d.setNom1(Sanitizer.cleanName(nom1.getText()));
				d.setNom2(Sanitizer.cleanName(nom2.getText()));
				d.setApe1(Sanitizer.cleanName(ape1.getText()));
				d.setApe2(Sanitizer.cleanName(ape2.getText()));
				d.setMatricula(Sanitizer.cleanId(mat.getText()));
				if (d.getIdDoctor() == null)
					dao.insert(d);
				else
					dao.update(d);
				load();
				return;
			} catch (RuntimeException re) {
				JOptionPane.showMessageDialog(this, re.getMessage());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error al guardar doctor.");
			}
		}
	}
}
