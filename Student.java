// Student.java - Model
import java.time.LocalDate;
import java.time.Period;

public class Student {
    private String studentId;
    private String title;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String currentSchool;
    private String email;
    
    public Student(String studentId, String title, String firstName, String lastName, 
                   LocalDate birthDate, String currentSchool, String email) {
        this.studentId = studentId;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.currentSchool = currentSchool;
        this.email = email;
    }
    
    // Getters
    public String getStudentId() { return studentId; }
    public String getTitle() { return title; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getCurrentSchool() { return currentSchool; }
    public String getEmail() { return email; }
    
    // Business Rule: Student must be at least 15 years old
    public boolean isAgeValid() {
        return Period.between(birthDate, LocalDate.now()).getYears() >= 15;
    }
    
    public String getFullName() {
        return title + " " + firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return String.format("ID: %s, Name: %s, School: %s, Email: %s", 
                           studentId, getFullName(), currentSchool, email);
    }
}