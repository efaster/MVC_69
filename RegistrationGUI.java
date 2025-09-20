// RegistrationGUI.java - Main GUI Application
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationGUI extends JFrame {
    private Registration registration;
    private String currentStudentId;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Components
    private JTextField studentIdField;
    private JTable availableSubjectsTable;
    private JTable subjectDetailsTable;
    private DefaultTableModel availableTableModel;
    private DefaultTableModel detailsTableModel;
    private JLabel studentInfoLabel;
    
    public RegistrationGUI() {
        registration = new Registration();
        initializeComponents();
        setupGUI();
    }
    
    private void loadStudentProfile() {
        Student student = registration.getStudent(currentStudentId);
        if (student == null) return;
        
        // Update student info label
        studentInfoLabel.setText("Welcome, " + student.getFullName() + " (" + currentStudentId + ")");
        
        // Find profile panel by iterating through components
        JPanel profilePanel = null;
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if ("profile".equals(panel.getName())) {
                    profilePanel = panel;
                    break;
                }
            }
        }
        
        if (profilePanel == null) {
            System.err.println("Profile panel not found!");
            return;
        }
        
        JPanel studentDetailsPanel = (JPanel) profilePanel.getClientProperty("studentDetailsPanel");
        JPanel registeredSubjectsPanel = (JPanel) profilePanel.getClientProperty("registeredSubjectsPanel");
        
        // Update student details
        if (studentDetailsPanel != null) {
            JLabel nameLabel = (JLabel) studentDetailsPanel.getClientProperty("nameLabel");
            JLabel idLabel = (JLabel) studentDetailsPanel.getClientProperty("idLabel");
            JLabel emailLabel = (JLabel) studentDetailsPanel.getClientProperty("emailLabel");
            JLabel schoolLabel = (JLabel) studentDetailsPanel.getClientProperty("schoolLabel");
            JLabel ageLabel = (JLabel) studentDetailsPanel.getClientProperty("ageLabel");
            
            if (nameLabel != null) nameLabel.setText("Full Name: " + student.getFullName());
            if (idLabel != null) idLabel.setText("Student ID: " + student.getStudentId());
            if (emailLabel != null) emailLabel.setText("Email: " + student.getEmail());
            if (schoolLabel != null) schoolLabel.setText("Current School: " + student.getCurrentSchool());
            
            // Calculate age
            int age = java.time.Period.between(student.getBirthDate(), java.time.LocalDate.now()).getYears();
            if (ageLabel != null) ageLabel.setText("Age: " + age + " years");
        } else {
            System.err.println("Student details panel not found!");
        }
        
        // Update registered subjects table
        if (registeredSubjectsPanel != null) {
            DefaultTableModel registeredTableModel = (DefaultTableModel) registeredSubjectsPanel.getClientProperty("tableModel");
            JLabel totalCreditsLabel = (JLabel) registeredSubjectsPanel.getClientProperty("creditsLabel");
            
            if (registeredTableModel != null) {
                registeredTableModel.setRowCount(0);
                int totalCredits = 0;
                
                for (Subject subject : registration.getAllSubjects()) {
                    if (registration.isStudentRegistered(currentStudentId, subject.getSubjectId())) {
                        Object[] row = {
                            subject.getSubjectId(),
                            subject.getSubjectName(),
                            subject.getCredits(),
                            subject.getInstructor()
                        };
                        registeredTableModel.addRow(row);
                        totalCredits += subject.getCredits();
                    }
                }
                
                if (totalCreditsLabel != null) {
                    totalCreditsLabel.setText("Total Credits: " + totalCredits + " credits");
                }
            } else {
                System.err.println("Registered table model not found!");
            }
        } else {
            System.err.println("Registered subjects panel not found!");
        }
    }
    
    private void showStudentProfile() {
        loadStudentProfile(); // Refresh data
        cardLayout.show(mainPanel, "profile");
    }
    private void initializeComponents() {
        setTitle("Course Registration System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create different panels
        createLoginPanel();
        createStudentProfilePanel();  // เพิ่มหน้าประวัตินักเรียน
        createRegistrationPanel();
        createSubjectDetailsPanel();
        
        add(mainPanel);
    }
    
    private void createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 248, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        JLabel titleLabel = new JLabel("Course Registration System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 102, 153));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 30, 20);
        loginPanel.add(titleLabel, gbc);
        
        // Student ID Label
        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(idLabel, gbc);
        
        // Student ID Field
        studentIdField = new JTextField(15);
        studentIdField.setFont(new Font("Arial", Font.PLAIN, 16));
        studentIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(studentIdField, gbc);
        
        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(51, 102, 153));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);
        
        // Enter key support
        studentIdField.addActionListener(e -> handleLogin());
        
        mainPanel.add(loginPanel, "login");
    }
    
    private void createStudentProfilePanel() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(Color.WHITE);
        
        // Top Panel with student info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(51, 102, 153));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        studentInfoLabel = new JLabel();
        studentInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        studentInfoLabel.setForeground(Color.WHITE);
        topPanel.add(studentInfoLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> handleLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        profilePanel.add(topPanel, BorderLayout.NORTH);
        
        // Main content panel with two sections
        JPanel mainContentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Left panel - Student Details
        JPanel studentDetailsPanel = new JPanel(new BorderLayout());
        studentDetailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(51, 102, 153), 2),
            "Student Information",
            0, 0, new Font("Arial", Font.BOLD, 16), new Color(51, 102, 153)
        ));
        
        JPanel studentInfoPanel = new JPanel();
        studentInfoPanel.setLayout(new BoxLayout(studentInfoPanel, BoxLayout.Y_AXIS));
        studentInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Student info labels (will be populated in loadStudentProfile)
        JLabel nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel idLabel = new JLabel();
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel emailLabel = new JLabel();
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel schoolLabel = new JLabel();
        schoolLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel ageLabel = new JLabel();
        ageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        studentInfoPanel.add(nameLabel);
        studentInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        studentInfoPanel.add(idLabel);
        studentInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        studentInfoPanel.add(emailLabel);
        studentInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        studentInfoPanel.add(schoolLabel);
        studentInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        studentInfoPanel.add(ageLabel);
        
        // Store references to labels for updating
        studentDetailsPanel.add(studentInfoPanel, BorderLayout.CENTER);
        studentDetailsPanel.putClientProperty("nameLabel", nameLabel);
        studentDetailsPanel.putClientProperty("idLabel", idLabel);
        studentDetailsPanel.putClientProperty("emailLabel", emailLabel);
        studentDetailsPanel.putClientProperty("schoolLabel", schoolLabel);
        studentDetailsPanel.putClientProperty("ageLabel", ageLabel);
        
        // Right panel - Registered Subjects
        JPanel registeredSubjectsPanel = new JPanel(new BorderLayout());
        registeredSubjectsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(40, 167, 69), 2),
            "Registered Subjects",
            0, 0, new Font("Arial", Font.BOLD, 16), new Color(40, 167, 69)
        ));
        
        // Table for registered subjects
        String[] regColumns = {"Subject ID", "Subject Name", "Credits", "Instructor"};
        DefaultTableModel registeredTableModel = new DefaultTableModel(regColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable registeredTable = new JTable(registeredTableModel);
        registeredTable.setFont(new Font("Arial", Font.PLAIN, 12));
        registeredTable.setRowHeight(25);
        registeredTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        registeredTable.getTableHeader().setBackground(new Color(230, 255, 230));
        
        JScrollPane regScrollPane = new JScrollPane(registeredTable);
        regScrollPane.setPreferredSize(new Dimension(400, 200));
        registeredSubjectsPanel.add(regScrollPane, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel totalCreditsLabel = new JLabel();
        totalCreditsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalCreditsLabel.setForeground(new Color(40, 167, 69));
        summaryPanel.add(totalCreditsLabel);
        registeredSubjectsPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Store references for updating
        registeredSubjectsPanel.putClientProperty("tableModel", registeredTableModel);
        registeredSubjectsPanel.putClientProperty("creditsLabel", totalCreditsLabel);
        
        mainContentPanel.add(studentDetailsPanel);
        mainContentPanel.add(registeredSubjectsPanel);
        profilePanel.add(mainContentPanel, BorderLayout.CENTER);
        
        // Bottom button panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton registerButton = new JButton("Register for New Subjects");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(51, 102, 153));
        registerButton.setForeground(Color.WHITE);
        registerButton.setPreferredSize(new Dimension(250, 40));
        registerButton.addActionListener(e -> showRegistrationPage());
        
        JButton viewAllSubjectsButton = new JButton("View All Subject Details");
        viewAllSubjectsButton.setFont(new Font("Arial", Font.BOLD, 16));
        viewAllSubjectsButton.setBackground(new Color(108, 117, 125));
        viewAllSubjectsButton.setForeground(Color.WHITE);
        viewAllSubjectsButton.setPreferredSize(new Dimension(250, 40));
        viewAllSubjectsButton.addActionListener(e -> showSubjectDetails());
        
        bottomPanel.add(registerButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        bottomPanel.add(viewAllSubjectsButton);
        
        profilePanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Store references to panels for later use
        profilePanel.setName("profile");
        profilePanel.putClientProperty("studentDetailsPanel", studentDetailsPanel);
        profilePanel.putClientProperty("registeredSubjectsPanel", registeredSubjectsPanel);
        
        mainPanel.add(profilePanel, "profile");
    }
    
    
    private void createRegistrationPanel() {
        JPanel regPanel = new JPanel(new BorderLayout());
        regPanel.setBackground(Color.WHITE);
        
        // Top Panel with student info and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(51, 102, 153));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel regTitleLabel = new JLabel("Course Registration");
        regTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        regTitleLabel.setForeground(Color.WHITE);
        topPanel.add(regTitleLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(51, 102, 153));
        
        JButton backToProfileButton = new JButton("Back to Profile");
        backToProfileButton.setBackground(Color.WHITE);
        backToProfileButton.setForeground(new Color(51, 102, 153));
        backToProfileButton.addActionListener(e -> showStudentProfile());
        
        JButton viewDetailsButton = new JButton("View All Subject Details");
        viewDetailsButton.setBackground(Color.WHITE);
        viewDetailsButton.setForeground(new Color(51, 102, 153));
        viewDetailsButton.addActionListener(e -> showSubjectDetails());
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> handleLogout());
        
        buttonPanel.add(backToProfileButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(logoutButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        regPanel.add(topPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Available Subjects for Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Table for available subjects
        String[] columnNames = {"Subject ID", "Subject Name", "Credits", "Instructor", 
                               "Prerequisite", "Capacity", "Status", "Action"};
        availableTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only action column is editable
            }
        };
        
        availableSubjectsTable = new JTable(availableTableModel);
        availableSubjectsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        availableSubjectsTable.setRowHeight(35);
        availableSubjectsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        availableSubjectsTable.getTableHeader().setBackground(new Color(230, 240, 255));
        
        // Add register buttons to table
        availableSubjectsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        availableSubjectsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(availableSubjectsTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        regPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(regPanel, "registration");
    }
    
    private void createSubjectDetailsPanel() {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        
        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(51, 102, 153));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("All Subject Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton backButton = new JButton("Back to Profile");
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(51, 102, 153));
        backButton.addActionListener(e -> showStudentProfile());
        topPanel.add(backButton, BorderLayout.EAST);
        
        detailsPanel.add(topPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Table for subject details
        String[] detailColumns = {"Subject ID", "Subject Name", "Credits", "Instructor", 
                                 "Prerequisite", "Max Capacity", "Current Enrollment", "Available Slots"};
        detailsTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        subjectDetailsTable = new JTable(detailsTableModel);
        subjectDetailsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        subjectDetailsTable.setRowHeight(30);
        subjectDetailsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        subjectDetailsTable.getTableHeader().setBackground(new Color(230, 240, 255));
        
        JScrollPane detailsScrollPane = new JScrollPane(subjectDetailsTable);
        detailsScrollPane.setPreferredSize(new Dimension(900, 500));
        contentPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        detailsPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(detailsPanel, "details");
    }
    
    private void handleLogin() {
        String studentId = studentIdField.getText().trim();
        
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (studentId.length() != 8 || !studentId.startsWith("69")) {
            JOptionPane.showMessageDialog(this, "Invalid Student ID format\n(Must be 8 digits starting with 69)", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Student student = registration.getStudent(studentId);
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student ID not found in system", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!student.isAgeValid()) {
            JOptionPane.showMessageDialog(this, "Age requirement not met (must be at least 15 years old)", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        currentStudentId = studentId;
        loadStudentProfile();  // โหลดข้อมูลนักเรียน
        cardLayout.show(mainPanel, "profile");  // ไปหน้าประวัตินักเรียนแทน
    }
    
    private void handleLogout() {
        currentStudentId = null;
        studentIdField.setText("");
        cardLayout.show(mainPanel, "login");
    }
    
    private void loadAvailableSubjects() {
        availableTableModel.setRowCount(0);
        
        for (Subject subject : registration.getAllSubjects()) {
            if (!registration.isStudentRegistered(currentStudentId, subject.getSubjectId())) {
                String prerequisite = subject.hasPrerequisite() ? subject.getPrerequisiteSubjectId() : "None";
                String capacity = subject.getMaxCapacity() == -1 ? "Unlimited" : 
                                subject.getCurrentEnrollment() + "/" + subject.getMaxCapacity();
                
                String status;
                boolean canRegister = registration.canRegisterWithCapacity(currentStudentId, subject.getSubjectId()) ||
                                    registration.canRegisterWithoutCapacity(currentStudentId, subject.getSubjectId());
                
                if (subject.getMaxCapacity() != -1 && subject.getCurrentEnrollment() >= subject.getMaxCapacity()) {
                    status = "FULL";
                } else if (canRegister) {
                    status = "Available";
                } else {
                    status = "Cannot Register";
                }
                
                Object[] row = {
                    subject.getSubjectId(),
                    subject.getSubjectName(),
                    subject.getCredits(),
                    subject.getInstructor(),
                    prerequisite,
                    capacity,
                    status,
                    "Register"
                };
                
                availableTableModel.addRow(row);
            }
        }
    }
    
    private void showSubjectDetails() {
        detailsTableModel.setRowCount(0);
        
        for (Subject subject : registration.getAllSubjects()) {
            String prerequisite = subject.hasPrerequisite() ? subject.getPrerequisiteSubjectId() : "None";
            String maxCap = subject.getMaxCapacity() == -1 ? "Unlimited" : String.valueOf(subject.getMaxCapacity());
            String availableSlots = subject.getMaxCapacity() == -1 ? "Unlimited" : 
                                  String.valueOf(subject.getMaxCapacity() - subject.getCurrentEnrollment());
            
            Object[] row = {
                subject.getSubjectId(),
                subject.getSubjectName(),
                subject.getCredits(),
                subject.getInstructor(),
                prerequisite,
                maxCap,
                subject.getCurrentEnrollment(),
                availableSlots
            };
            
            detailsTableModel.addRow(row);
        }
        
        cardLayout.show(mainPanel, "details");
    }
    
    private void showRegistrationPage() {
        loadAvailableSubjects(); // Refresh data
        cardLayout.show(mainPanel, "registration");
    }
    
    // Button Renderer for table
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            String status = (String) table.getValueAt(row, 6);
            
            if ("Available".equals(status)) {
                setText("Register");
                setBackground(new Color(40, 167, 69));
                setForeground(Color.WHITE);
                setEnabled(true);
            } else {
                setText("Cannot Register");
                setBackground(new Color(220, 220, 220));
                setForeground(Color.GRAY);
                setEnabled(false);
            }
            
            return this;
        }
    }
    
    // Button Editor for table
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            currentRow = row;
            String status = (String) table.getValueAt(row, 6);
            
            if ("Available".equals(status)) {
                label = "Register";
                button.setText(label);
                button.setBackground(new Color(40, 167, 69));
                button.setForeground(Color.WHITE);
                isPushed = true;
            } else {
                label = "Cannot Register";
                button.setText(label);
                button.setBackground(new Color(220, 220, 220));
                button.setForeground(Color.GRAY);
                isPushed = false;
            }
            
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String subjectId = (String) availableSubjectsTable.getValueAt(currentRow, 0);
                String subjectName = (String) availableSubjectsTable.getValueAt(currentRow, 1);
                
                if (registration.registerStudent(currentStudentId, subjectId)) {
                    JOptionPane.showMessageDialog(RegistrationGUI.this, 
                        "Successfully registered for: " + subjectName,
                        "Registration Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadAvailableSubjects(); // Refresh table
                    
                    // Show option to go back to profile or continue registering
                    int choice = JOptionPane.showConfirmDialog(RegistrationGUI.this,
                        "Registration successful!\nDo you want to continue registering for more subjects?",
                        "Continue Registration?",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (choice == JOptionPane.NO_OPTION) {
                        showStudentProfile(); // Go back to profile
                    }
                } else {
                    JOptionPane.showMessageDialog(RegistrationGUI.this, 
                        "Registration failed. Please check requirements.",
                        "Registration Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    private void setupGUI() {
        // Show login panel initially
        cardLayout.show(mainPanel, "login");
    }
    
}