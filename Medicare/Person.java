/**
 * Abstract class Person represents a generic person in the hospital system.
 *
 * Demonstrates:
 * - Abstraction: Cannot be directly instantiated; defines abstract displayDetails().
 * - Encapsulation: Private member variables accessible only through public getters/setters.
 */
public abstract class Person {

    // Encapsulation: Private fields
    private int id;
    private String name;
    private String phone;

    /**
     * Default constructor
     */
    public Person() {
    }

    /**
     * Parameterized constructor
     *
     * @param id    Person ID
     * @param name  Full name of the person
     * @param phone Contact phone number
     */
    public Person(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    // Getters and Setters (Encapsulation)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Abstract method to display details.
     * Must be overridden by subclasses (Patient, Doctor) - Demonstrates Polymorphism.
     */
    public abstract void displayDetails();
}
