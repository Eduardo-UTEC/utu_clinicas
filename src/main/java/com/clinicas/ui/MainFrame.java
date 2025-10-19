
package com.clinicas.ui;
import javax.swing.*; import java.awt.*; import com.clinicas.email.EmailService;
public class MainFrame extends JFrame {
  private JDesktopPane desktop; private EmailService email;
  public MainFrame(EmailService email){ this.email=email; initComponents(); }
  private void initComponents(){
    setTitle("Clínicas"); setLayout(new BorderLayout());
    desktop=new JDesktopPane(); setJMenuBar(buildMenu()); add(desktop,BorderLayout.CENTER);
  }
  private JMenuBar buildMenu(){
    JMenuBar mb=new JMenuBar(); JMenu m=new JMenu(); m.setIcon(new HamburgerIcon(18,3,6)); m.setPreferredSize(new Dimension(52,34));
    JMenuItem miPac=new JMenuItem("Pacientes"); miPac.addActionListener(e->open(new PacienteFrame()));
    JMenuItem miDoc=new JMenuItem("Doctores"); miDoc.addActionListener(e->open(new DoctorFrame()));
    JMenuItem miTur=new JMenuItem("Turnos por Doctor"); miTur.addActionListener(e->open(new TurnoFrame()));
    JMenuItem miRes=new JMenuItem("Reservas"); miRes.addActionListener(e->open(new ReservaFrame(email)));
    JMenuItem miCon=new JMenuItem("Consultas"); miCon.addActionListener(e->open(new ConsultaFrame()));
    JMenuItem miVis=new JMenuItem("Visitantes por Paciente"); miVis.addActionListener(e->open(new VisitanteFrame()));
    JMenuItem miEsp=new JMenuItem("Especialidades por Doctor"); miEsp.addActionListener(e->open(new DoctorEspecialidadFrame()));
    m.add(miPac); m.add(miDoc); m.add(miTur); m.add(miRes); m.add(miCon); m.addSeparator(); m.add(miVis); m.add(miEsp); mb.add(m); return mb;
  }
  private void open(JInternalFrame f){ desktop.add(f); f.setVisible(true); }
  static class HamburgerIcon implements Icon{ private final int w,h,g; HamburgerIcon(int w,int h,int g){this.w=w;this.h=h;this.g=g;}
    public void paintIcon(Component c, java.awt.Graphics g2, int x, int y){ int H=getIconHeight(); int y1=y+(H-(h*3+g*2))/2; for(int i=0;i<3;i++){ g2.fillRect(x+8,y1+i*(h+g),w,h);} } public int getIconWidth(){return w+16;} public int getIconHeight(){return h*3+g*2+10;} }
}
