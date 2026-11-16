/**
 * Doctor class represents a medical doctor in the MEDICARE hospital system.
 *
 * Demonstrates:
 * - Inheritance: Extends the abstract Person class.
 * - Encapsulation: Private fields with public getters and setters.
 * - Polymorphism: Overrides displayDetails() method from Person.
 */
public class Doctor extends Person {

    // Encapsulation: Private fields specific to Doctor
    private String specialization;
    private double consultationFee;

    /**
     * Default constructor
     */
    public Doctor() {
        super();
    }

    /**
     * Parameterized constructor
     *
     * @param id              Doctor ID
     * @param name            Doctor Name
     * @param specialization  Medical Specialization (e.g., Cardiologist, Physician)
     * @param phone           Contact Phone Number
     * @param consultationFee Standard consultation fee in currency
     */
    public Doctor(int id, String name, String specialization, String phone, double consultationFee) {
        super(id, name, phone); // Call to parent class constructor (Inheritance)
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    // Getters and Setters

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    /**
     * Polymorphic method implementation.
     * Overrides displayDetails() declared in Person abstract class.
     */
    @Override
    public void displayDetails() {
        System.out.printf("| %-4d | %-22s | %-22s | %-12s | Rs. %-9.2f |\n",
                getId(), getName(), specialization, getPhone(), consultationFee);
    }

    @Override
    public String toString() {
        return "Doctor [ID=" + getId() + ", Name=" + getName() +
                ", Specialization=" + specialization + ", Phone=" + getPhone() +
                ", Fee=" + consultationFee + "]";
    }
}
