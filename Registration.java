// Registration.java - Model for handling registration logic
import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class Registration {
    private static final String STUDENTS_CSV = "students.csv";
    private static final String SUBJECTS_CSV = "subjects.csv";
    private static final String REGISTRATIONS_CSV = "registrations.csv";
    
    private Map<String, Student> students;
    private Map<String, Subject> subjects;
    private Set<String> registeredCombinations; // studentId_subjectId
    
    public Registration() {
        students = new HashMap<>();
        subjects = new HashMap<>();
        registeredCombinations = new HashSet<>();
        loadData();
    }
    
    private void loadData() {
        loadStudents();
        loadSubjects();
        loadRegistrations();
    }
    
    private void loadStudents() {
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENTS_CSV))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    LocalDate birthDate = LocalDate.parse(data[4]);
                    Student student = new Student(data[0], data[1], data[2], data[3],
                                                birthDate, data[5], data[6]);
                    students.put(data[0], student);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
    }
    
    private void loadSubjects() {
        try (BufferedReader br = new BufferedReader(new FileReader(SUBJECTS_CSV))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    String prerequisite = data[4].isEmpty() ? null : data[4];
                    Subject subject = new Subject(data[0], data[1], Integer.parseInt(data[2]),
                                                data[3], prerequisite, Integer.parseInt(data[5]),
                                                Integer.parseInt(data[6]));
                    subjects.put(data[0], subject);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading subjects: " + e.getMessage());
        }
    }
    
    private void loadRegistrations() {
        try (BufferedReader br = new BufferedReader(new FileReader(REGISTRATIONS_CSV))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    registeredCombinations.add(data[0] + "_" + data[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading registrations: " + e.getMessage());
        }
    }
    
    // Model methods for registration validation with capacity check
    public boolean canRegisterWithCapacity(String studentId, String subjectId) {
        Student student = students.get(studentId);
        Subject subject = subjects.get(subjectId);
        
        if (student == null || subject == null) {
            return false;
        }
        
        // Check age requirement
        if (!student.isAgeValid()) {
            return false;
        }
        
        // Check if already registered
        if (registeredCombinations.contains(studentId + "_" + subjectId)) {
            return false;
        }
        
        // Check capacity
        return subject.canRegister();
    }
    
    // Model methods for registration validation without capacity check
    public boolean canRegisterWithoutCapacity(String studentId, String subjectId) {
        Student student = students.get(studentId);
        Subject subject = subjects.get(subjectId);
        
        if (student == null || subject == null) {
            return false;
        }
        
        // Check age requirement
        if (!student.isAgeValid()) {
            return false;
        }
        
        // Check if already registered
        if (registeredCombinations.contains(studentId + "_" + subjectId)) {
            return false;
        }
        
        // For subjects without capacity limit (maxCapacity == -1)
        return subject.getMaxCapacity() == -1;
    }
    
    public boolean registerStudent(String studentId, String subjectId) {
        if (!canRegisterWithCapacity(studentId, subjectId)) {
            return false;
        }
        
        Subject subject = subjects.get(subjectId);
        
        // Check prerequisite
        if (subject.hasPrerequisite()) {
            // For simplicity, assume prerequisite check passes
            // In real implementation, check if student has completed prerequisite
        }
        
        // Register student
        registeredCombinations.add(studentId + "_" + subjectId);
        subject.incrementEnrollment();
        
        // Save to CSV
        saveRegistration(studentId, subjectId);
        updateSubjectEnrollment(subjectId, subject.getCurrentEnrollment());
        
        return true;
    }
    
    private void saveRegistration(String studentId, String subjectId) {
        try (FileWriter fw = new FileWriter(REGISTRATIONS_CSV, true)) {
            fw.write(studentId + "," + subjectId + "\n");
        } catch (IOException e) {
            System.err.println("Error saving registration: " + e.getMessage());
        }
    }
    
    private void updateSubjectEnrollment(String subjectId, int newEnrollment) {
        // Update subjects.csv with new enrollment count
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(SUBJECTS_CSV))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("subjectId")) {
                        lines.add(line); // header
                    } else {
                        String[] data = line.split(",");
                        if (data[0].equals(subjectId)) {
                            data[6] = String.valueOf(newEnrollment);
                            lines.add(String.join(",", data));
                        } else {
                            lines.add(line);
                        }
                    }
                }
            }
            
            try (FileWriter fw = new FileWriter(SUBJECTS_CSV)) {
                for (String line : lines) {
                    fw.write(line + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error updating subject enrollment: " + e.getMessage());
        }
    }
    
    // Getters
    public Student getStudent(String studentId) {
        return students.get(studentId);
    }
    
    public Subject getSubject(String subjectId) {
        return subjects.get(subjectId);
    }
    
    public Collection<Subject> getAllSubjects() {
        return subjects.values();
    }
    
    public boolean isStudentRegistered(String studentId, String subjectId) {
        return registeredCombinations.contains(studentId + "_" + subjectId);
    }
}