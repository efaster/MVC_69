// Subject.java - Model
public class Subject {
    private String subjectId;
    private String subjectName;
    private int credits;
    private String instructor;
    private String prerequisiteSubjectId;
    private int maxCapacity; // -1 if no maximum limit
    private int currentEnrollment;
    
    public Subject(String subjectId, String subjectName, int credits, String instructor,
                   String prerequisiteSubjectId, int maxCapacity, int currentEnrollment) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.credits = credits;
        this.instructor = instructor;
        this.prerequisiteSubjectId = prerequisiteSubjectId;
        this.maxCapacity = maxCapacity;
        this.currentEnrollment = currentEnrollment;
    }
    
    // Getters
    public String getSubjectId() { return subjectId; }
    public String getSubjectName() { return subjectName; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }
    public String getPrerequisiteSubjectId() { return prerequisiteSubjectId; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getCurrentEnrollment() { return currentEnrollment; }
    
    // Setters
    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
    
    // Business Rules
    public boolean canRegister() {
        if (maxCapacity == -1) {
            return true; // No maximum limit
        }
        return currentEnrollment < maxCapacity;
    }
    
    public boolean hasPrerequisite() {
        return prerequisiteSubjectId != null && !prerequisiteSubjectId.isEmpty();
    }
    
    public void incrementEnrollment() {
        if (canRegister()) {
            currentEnrollment++;
        }
    }
    
    public String getCapacityInfo() {
        if (maxCapacity == -1) {
            return String.format("Enrolled: %d students (Unlimited)", currentEnrollment);
        }
        return String.format("Enrolled: %d/%d students", currentEnrollment, maxCapacity);
    }
    
    @Override
    public String toString() {
        return String.format("Subject ID: %s\nSubject Name: %s\nCredits: %d\nInstructor: %s\n%s",
                           subjectId, subjectName, credits, instructor, getCapacityInfo());
    }
}