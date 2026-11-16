import java.util.Scanner;

/**
 * Main is the entry point for the MEDICARE Hospital Management System.
 *
 * Demonstrates:
 * - Simple, menu-driven CLI application
 * - Delegation of operations to specialized management classes
 * - Database connectivity check on startup
 * - Clean Scanner input handling and lifecycle management
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Display Welcome Banner
        System.out.println("=========================================");
        System.out.println("          MEDICARE HOSPITAL              ");
        System.out.println("           MANAGEMENT SYSTEM             ");
        System.out.println("=========================================");

        // Test database connectivity on startup
        System.out.print("[i] Checking database connection... ");
        if (DatabaseConnection.testConnection()) {
            System.out.println("[✓] Connected to MySQL Database (medicare_db)");
        } else {
            System.out.println("\n[!] WARNING: Could not connect to MySQL database!");
            System.out.println("    Please ensure:");
            System.out.println("    1. MySQL server is running (e.g., port 3306).");
            System.out.println("    2. 'medicare_db' database is created (run schema.sql).");
            System.out.println("    3. Credentials in db.properties are correct.");
            System.out.println("    (You may still proceed, but database operations will fail until connected.)");
        }

        // Initialize management modules
        PatientManagement patientMgmt = new PatientManagement(scanner);
        DoctorManagement doctorMgmt = new DoctorManagement(scanner);
        AppointmentManagement appointmentMgmt = new AppointmentManagement(scanner);
        BillingManagement billingMgmt = new BillingManagement(scanner);

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("=========================================");
            System.out.println("          MEDICARE HOSPITAL              ");
            System.out.println("           MANAGEMENT SYSTEM             ");
            System.out.println("=========================================");
            System.out.println("1. Patient Management");
            System.out.println("2. Doctor Management");
            System.out.println("3. Appointment Management");
            System.out.println("4. Billing Management");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    patientMgmt.patientMenu();
                    break;
                case "2":
                    doctorMgmt.doctorMenu();
                    break;
                case "3":
                    appointmentMgmt.appointmentMenu();
                    break;
                case "4":
                    billingMgmt.billingMenu();
                    break;
                case "5":
                    running = false;
                    System.out.println("\n=========================================");
                    System.out.println("  Thank you for using MEDICARE SYSTEM!   ");
                    System.out.println("               Stay Healthy!             ");
                    System.out.println("=========================================");
                    break;
                default:
                    System.out.println("[!] Invalid choice! Please enter a number between 1 and 5.");
            }
        }

        scanner.close();
    }
}
