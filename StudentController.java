// StudentController.java - Controller for Student operations
import java.util.Scanner;

public class StudentController {
    private Registration registration;
    private Scanner scanner;
    
    public StudentController() {
        this.registration = new Registration();
        this.scanner = new Scanner(System.in);
    }
    
    public void showStudentProfile(String studentId) {
        Student student = registration.getStudent(studentId);
        if (student == null) {
            System.out.println("Student not found");
            return;
        }
        
        System.out.println("\n=== Student Profile ===");
        System.out.println(student);
        System.out.println("Age: " + java.time.Period.between(student.getBirthDate(), 
                                                              java.time.LocalDate.now()).getYears() + " years");
        
        // Show registered subjects
        System.out.println("\n=== Registered Subjects ===");
        boolean hasRegistrations = false;
        for (Subject subject : registration.getAllSubjects()) {
            if (registration.isStudentRegistered(studentId, subject.getSubjectId())) {
                System.out.println("- " + subject.getSubjectName() + " (" + subject.getSubjectId() + ")");
                hasRegistrations = true;
            }
        }
        
        if (!hasRegistrations) {
            System.out.println("No subjects registered yet");
        }
    }
}