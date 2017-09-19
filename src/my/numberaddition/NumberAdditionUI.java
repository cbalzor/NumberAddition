 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.numberaddition;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.*;


import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.openxml4j.opc.OPCPackage;
/**
 *
 * @author Dr. Baladron
 */
public class NumberAdditionUI extends javax.swing.JFrame {
    
    private final String HOST = "localhost/testEco";
    private final String USER = "admin";
    private final String PASS = "admin";
    public static final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-mm-yyyy");
    
    
    private Connection conn;
    private ResultSet rs;
    private DbManager myDbManager;
    /**
     * Creates new form NumberAdditionUI
     */
    public NumberAdditionUI() {
        conn = null;
        rs = null;
        initComponents();
        //testConnection();
        myDbManager = new DbManager();
        myDbManager.connect(HOST, USER, PASS);
    }
    
    private int newReg(){
        int id = myDbManager.getNextId()+1;
        NumHistField.setText(String.valueOf(id));
        DataPackage d = myDbManager.createVoidReg(id);
        updateInfo(d);
        return id;
    }
    
    private void insertHeader() throws ParseException{
        int r=Integer.parseInt(IdField.getText());
        int n=Integer.parseInt(NumHistField.getText());
        String p=PacienteField.getText();
        
        String fechaIntermedia=FechaField.getText(); // Ojo
        //SimpleDateFormat sdf1 = new SimpleDateFormat("dd-mm-yyyy");
        java.util.Date date = sdf1.parse(fechaIntermedia);
        java.sql.Date f = new java.sql.Date(date.getTime()); 
        
        String pr=ProcedenciaField.getText();
        String med=MedicoField.getText();
        String mot=MotivoField.getText();
        String ep=EpisodioField.getText();

        Header h = new Header(myDbManager.TABLE_HEADER, r, n, p, f, pr, med, mot, ep);
        //h.write(myDbManager.conn);
        myDbManager.updateHeader(h);
    }
    
    private void saveReg(){
        try {
            insertHeader();
            
        } catch (ParseException ex) {
            Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    public void loadReg(int id){
        DataPackage data = myDbManager.retrieveReg(id);
        updateInfo(data);
    }
    
    private void updateInfo(DataPackage d){
        IdField.setText(String.valueOf(d.header.regId));
        NumHistField.setText(String.valueOf(d.header.numhist));
        PacienteField.setText(String.valueOf(d.header.paciente));
        FechaField.setText(sdf1.format((d.header.fecha)));
        ProcedenciaField.setText(d.header.procedencia);
        MedicoField.setText(d.header.medico);
        ProcedenciaField.setText(d.header.procedencia);
        MotivoField.setText(d.header.motivo);
        EpisodioField.setText(d.header.episodio);
    }

    private void insertReg(){
        try {
            float num1 = Float.parseFloat(jTextField1.getText());
            float num2 = Float.parseFloat(jTextField2.getText());
            float num3 = Float.parseFloat(jTextField3.getText());
            
            // the mysql insert statement
            String query = " insert into valores "
                    + " values (?, ?, ?)";
            
            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, Math.round(num1));
            preparedStmt.setInt(2, Math.round(num2));
            preparedStmt.setInt(3, Math.round(num3));
            // execute the preparedstatement
            preparedStmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateNumbers(int num1, int num2, int num3){
        jTextField1.setText(String.valueOf(num1));
        jTextField2.setText(String.valueOf(num2));
        jTextField3.setText(String.valueOf(num3));
    }
    
    private void loadReg(){
        if (rs==null){
            try {
                // Preparamos la consulta
                Statement s = conn.createStatement(); 
                rs = s.executeQuery("select numhist, paciente from valores");
                if (rs.next()){
                    updateNumbers(rs.getInt(1),rs.getInt(2),rs.getInt(3));
                }
            } catch (SQLException ex) {
                Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{
            try {
                if (rs.next()){
                    updateNumbers(rs.getInt(1),rs.getInt(2),rs.getInt(3));
                }
            } catch (SQLException ex) {
                Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void substituteText() throws IOException, InvalidFormatException{
        loadReg();
        XWPFDocument doc = new XWPFDocument(OPCPackage.open("Prueba.docx"));
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains("<CODE1>")) {
                        text = text.replace("<CODE1>", "--haystack--");
                        r.setText(text, 0);
                    }
                }
            }
        }
        doc.write(new FileOutputStream("output.docx"));
    }
    
    private void testConnection() {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("DRIVER OK");
        } catch (Exception ex) {
            System.out.println("DRIVER ERROR");
        }
        
        try {
            conn
                    = DriverManager.getConnection("jdbc:mysql://localhost/prueba1?"
                            + "user=admin&password=admin");
            System.out.println("Conection OK");
            // Do something with the Connection
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
    
    private void testRemoteDb(){
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("DRIVER OK");
        } catch (Exception ex) {
            System.out.println("DRIVER ERROR");
        }
        
        try {
            conn
                    = DriverManager.getConnection("jdbc:mysql://db4free.net/basededatoscb?"
                            + "user=datoscb&password=qwerty");
            System.out.println("Conection OK");
            // Do something with the Connection
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        NumHistLabel = new javax.swing.JLabel();
        NumHistField = new javax.swing.JTextField();
        PacienteField = new javax.swing.JTextField();
        PacienteLabel = new javax.swing.JLabel();
        FechaField = new javax.swing.JTextField();
        FechaLabel = new javax.swing.JLabel();
        ProcedenciaField = new javax.swing.JTextField();
        ProcedenciaLabel = new javax.swing.JLabel();
        MedicoField = new javax.swing.JTextField();
        MedicoLabel = new javax.swing.JLabel();
        MotivoField = new javax.swing.JTextField();
        MotivoLabel = new javax.swing.JLabel();
        EpisodioField = new javax.swing.JTextField();
        EpisodioLabel = new javax.swing.JLabel();
        ButtonUp = new javax.swing.JButton();
        ButtonDown = new javax.swing.JButton();
        ButtonNew = new javax.swing.JButton();
        ButtonBorrar = new javax.swing.JButton();
        ButtonGuardar = new javax.swing.JButton();
        IdField = new javax.swing.JTextField();
        IdLabel = new javax.swing.JLabel();
        LoadButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Panel");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Prueba Título"));
        jPanel1.setToolTipText("ToolTip");
        jPanel1.setName("Panel"); // NOI18N

        jLabel1.setText("Number 1");

        jLabel2.setText("Number 2");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Result");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setText("Clear");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Add");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setText("InsertReg");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("testRemoteDb");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton4.setText("LoadReg");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton3.setText("Exit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField3)
                                    .addComponent(jTextField2))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3)
                            .addComponent(jButton2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(126, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton4))
                .addContainerGap())
        );

        jTabbedPane2.addTab("tab1", jPanel1);
        jPanel1.getAccessibleContext().setAccessibleName("AccessibleNAme");

        NumHistLabel.setLabelFor(NumHistField);
        NumHistLabel.setText("Número de Historia");
        NumHistLabel.setName("NumHistLabel"); // NOI18N

        NumHistField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NumHistFieldActionPerformed(evt);
            }
        });

        PacienteField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PacienteFieldActionPerformed(evt);
            }
        });

        PacienteLabel.setLabelFor(PacienteField);
        PacienteLabel.setText("Paciente");
        PacienteLabel.setName("NumHistLabel"); // NOI18N

        FechaField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FechaFieldActionPerformed(evt);
            }
        });

        FechaLabel.setLabelFor(FechaField);
        FechaLabel.setText("Fecha");
        FechaLabel.setName("NumHistLabel"); // NOI18N

        ProcedenciaField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProcedenciaFieldActionPerformed(evt);
            }
        });

        ProcedenciaLabel.setLabelFor(ProcedenciaField);
        ProcedenciaLabel.setText("Procedencia");
        ProcedenciaLabel.setName("NumHistLabel"); // NOI18N

        MedicoField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MedicoFieldActionPerformed(evt);
            }
        });

        MedicoLabel.setLabelFor(MedicoField);
        MedicoLabel.setText("Médico");
        MedicoLabel.setName("NumHistLabel"); // NOI18N

        MotivoField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MotivoFieldActionPerformed(evt);
            }
        });

        MotivoLabel.setLabelFor(MotivoField);
        MotivoLabel.setText("Motivo de Petición");
        MotivoLabel.setName("NumHistLabel"); // NOI18N

        EpisodioField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EpisodioFieldActionPerformed(evt);
            }
        });

        EpisodioLabel.setLabelFor(EpisodioField);
        EpisodioLabel.setText("Episodio");
        EpisodioLabel.setName("NumHistLabel"); // NOI18N

        ButtonUp.setText("Up");
        ButtonUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonUpActionPerformed(evt);
            }
        });

        ButtonDown.setText("Down");

        ButtonNew.setText("New");
        ButtonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonNewActionPerformed(evt);
            }
        });

        ButtonBorrar.setText("Borrar");

        ButtonGuardar.setText("Guardar");
        ButtonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonGuardarActionPerformed(evt);
            }
        });

        IdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IdFieldActionPerformed(evt);
            }
        });

        IdLabel.setLabelFor(NumHistField);
        IdLabel.setText("Id");
        IdLabel.setName("NumHistLabel"); // NOI18N

        LoadButton.setText("Cargar");
        LoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(NumHistLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(NumHistField, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(46, 46, 46)
                                .addComponent(PacienteLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PacienteField, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(FechaLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(FechaField, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(ProcedenciaLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ProcedenciaField, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(MedicoLabel))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(IdLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(IdField))
                                            .addComponent(MotivoLabel))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(MotivoField, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(23, 23, 23)
                                        .addComponent(EpisodioLabel)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(MedicoField)
                                    .addComponent(EpisodioField))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ButtonDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ButtonUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ButtonNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ButtonGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ButtonBorrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LoadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NumHistLabel)
                    .addComponent(NumHistField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PacienteLabel)
                    .addComponent(PacienteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonUp)
                    .addComponent(ButtonBorrar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FechaLabel)
                    .addComponent(FechaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ProcedenciaLabel)
                    .addComponent(ProcedenciaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MedicoLabel)
                    .addComponent(MedicoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonDown)
                    .addComponent(ButtonGuardar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(MotivoLabel)
                    .addComponent(MotivoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EpisodioLabel)
                    .addComponent(EpisodioField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonNew)
                    .addComponent(LoadButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IdLabel)
                    .addComponent(IdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // First we define float variables.
        float num1, num2, result;
        // We have to parse the text to a type float.
        num1 = Float.parseFloat(jTextField1.getText());
        num2 = Float.parseFloat(jTextField2.getText());
        // Now we can perform the addition.
        result = num1+num2;
        // We will now pass the value of result to jTextField3.
        // At the same time, we are going to
        // change the value of result from a float to a string.
        jTextField3.setText(String.valueOf(result));
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        loadReg();
        try {
            substituteText();
        } catch (IOException ex) {
            Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        insertReg();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        testRemoteDb();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void NumHistFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NumHistFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumHistFieldActionPerformed

    private void PacienteFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PacienteFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PacienteFieldActionPerformed

    private void FechaFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FechaFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FechaFieldActionPerformed

    private void ProcedenciaFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProcedenciaFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ProcedenciaFieldActionPerformed

    private void MedicoFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MedicoFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MedicoFieldActionPerformed

    private void MotivoFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MotivoFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MotivoFieldActionPerformed

    private void EpisodioFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EpisodioFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EpisodioFieldActionPerformed

    private void ButtonUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonUpActionPerformed
        loadReg(1);
    }//GEN-LAST:event_ButtonUpActionPerformed

    private void ButtonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonGuardarActionPerformed
        try {
            insertHeader();
        } catch (ParseException ex) {
            Logger.getLogger(NumberAdditionUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ButtonGuardarActionPerformed

    private void ButtonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonNewActionPerformed
        newReg();
    }//GEN-LAST:event_ButtonNewActionPerformed

    private void IdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IdFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IdFieldActionPerformed

    private void LoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadButtonActionPerformed
        RegSelector rs = new RegSelector(this,false);
        rs.setVisible(true);
        ResultSet results = myDbManager.getAllRegTags();
        rs.setList(results);
        rs.setUI(this);
        //System.out.println("JABOR");
    }//GEN-LAST:event_LoadButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NumberAdditionUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NumberAdditionUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NumberAdditionUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NumberAdditionUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NumberAdditionUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonBorrar;
    private javax.swing.JButton ButtonDown;
    private javax.swing.JButton ButtonGuardar;
    private javax.swing.JButton ButtonNew;
    private javax.swing.JButton ButtonUp;
    private javax.swing.JTextField EpisodioField;
    private javax.swing.JLabel EpisodioLabel;
    private javax.swing.JTextField FechaField;
    private javax.swing.JLabel FechaLabel;
    private javax.swing.JTextField IdField;
    private javax.swing.JLabel IdLabel;
    private javax.swing.JButton LoadButton;
    private javax.swing.JTextField MedicoField;
    private javax.swing.JLabel MedicoLabel;
    private javax.swing.JTextField MotivoField;
    private javax.swing.JLabel MotivoLabel;
    private javax.swing.JTextField NumHistField;
    private javax.swing.JLabel NumHistLabel;
    private javax.swing.JTextField PacienteField;
    private javax.swing.JLabel PacienteLabel;
    private javax.swing.JTextField ProcedenciaField;
    private javax.swing.JLabel ProcedenciaLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}

class DataPackage{
    Header header;
            

}
