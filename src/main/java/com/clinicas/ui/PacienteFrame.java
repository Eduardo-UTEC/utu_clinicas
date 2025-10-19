
package com.clinicas.ui;
import javax.swing.*; import javax.swing.table.*; import java.awt.*; import java.awt.event.*;
import com.clinicas.dao.*; import com.clinicas.model.*; import com.clinicas.util.*;
public class PacienteFrame extends JInternalFrame {
  private JTextField txtFiltro; private JTable table; private final PacienteDao dao=new PacienteDaoJdbc();
  public PacienteFrame(){ super("Pacientes",true,true,true,true); initComponents(); load(); }
  private void initComponents(){
    setSize(900,520); JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT));
    top.add(new JLabel("Buscar (Cédula):")); txtFiltro=new JTextField(20); top.add(txtFiltro);
    JButton bBus=new JButton("Buscar"); JButton bNew=new JButton("Nuevo"); JButton bEd=new JButton("Editar");
    top.add(bBus); top.add(bNew); top.add(bEd); add(top,BorderLayout.NORTH);
    table=new JTable(); add(new JScrollPane(table),BorderLayout.CENTER);
    txtFiltro.addKeyListener(new KeyAdapter(){ public void keyPressed(KeyEvent e){ if(e.getKeyCode()==KeyEvent.VK_ENTER) load(); }});
    bBus.addActionListener(e->load()); bNew.addActionListener(e->edit(null));
    bEd.addActionListener(e->{ int row=table.getSelectedRow(); if(row>=0){ String ced=(String)table.getValueAt(row,0); try{ edit(dao.findByCedula(ced)); }catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage()); } } });
  }
  private void load(){
    try{
      var list=dao.search(txtFiltro.getText().trim());
      DefaultTableModel m=new DefaultTableModel(new Object[]{"Cédula","Nombre 1","Apellido 1","Email"},0){ public boolean isCellEditable(int r,int c){return false;}};
      for(Paciente p:list) m.addRow(new Object[]{p.getCedula(),p.getNom1(),p.getApe1(),p.getEmail()});
      table.setModel(m);
    }catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage()); }
  }
  private void edit(Paciente p){
    while(true){
      JTextField nom1=new JTextField(p!=null?p.getNom1():""); JTextField nom2=new JTextField(p!=null?p.getNom2():"");
      JTextField ape1=new JTextField(p!=null?p.getApe1():""); JTextField ape2=new JTextField(p!=null?p.getApe2():"");
      JTextField ced=new JTextField(p!=null?p.getCedula():""); JTextField email=new JTextField(p!=null?p.getEmail():"");
      JPanel f=new JPanel(new GridLayout(0,2,8,8)); f.add(new JLabel("Nombre 1")); f.add(nom1); f.add(new JLabel("Nombre 2")); f.add(nom2);
      f.add(new JLabel("Apellido 1")); f.add(ape1); f.add(new JLabel("Apellido 2")); f.add(ape2);
      f.add(new JLabel("Cédula")); f.add(ced); f.add(new JLabel("Email")); f.add(email);
      int ok=JOptionPane.showConfirmDialog(this,f,p==null?"Nuevo Paciente":"Editar Paciente",JOptionPane.OK_CANCEL_OPTION); if(ok!=JOptionPane.OK_OPTION) return;
      if(ced.getText().trim().isEmpty()){ JOptionPane.showMessageDialog(this,"La Cédula es obligatoria."); continue; }
      if(!email.getText().isBlank() && !Validators.isValidEmail(email.getText())){ JOptionPane.showMessageDialog(this,"Email inválido."); continue; }
      try{
        if(p==null) p=new Paciente();
        p.setNom1(Sanitizer.cleanName(nom1.getText())); p.setNom2(Sanitizer.cleanName(nom2.getText())); p.setApe1(Sanitizer.cleanName(ape1.getText())); p.setApe2(Sanitizer.cleanName(ape2.getText()));
        p.setCedula(Sanitizer.cleanId(ced.getText())); p.setEmail(Sanitizer.cleanEmail(email.getText()));
        if(p.getIdPaciente()==null) dao.insert(p); else dao.update(p); load(); return;
      }catch(RuntimeException re){ JOptionPane.showMessageDialog(this, re.getMessage()); } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Ocurrió un error al guardar el paciente."); }
    }
  }
}
