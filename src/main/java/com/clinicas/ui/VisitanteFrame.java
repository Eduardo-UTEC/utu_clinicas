
package com.clinicas.ui;
import javax.swing.*; import javax.swing.table.DefaultTableModel; import java.awt.*;
import com.clinicas.dao.*; import com.clinicas.model.*; import com.clinicas.util.Sanitizer;
public class VisitanteFrame extends JInternalFrame {
  private JComboBox<Paciente> cboPac; private JTable tbl; private final PacienteDao pDao=new PacienteDaoJdbc(); private final VisitanteDao vDao=new VisitanteDaoJdbc();
  public VisitanteFrame(){ super("Visitantes por Paciente",true,true,true,true); initComponents(); loadPacientes(); load(); }
  private void initComponents(){
    setSize(820,520); JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT)); top.add(new JLabel("Paciente:")); cboPac=new JComboBox<>(); top.add(cboPac);
    JButton bAdd=new JButton("Agregar"); JButton bEd=new JButton("Editar"); JButton bDel=new JButton("Quitar vínculo");
    top.add(bAdd); top.add(bEd); top.add(bDel); add(top,BorderLayout.NORTH); tbl=new JTable(); add(new JScrollPane(tbl),BorderLayout.CENTER);
    cboPac.addActionListener(e->load()); bAdd.addActionListener(e->edit(null)); bEd.addActionListener(e->onEdit()); bDel.addActionListener(e->onDel());
  }
  private void loadPacientes(){ try{ for(Paciente p:pDao.search("")) cboPac.addItem(p);}catch(Exception ignored){} }
  private void load(){ Paciente sel=(Paciente)cboPac.getSelectedItem(); if(sel==null) return; try{ var list=vDao.findByPaciente(sel.getIdPaciente()); DefaultTableModel m=new DefaultTableModel(new Object[]{"_id","Nombre 1","Nombre 2","Apellido 1","Apellido 2"},0){ public boolean isCellEditable(int r,int c){return false;}}; for(Visitante v:list) m.addRow(new Object[]{v.getIdVisitante(), v.getNom1(), v.getNom2(), v.getApe1(), v.getApe2()}); tbl.setModel(m); tbl.removeColumn(tbl.getColumnModel().getColumn(0)); }catch(Exception ex){ JOptionPane.showMessageDialog(this,"No se pudieron cargar los visitantes."); } }
  private void onEdit(){ int row=tbl.getSelectedRow(); if(row<0){ JOptionPane.showMessageDialog(this,"Seleccione un visitante."); return; } int id=(Integer)((DefaultTableModel)tbl.getModel()).getValueAt(row,0); Visitante v=new Visitante(); v.setIdVisitante(id); edit(v); }
  private void onDel(){ int row=tbl.getSelectedRow(); if(row<0){ JOptionPane.showMessageDialog(this,"Seleccione un visitante."); return; } int id=(Integer)((DefaultTableModel)tbl.getModel()).getValueAt(row,0); Paciente sel=(Paciente)cboPac.getSelectedItem(); try{ vDao.unlink(sel.getIdPaciente(), id); load(); }catch(Exception ex){ JOptionPane.showMessageDialog(this,"No se pudo eliminar el vínculo."); } }
  private void edit(Visitante v){
    Paciente sel=(Paciente)cboPac.getSelectedItem(); if(sel==null) return;
    JTextField nom1=new JTextField(v!=null?v.getNom1():""); JTextField nom2=new JTextField(v!=null?v.getNom2():""); JTextField ape1=new JTextField(v!=null?v.getApe1():""); JTextField ape2=new JTextField(v!=null?v.getApe2():"");
    JPanel f=new JPanel(new GridLayout(0,2,8,8)); f.add(new JLabel("Nombre 1")); f.add(nom1); f.add(new JLabel("Nombre 2")); f.add(nom2); f.add(new JLabel("Apellido 1")); f.add(ape1); f.add(new JLabel("Apellido 2")); f.add(ape2);
    int ok=JOptionPane.showConfirmDialog(this,f,v==null?"Agregar Visitante":"Editar Visitante",JOptionPane.OK_CANCEL_OPTION); if(ok!=JOptionPane.OK_OPTION) return;
    try{
      if(v==null){ v=new Visitante(); v.setNom1(Sanitizer.cleanName(nom1.getText())); v.setNom2(Sanitizer.cleanName(nom2.getText())); v.setApe1(Sanitizer.cleanName(ape1.getText())); v.setApe2(Sanitizer.cleanName(ape2.getText())); vDao.insert(v); vDao.link(sel.getIdPaciente(), v.getIdVisitante()); }
      else { v.setNom1(Sanitizer.cleanName(nom1.getText())); v.setNom2(Sanitizer.cleanName(nom2.getText())); v.setApe1(Sanitizer.cleanName(ape1.getText())); v.setApe2(Sanitizer.cleanName(ape2.getText())); vDao.update(v); }
      load();
    }catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error al guardar visitante."); }
  }
}
