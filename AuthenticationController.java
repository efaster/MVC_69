// AuthenticationController.java - Controller for Authentication
public class AuthenticationController {
    private Registration registration;
    
    public AuthenticationController() {
        this.registration = new Registration();
    }
    
    public boolean authenticate(String studentId) {
        // Simple authentication - check if student exists and ID format is valid
        if (!isValidStudentId(studentId)) {
            System.out.println("Invalid student ID (must be 8 digits starting with 69)");
            return false;
        }
        
        Student student = registration.getStudent(studentId);
        if (student == null) {
            System.out.println("Student ID not found in system");
            return false;
        }
        
        if (!student.isAgeValid()) {
            System.out.println("Age requirement not met (must be at least 15 years old)");
            return false;
        }
        
        System.out.println("Login successful: " + student.getFullName());
        return true;
    }
    
    private boolean isValidStudentId(String studentId) {
        if (studentId == null || studentId.length() != 8) {
            return false;
        }
        
        try {
            long id = Long.parseLong(studentId);
            return studentId.startsWith("69");
        } catch (NumberFormatException e) {
            return false;
        }
    }
}