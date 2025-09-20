// SubjectController.java - Controller for Subject operations
import java.util.Scanner;

public class SubjectController {
    private Registration registration;
    private Scanner scanner;
    
    public SubjectController() {
        this.registration = new Registration();
        this.scanner = new Scanner(System.in);
    }
    
    // View: Subject Details Page
    public void showSubjectDetails() {
        System.out.println("\n=== All Subject Details ===");
        int count = 1;
        for (Subject subject : registration.getAllSubjects()) {
            System.out.println(count + ". " + subject);
            if (subject.hasPrerequisite()) {
                System.out.println("   Prerequisite: " + subject.getPrerequisiteSubjectId());
            }
            System.out.println();
            count++;
        }
    }
    
    // View: Registration Page
    public void showRegistrationPage(String studentId) {
        System.out.println("\n=== Subject Registration ===");
        System.out.println("Available subjects for registration:");
        
        int count = 1;
        for (Subject subject : registration.getAllSubjects()) {
            if (!registration.isStudentRegistered(studentId, subject.getSubjectId())) {
                System.out.println(count + ". " + subject.getSubjectName() + 
                                 " (" + subject.getSubjectId() + ")");
                System.out.println("   " + subject.getCapacityInfo());
                
                if (subject.hasPrerequisite()) {
                    System.out.println("   Prerequisite: " + subject.getPrerequisiteSubjectId());
                }
                
                // Check if can register
                boolean canRegisterCap = registration.canRegisterWithCapacity(studentId, subject.getSubjectId());
                boolean canRegisterNoCap = registration.canRegisterWithoutCapacity(studentId, subject.getSubjectId());
                
                if (!canRegisterCap && subject.getMaxCapacity() != -1) {
                    System.out.println("   Status: FULL");
                } else if (canRegisterCap || canRegisterNoCap) {
                    System.out.println("   Status: Available");
                } else {
                    System.out.println("   Status: Cannot register");
                }
                System.out.println();
                count++;
            }
        }
        
        if (count == 1) {
            System.out.println("No subjects available for registration");
            return;
        }
        
        System.out.print("Enter subject ID to register (or 'exit' to go back): ");
        String subjectId = scanner.nextLine().trim();
        
        if (subjectId.equalsIgnoreCase("exit")) {
            return;
        }
        
        registerForSubject(studentId, subjectId);
    }
    
    private void registerForSubject(String studentId, String subjectId) {
        Subject subject = registration.getSubject(subjectId);
        
        if (subject == null) {
            System.out.println("Subject ID not found");
            return;
        }
        
        if (registration.isStudentRegistered(studentId, subjectId)) {
            System.out.println("You are already registered for this subject");
            return;
        }
        
        // Check business rules
        if (!registration.canRegisterWithCapacity(studentId, subjectId)) {
            if (subject.getMaxCapacity() != -1 && !subject.canRegister()) {
                System.out.println("Cannot register: Subject is full");
            } else {
                System.out.println("Cannot register: Requirements not met");
            }
            return;
        }
        
        // Attempt registration
        if (registration.registerStudent(studentId, subjectId)) {
            System.out.println("Registration successful: " + subject.getSubjectName());
            System.out.println("Returning to student profile...");
            
            // Show updated student profile
            StudentController studentController = new StudentController();
            studentController.showStudentProfile(studentId);
        } else {
            System.out.println("Registration failed");
        }
    }
}