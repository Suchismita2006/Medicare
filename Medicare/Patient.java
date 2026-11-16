/**
 * Patient class represents a patient in the MEDICARE hospital system.
 *
 * Demonstrates:
 * - Inheritance: Extends the abstract Person class.
 * - Encapsulation: Private fields with public getters and setters.
 * - Polymorphism: Overrides displayDetails() method from Person.
 */
public class Patient extends Person {

    // Encapsulation: Private fields specific to Patient
    private int age;
    private String gender;
    private String disease;

    /**
     * Default constructor
     */
    public Patient() {
        super();
    }

    /**
     * Parameterized constructor
     *
     * @param id      Patient ID
     * @param name    Patient Name
     * @param age     Patient Age
     * @param gender  Patient Gender (Male/Female/Other)
     * @param phone   Phone Number
     * @param disease Diagnosed disease or symptom
     */
    public Patient(int id, String name, int age, String gender, String phone, String disease) {
        super(id, name, phone); // Call to parent class constructor (Inheritance)
        this.age = age;
        this.gender = gender;
        this.disease = disease;
    }

    // Getters and Setters

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    /**
     * Polymorphic method implementation.
     * Overrides displayDetails() declared in Person abstract class.
     */
    @Override
    public void displayDetails() {
        System.out.printf("| %-4d | %-20s | %-4d | %-8s | %-12s | %-20s |\n",
                getId(), getName(), age, gender, getPhone(), disease);
    }

    @Override
    public String toString() {
        return "Patient [ID=" + getId() + ", Name=" + getName() + ", Age=" + age +
                ", Gender=" + gender + ", Phone=" + getPhone() + ", Disease=" + disease + "]";
    }
}
