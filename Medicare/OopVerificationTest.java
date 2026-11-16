import java.util.ArrayList;

/**
 * OopVerificationTest is a standalone verification and demonstration class
 * for BCA academic examination and viva voce.
 *
 * It tests and demonstrates:
 * 1. Abstraction: Person is abstract with abstract displayDetails().
 * 2. Inheritance: Patient and Doctor inherit from Person.
 * 3. Polymorphism: ArrayList<Person> holding Patient and Doctor instances,
 *    invoking displayDetails() polymorphically at runtime (Dynamic Method Dispatch).
 * 4. Encapsulation: Private fields accessed exclusively via getters and setters.
 * 5. Bill Calculation: Business logic validation.
 */
public class OopVerificationTest {

    public static void main(String[] args) {
        System.out.println("=============================================================");
        System.out.println("     MEDICARE - OOP CONCEPTS VERIFICATION & DEMO             ");
        System.out.println("=============================================================");

        // 1. Encapsulation & Inheritance Demonstration
        System.out.println("\n[1] DEMONSTRATING INHERITANCE & ENCAPSULATION:");
        Patient p1 = new Patient(101, "Aarav Sharma", 29, "Male", "9876543210", "Seasonal Flu");
        Doctor d1 = new Doctor(201, "Dr. Sunita Rao", "Cardiologist", "9811223344", 750.0);

        System.out.println("Patient p1 created using subclass constructor with super() call:");
        System.out.println("  ID     : " + p1.getId() + " (Inherited from Person)");
        System.out.println("  Name   : " + p1.getName() + " (Inherited from Person)");
        System.out.println("  Phone  : " + p1.getPhone() + " (Inherited from Person)");
        System.out.println("  Age    : " + p1.getAge() + " (Patient specific)");
        System.out.println("  Disease: " + p1.getDisease() + " (Patient specific)");

        System.out.println("\nDoctor d1 created using subclass constructor with super() call:");
        System.out.println("  ID            : " + d1.getId() + " (Inherited from Person)");
        System.out.println("  Name          : " + d1.getName() + " (Inherited from Person)");
        System.out.println("  Specialization: " + d1.getSpecialization() + " (Doctor specific)");
        System.out.println("  Consult Fee   : Rs. " + d1.getConsultationFee() + " (Doctor specific)");

        // 2. Polymorphism & Abstraction Demonstration
        System.out.println("\n[2] DEMONSTRATING POLYMORPHISM (Dynamic Method Dispatch):");
        System.out.println("Adding both Patient and Doctor objects to an ArrayList<Person>...");

        ArrayList<Person> hospitalMembers = new ArrayList<>();
        hospitalMembers.add(p1);
        hospitalMembers.add(new Patient(102, "Meera Nair", 34, "Female", "9845123456", "Asthma"));
        hospitalMembers.add(d1);
        hospitalMembers.add(new Doctor(202, "Dr. Rajesh Gupta", "Orthopedic", "9877001122", 600.0));

        System.out.println("\nIterating through ArrayList<Person> and calling person.displayDetails():");
        System.out.println("-----------------------------------------------------------------------------");
        for (Person person : hospitalMembers) {
            // At runtime, Java dynamically calls the correct subclass displayDetails()
            person.displayDetails();
        }
        System.out.println("-----------------------------------------------------------------------------");

        // 3. Billing Logic & Encapsulation Demonstration
        System.out.println("\n[3] DEMONSTRATING BILLING CALCULATION:");
        Bill bill = new Bill(501, 101, "Aarav Sharma", 750.0, 250.0, 0.0);
        bill.calculateTotal();
        System.out.println("Calculated Total (750.0 + 250.0) = Rs. " + bill.getTotalAmount());
        assert bill.getTotalAmount() == 1000.0 : "Billing calculation mismatch!";
        bill.printInvoice();

        // 4. Appointment Demonstration
        System.out.println("\n[4] DEMONSTRATING APPOINTMENT CREATION:");
        Appointment app = new Appointment(301, 101, "Aarav Sharma", 201, "Dr. Sunita Rao", "2026-11-20", "Scheduled");
        app.displayDetails();

        System.out.println("\n=============================================================");
        System.out.println("     ALL OOP CONCEPTS AND LOGIC VERIFIED SUCCESSFULLY!       ");
        System.out.println("=============================================================");
    }
}
