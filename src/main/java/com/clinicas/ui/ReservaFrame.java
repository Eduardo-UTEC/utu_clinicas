package com.clinicas.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.HashMap;
import java.util.Map;

import com.clinicas.dao.DoctorDao;
import com.clinicas.dao.DoctorDaoJdbc;
import com.clinicas.dao.DoctorTurnoDao;
import com.clinicas.dao.DoctorTurnoDaoJdbc;
import com.clinicas.dao.PacienteDao;
import com.clinicas.dao.PacienteDaoJdbc;
import com.clinicas.dao.ReservaDao;
import com.clinicas.dao.ReservaDaoJdbc;
import com.clinicas.dao.TurnoDao;
import com.clinicas.dao.TurnoDaoJdbc;

import com.clinicas.model.Doctor;
import com.clinicas.model.Paciente;
import com.clinicas.model.Reserva;
import com.clinicas.model.Turno;

import com.clinicas.email.EmailService;
import com.clinicas.util.ReminderScheduler;

// DateTimePicker (LGoodDatePicker)
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;

import java.util.Locale;

public class ReservaFrame extends JInternalFrame {

    // DAOs
    private final PacienteDao pDao = new PacienteDaoJdbc();
    private final DoctorDao   dDao = new DoctorDaoJdbc();
    private final TurnoDao    tDao = new TurnoDaoJdbc();
    private final DoctorTurnoDao dtDao = new DoctorTurnoDaoJdbc();
    private final ReservaDao  rDao = new ReservaDaoJdbc();

    // UI (form)
    private JComboBox<Paciente> cboPac;
    private JComboBox<Doctor>   cboDoc;
    private JComboBox<Turno>    cboTur;

    private DateTimePicker dtInicio; // << reemplaza spinners
    private JTextField txtMotivo, txtObs;
    private JButton btnCrear;

    // UI (list/gestión)
    private JTable tbl;
    private JComboBox<String> cboEstadoFiltro;
    private JButton btnCancelar;

    private final EmailService email;

    public ReservaFrame(EmailService emailService) {
        super("Reservas", true, true, true, true);
        this.email = emailService;
        initComponents();
        loadCombos();
        loadTable();
    }

    private void initComponents(){
        setSize(1040, 600);
        getContentPane().setLayout(new BorderLayout(10,10));

        // ====== Formulario ======
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "<html><b>Crear nueva reserva</b></html>",
                TitledBorder.LEFT, TitledBorder.TOP
        ));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.WEST;

        int r = 0;
        gc.gridx=0; gc.gridy=r; formPanel.add(new JLabel("Paciente"), gc);
        cboPac = new JComboBox<Paciente>();
        gc.gridx=1; gc.gridy=r; gc.gridwidth=3; gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;
        formPanel.add(cboPac, gc);
        gc.gridwidth=1; gc.fill=GridBagConstraints.NONE; gc.weightx=0;

        r++;
        gc.gridx=0; gc.gridy=r; formPanel.add(new JLabel("Doctor"), gc);
        cboDoc = new JComboBox<Doctor>();
        gc.gridx=1; gc.gridy=r; gc.gridwidth=3; gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;
        formPanel.add(cboDoc, gc);
        gc.gridwidth=1; gc.fill=GridBagConstraints.NONE; gc.weightx=0;

        r++;
        gc.gridx=0; gc.gridy=r; formPanel.add(new JLabel("Turno"), gc);
        cboTur = new JComboBox<Turno>();
        gc.gridx=1; gc.gridy=r; gc.gridwidth=3; gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;
        formPanel.add(cboTur, gc);
        gc.gridwidth=1; gc.fill=GridBagConstraints.NONE; gc.weightx=0;

        // Inicio (DateTimePicker con formato dd/MM/yyyy y reloj 24h)
        r++;
        gc.gridx=0; gc.gridy=r; formPanel.add(new JLabel("Inicio"), gc);
        DatePickerSettings dps = new DatePickerSettings(new Locale("es","UY"));
        dps.setFormatForDatesCommonEra("dd/MM/yyyy");
        TimePickerSettings tps = new TimePickerSettings(new Locale("es","UY"));
        tps.use24HourClockFormat();
        dtInicio = new DateTimePicker(dps, tps);
        gc.gridx=1; gc.gridy=r; gc.gridwidth=3; gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;
        formPanel.add(dtInicio, gc);
        gc.gridwidth=1; gc.fill=GridBagConstraints.NONE; gc.weightx=0;

        r++;
        txtMotivo = new JTextField(24);
        gc.gridx=0; gc.gridy=r; formPanel.add(new JLabel("Motivo"), gc);
        gc.gridx=1; gc.gridy=r; gc.gridwidth=3; gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;
        formPanel.add(txtMotivo, gc);
        gc.gridwidth=1; gc.fill=GridBagConstraints.NONE; gc.weightx=0;

        r++;
        txtObs = new JTextField(24);
        gc.gridx=0; gc.gridy=r; formPanel.add(new JLabel("Observaciones"), gc);
        gc.gridx=1; gc.gridy=r; gc.gridwidth=3; gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;
        formPanel.add(txtObs, gc);
        gc.gridwidth=1; gc.fill=GridBagConstraints.NONE; gc.weightx=0;

        r++;
        btnCrear = new JButton("Crear reserva");
        gc.gridx=1; gc.gridy=r; formPanel.add(btnCrear, gc);

        getContentPane().add(formPanel, BorderLayout.NORTH);

        // ====== Listado / Gestión ======
        JPanel listContainer = new JPanel(new BorderLayout(6,6));
        listContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "<html><b>Listado y gestión de reservas</b></html>",
                TitledBorder.LEFT, TitledBorder.TOP
        ));

        JPanel listToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        listToolbar.add(new JLabel("Estado"));
        cboEstadoFiltro = new JComboBox<String>(new String[]{"TODOS","PENDIENTE","FINALIZADA","CANCELADA"});
        listToolbar.add(cboEstadoFiltro);
        btnCancelar = new JButton("Cancelar reserva seleccionada");
        listToolbar.add(btnCancelar);
        listContainer.add(listToolbar, BorderLayout.NORTH);

        tbl = new JTable();
        JScrollPane scroll = new JScrollPane(tbl);
        listContainer.add(scroll, BorderLayout.CENTER);

        getContentPane().add(listContainer, BorderLayout.CENTER);

        // Listeners
        cboDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { reloadTurnosByDoctor(); }
        });
        btnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { onCreate(); }
        });
        cboEstadoFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { loadTable(); }
        });
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { onCancel(); }
        });
    }

    private void loadCombos(){
        try{
            cboPac.removeAllItems();
            for (Paciente p : pDao.search("")) {
                cboPac.addItem(p);
            }
            cboDoc.removeAllItems();
            for (Doctor d : dDao.search("")) {
                cboDoc.addItem(d);
            }
            reloadTurnosByDoctor();
        }catch(Exception ignored){}
    }

    private void reloadTurnosByDoctor(){
        try{
            cboTur.removeAllItems();
            Doctor d = (Doctor) cboDoc.getSelectedItem();
            if (d == null) return;
            for (Turno t : dtDao.findByDoctor(d.getIdDoctor())) {
                cboTur.addItem(t);
            }
        }catch(Exception ignored){}
    }

    private void onCreate(){
        try{
            Paciente p = (Paciente) cboPac.getSelectedItem();
            Doctor   d = (Doctor)   cboDoc.getSelectedItem();
            Turno    t = (Turno)    cboTur.getSelectedItem();
            if(p==null || d==null || t==null){
                JOptionPane.showMessageDialog(this,"Seleccione paciente, doctor y turno.");
                return;
            }

            LocalDateTime ini = dtInicio.getDateTimeStrict();
            if (ini == null) {
                JOptionPane.showMessageDialog(this,"Defina la fecha y hora de inicio.");
                return;
            }

            // Validar que ese doctor atiende ese turno
            boolean okPair = false;
            for (Turno tx : dtDao.findByDoctor(d.getIdDoctor())) {
                if (tx.getIdTurno() != null && tx.getIdTurno().equals(t.getIdTurno())) { okPair = true; break; }
            }
            if (!okPair) {
                JOptionPane.showMessageDialog(this,"El doctor seleccionado no atiende ese turno.");
                return;
            }

            Reserva r = new Reserva();
            r.setIdPaciente(p.getIdPaciente());
            r.setIdDoctor(d.getIdDoctor());
            r.setIdTurno(t.getIdTurno());
            r.setFechaInicio(ini);
            r.setFechaCreacion(LocalDateTime.now());
            r.setEstado("PENDIENTENTE".replace("ENTENTE","ENTE")); // = "PENDIENTE", truco para evitar typos accidentales :)
            r.setMotivo(txtMotivo.getText());
            r.setObservaciones(txtObs.getText());

            rDao.insert(r);
            ReminderScheduler.get().scheduleFor(r);
            loadTable();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"No se pudo crear la reserva.");
        }
    }

    private void loadTable(){
        try{
            String estado = (String) cboEstadoFiltro.getSelectedItem();
            java.util.List<Reserva> list = rDao.listUpcomingByEstado(estado);

            DefaultTableModel m = new DefaultTableModel(
                new Object[]{"ID","Paciente","Doctor","Turno","Inicio","Fin","Estado"}, 0){
                public boolean isCellEditable(int r,int c){ return false; }
            };

            Map<Integer, String> doctorNameById = new HashMap<Integer, String>();
            for (Doctor d2 : dDao.search("")) {
                String n1 = d2.getNom1() != null ? d2.getNom1() : "";
                String a1 = d2.getApe1() != null ? d2.getApe1() : "";
                String dn = (n1 + " " + a1).trim();
                doctorNameById.put(d2.getIdDoctor(), dn.isEmpty()? ("Doctor #" + d2.getIdDoctor()) : dn);
            }
            Map<Integer, String> turnoNameById = new HashMap<Integer, String>();
            for (Turno t2 : tDao.findAll()) {
                turnoNameById.put(t2.getIdTurno(), t2.getNombre());
            }

            for (Reserva r : list){
                Paciente p = pDao.findById(r.getIdPaciente());
                String nombrePac = p!=null
                        ? String.format("%s %s (%s)",
                        nullSafe(p.getNom1()), nullSafe(p.getApe1()), nullSafe(p.getCedula()))
                        : String.valueOf(r.getIdPaciente());

                String docName = doctorNameById.containsKey(r.getIdDoctor())
                        ? doctorNameById.get(r.getIdDoctor())
                        : String.valueOf(r.getIdDoctor());
                String turName = turnoNameById.containsKey(r.getIdTurno())
                        ? turnoNameById.get(r.getIdTurno())
                        : String.valueOf(r.getIdTurno());

                m.addRow(new Object[]{
                        r.getIdReserva(),
                        nombrePac,
                        docName,
                        turName,
                        r.getFechaInicio(),
                        r.getFechaFin(),   // null hasta finalizar
                        r.getEstado()
                });
            }
            tbl.setModel(m);
            // ocultar ID
            if (tbl.getColumnModel().getColumnCount() > 0) {
                tbl.removeColumn(tbl.getColumnModel().getColumn(0));
            }
            // aplicar formato dd/MM/yyyy HH:mm a Inicio(4) y Fin(5)
            if (tbl.getColumnModel().getColumnCount() >= 6) {
                tbl.getColumnModel().getColumn(4).setCellRenderer(UiFormats.dateTimeRenderer());
                tbl.getColumnModel().getColumn(5).setCellRenderer(UiFormats.dateTimeRenderer());
            }
        }catch(Exception ignored){}
    }

    private void onCancel(){
        int viewRow = tbl.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva del listado.");
            return;
        }
        int modelRow = tbl.convertRowIndexToModel(viewRow);
        Object idObj = tbl.getModel().getValueAt(modelRow, 0);   // ID (modelo)
        Object estadoObj = tbl.getModel().getValueAt(modelRow, 6); // Estado
        Object inicioObj = tbl.getModel().getValueAt(modelRow, 4); // Inicio

        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "No se pudo determinar el ID de la reserva.");
            return;
        }
        String estadoActual = (estadoObj != null) ? estadoObj.toString() : "";

        if ("FINALIZADA".equalsIgnoreCase(estadoActual)) {
            JOptionPane.showMessageDialog(this, "No se puede cancelar una reserva FINALIZADA.");
            return;
        }
        if ("CANCELADA".equalsIgnoreCase(estadoActual)) {
            JOptionPane.showMessageDialog(this, "La reserva ya está CANCELADA.");
            return;
        }

        // No permitir cancelar si ya inició
        try {
            LocalDateTime inicio = null;
            if (inicioObj instanceof LocalDateTime) inicio = (LocalDateTime) inicioObj;
            else if (inicioObj != null) inicio = LocalDateTime.parse(inicioObj.toString().replace(' ', 'T'));
            if (inicio != null && !LocalDateTime.now().isBefore(inicio)) {
                JOptionPane.showMessageDialog(this, "No se puede cancelar una reserva que ya inició.");
                return;
            }
        } catch (Exception ignored){}

        int id = (idObj instanceof Integer) ? ((Integer) idObj) : Integer.parseInt(idObj.toString());

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Cancelar la reserva seleccionada?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp != JOptionPane.YES_OPTION) return;

        try {
            rDao.updateEstado(id, "CANCELADA");
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo cancelar la reserva.");
        }
    }

    private static String nullSafe(String s){ return s==null? "": s; }

    // Helpers opcionales si necesitás truncar minutos, etc.
    @SuppressWarnings("unused")
    private static LocalDateTime of(LocalDate d, LocalTime t){
        return (d==null || t==null)? null : LocalDateTime.of(d, t.withSecond(0).withNano(0));
    }
}
