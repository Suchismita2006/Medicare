import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * DoctorManagement handles all CRUD operations for Doctors.
 *
 * Demonstrates:
 * - JDBC PreparedStatement usage
 * - ArrayList for temporary in-memory data handling
 * - Polymorphism via doctor.displayDetails()
 * - Exception handling for database operations
 */
public class DoctorManagement {

    private Scanner scanner;

    public DoctorManagement(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Doctor Management Sub-Menu Loop
     */
    public void doctorMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("=========================================");
            System.out.println("            DOCTOR MANAGEMENT            ");
            System.out.println("=========================================");
            System.out.println("1. Add Doctor");
            System.out.println("2. View All Doctors");
            System.out.println("3. Search Doctor");
            System.out.println("4. Delete Doctor");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    addDoctor();
                    break;
                case "2":
                    viewAllDoctors();
                    break;
                case "3":
                    searchDoctor();
                    break;
                case "4":
                    deleteDoctor();
                    break;
                case "5":
                    back = true;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("[!] Invalid choice! Please enter a number between 1 and 5.");
            }
        }
    }

    /**
     * Adds a new doctor to the database.
     */
    public void addDoctor() {
        System.out.println("\n--- Add New Doctor ---");

        System.out.print("Enter Doctor Name: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("Name cannot be empty. Enter Doctor Name: ");
            name = scanner.nextLine().trim();
        }

        System.out.print("Enter Specialization (e.g. Cardiologist, General Physician): ");
        String specialization = scanner.nextLine().trim();
        if (specialization.isEmpty()) {
            specialization = "General Physician";
        }

        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine().trim();

        double fee = readDouble("Enter Consultation Fee (Rs.): ", 0.0, 100000.0);

        String sql = "INSERT INTO doctors (name, specialization, phone, consultation_fee) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, specialization);
            pstmt.setString(3, phone);
            pstmt.setDouble(4, fee);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("\n[✓] Doctor registered successfully with Doctor ID: " + generatedId);
                    } else {
                        System.out.println("\n[✓] Doctor registered successfully!");
                    }
                }
            } else {
                System.out.println("\n[!] Failed to register doctor.");
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error while adding doctor: " + e.getMessage());
        }
    }

    /**
     * Fetches all doctors and displays them using an ArrayList and polymorphic displayDetails().
     */
    public void viewAllDoctors() {
        System.out.println("\n--- All Registered Doctors ---");
        String sql = "SELECT * FROM doctors ORDER BY doctor_id ASC";

        // ArrayList for temporary data handling
        ArrayList<Doctor> doctorList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Doctor d = new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("phone"),
                        rs.getDouble("consultation_fee")
                );
                doctorList.add(d);
            }

            if (doctorList.isEmpty()) {
                System.out.println("No doctor records found in the database.");
                return;
            }

            printDoctorTableHeader();
            // Demonstrates Polymorphism: invoking displayDetails() on each Doctor object
            for (Doctor doctor : doctorList) {
                doctor.displayDetails();
            }
            printDoctorTableFooter();
            System.out.println("Total Doctors: " + doctorList.size());

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error while retrieving doctors: " + e.getMessage());
        }
    }

    /**
     * Searches for a doctor by ID and displays their details.
     */
    public void searchDoctor() {
        System.out.println("\n--- Search Doctor by ID ---");
        int doctorId = readInt("Enter Doctor ID to search: ", 1, Integer.MAX_VALUE);

        Doctor doctor = getDoctorById(doctorId);
        if (doctor != null) {
            System.out.println("\n[✓] Doctor Record Found:");
            printDoctorTableHeader();
            doctor.displayDetails();
            printDoctorTableFooter();
        } else {
            System.out.println("\n[!] No doctor found with ID: " + doctorId);
        }
    }

    /**
     * Deletes a doctor from the database.
     */
    public void deleteDoctor() {
        System.out.println("\n--- Delete Doctor ---");
        int doctorId = readInt("Enter Doctor ID to delete: ", 1, Integer.MAX_VALUE);

        Doctor doctor = getDoctorById(doctorId);
        if (doctor == null) {
            System.out.println("\n[!] No doctor found with ID: " + doctorId);
            return;
        }

        System.out.print("Are you sure you want to delete doctor '" + doctor.getName() + "' (ID: " + doctorId + ")? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        String sql = "DELETE FROM doctors WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("\n[✓] Doctor deleted successfully (along with associated appointments).");
            } else {
                System.out.println("\n[!] Failed to delete doctor.");
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error during doctor deletion: " + e.getMessage());
        }
    }

    /**
     * Helper to retrieve a single Doctor object from the database by ID.
     */
    public static Doctor getDoctorById(int doctorId) {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(
                            rs.getInt("doctor_id"),
                            rs.getString("name"),
                            rs.getString("specialization"),
                            rs.getString("phone"),
                            rs.getDouble("consultation_fee")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error finding doctor by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if a doctor exists in the database.
     */
    public static boolean doctorExists(int doctorId) {
        return getDoctorById(doctorId) != null;
    }

    // Table printing helpers

    private void printDoctorTableHeader() {
        System.out.println("+------+------------------------+------------------------+--------------+---------------+");
        System.out.println("| ID   | Name                   | Specialization         | Phone        | Consult Fee   |");
        System.out.println("+------+------------------------+------------------------+--------------+---------------+");
    }

    private void printDoctorTableFooter() {
        System.out.println("+------+------------------------+------------------------+--------------+---------------+");
    }

    /**
     * Safe Integer Input Reader
     */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("[!] Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input! Please enter a valid integer.");
            }
        }
    }

    /**
     * Safe Double Input Reader
     */
    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("[!] Please enter a value between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input! Please enter a valid decimal number.");
            }
        }
    }
}
