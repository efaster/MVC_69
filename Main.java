// Main.java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthenticationController authController = new AuthenticationController();
        
        System.out.println("=== Early Registration System ===");
        System.out.println("Please login");
        System.out.print("Student ID (8 digits): ");
        String studentId = scanner.nextLine();
        
        if (authController.authenticate(studentId)) {
            StudentController studentController = new StudentController();
            SubjectController subjectController = new SubjectController();
            
            while (true) {
                System.out.println("\n=== Main Menu ===");
                System.out.println("1. View Subject Details");
                System.out.println("2. Register for Subjects");
                System.out.println("3. Exit");
                System.out.print("Choose option: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                switch (choice) {
                    case 1:
                        subjectController.showSubjectDetails();
                        break;
                    case 2:
                        subjectController.showRegistrationPage(studentId);
                        break;
                    case 3:
                        System.out.println("Logged out successfully");
                        return;
                    default:
                        System.out.println("Please choose a valid option");
                }
            }
        } else {
            System.out.println("Login failed");
        }
        
        scanner.close();
    }
}