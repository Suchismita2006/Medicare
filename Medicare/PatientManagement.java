import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * PatientManagement handles all CRUD operations for Patients.
 *
 * Demonstrates:
 * - JDBC PreparedStatement usage
 * - ArrayList for temporary in-memory data handling
 * - Polymorphism via patient.displayDetails()
 * - Exception handling for database operations
 */
public class PatientManagement {

    private Scanner scanner;

    public PatientManagement(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Patient Management Sub-Menu Loop
     */
    public void patientMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("=========================================");
            System.out.println("           PATIENT MANAGEMENT            ");
            System.out.println("=========================================");
            System.out.println("1. Add Patient");
            System.out.println("2. View All Patients");
            System.out.println("3. Search Patient");
            System.out.println("4. Update Patient");
            System.out.println("5. Delete Patient");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    addPatient();
                    break;
                case "2":
                    viewAllPatients();
                    break;
                case "3":
                    searchPatient();
                    break;
                case "4":
                    updatePatient();
                    break;
                case "5":
                    deletePatient();
                    break;
                case "6":
                    back = true;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("[!] Invalid choice! Please enter a number between 1 and 6.");
            }
        }
    }

    /**
     * Adds a new patient to the database.
     */
    public void addPatient() {
        System.out.println("\n--- Add New Patient ---");

        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("Name cannot be empty. Enter Patient Name: ");
            name = scanner.nextLine().trim();
        }

        int age = readInt("Enter Age: ", 0, 150);

        System.out.print("Enter Gender (Male/Female/Other): ");
        String gender = scanner.nextLine().trim();
        if (gender.isEmpty()) {
            gender = "Unspecified";
        }

        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Enter Diagnosed Disease/Symptom: ");
        String disease = scanner.nextLine().trim();

        String sql = "INSERT INTO patients (name, age, gender, phone, disease) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.setString(4, phone);
            pstmt.setString(5, disease);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("\n[✓] Patient registered successfully with Patient ID: " + generatedId);
                    } else {
                        System.out.println("\n[✓] Patient registered successfully!");
                    }
                }
            } else {
                System.out.println("\n[!] Failed to register patient.");
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error while adding patient: " + e.getMessage());
        }
    }

    /**
     * Fetches all patients and displays them using an ArrayList and polymorphic displayDetails().
     */
    public void viewAllPatients() {
        System.out.println("\n--- All Registered Patients ---");
        String sql = "SELECT * FROM patients ORDER BY patient_id ASC";

        // ArrayList for temporary data handling
        ArrayList<Patient> patientList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Patient p = new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("disease")
                );
                patientList.add(p);
            }

            if (patientList.isEmpty()) {
                System.out.println("No patient records found in the database.");
                return;
            }

            printPatientTableHeader();
            // Demonstrates Polymorphism: invoking displayDetails() on each Patient object
            for (Patient patient : patientList) {
                patient.displayDetails();
            }
            printPatientTableFooter();
            System.out.println("Total Patients: " + patientList.size());

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error while retrieving patients: " + e.getMessage());
        }
    }

    /**
     * Searches for a patient by ID and displays their details.
     */
    public void searchPatient() {
        System.out.println("\n--- Search Patient by ID ---");
        int patientId = readInt("Enter Patient ID to search: ", 1, Integer.MAX_VALUE);

        Patient patient = getPatientById(patientId);
        if (patient != null) {
            System.out.println("\n[✓] Patient Record Found:");
            printPatientTableHeader();
            patient.displayDetails();
            printPatientTableFooter();
        } else {
            System.out.println("\n[!] No patient found with ID: " + patientId);
        }
    }

    /**
     * Updates an existing patient's details.
     */
    public void updatePatient() {
        System.out.println("\n--- Update Patient Details ---");
        int patientId = readInt("Enter Patient ID to update: ", 1, Integer.MAX_VALUE);

        Patient existing = getPatientById(patientId);
        if (existing == null) {
            System.out.println("\n[!] Patient with ID " + patientId + " does not exist.");
            return;
        }

        System.out.println("\nCurrent Patient Details:");
        printPatientTableHeader();
        existing.displayDetails();
        printPatientTableFooter();

        System.out.println("\n(Press ENTER without typing to keep current value)");

        System.out.print("New Name [" + existing.getName() + "]: ");
        String newName = scanner.nextLine().trim();
        if (newName.isEmpty()) newName = existing.getName();

        System.out.print("New Age [" + existing.getAge() + "]: ");
        String ageStr = scanner.nextLine().trim();
        int newAge = existing.getAge();
        if (!ageStr.isEmpty()) {
            try {
                newAge = Integer.parseInt(ageStr);
            } catch (NumberFormatException ignored) {}
        }

        System.out.print("New Gender [" + existing.getGender() + "]: ");
        String newGender = scanner.nextLine().trim();
        if (newGender.isEmpty()) newGender = existing.getGender();

        System.out.print("New Phone [" + existing.getPhone() + "]: ");
        String newPhone = scanner.nextLine().trim();
        if (newPhone.isEmpty()) newPhone = existing.getPhone();

        System.out.print("New Disease [" + existing.getDisease() + "]: ");
        String newDisease = scanner.nextLine().trim();
        if (newDisease.isEmpty()) newDisease = existing.getDisease();

        String sql = "UPDATE patients SET name = ?, age = ?, gender = ?, phone = ?, disease = ? WHERE patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setString(3, newGender);
            pstmt.setString(4, newPhone);
            pstmt.setString(5, newDisease);
            pstmt.setInt(6, patientId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("\n[✓] Patient details updated successfully!");
            } else {
                System.out.println("\n[!] Update failed.");
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error during patient update: " + e.getMessage());
        }
    }

    /**
     * Deletes a patient from the database.
     */
    public void deletePatient() {
        System.out.println("\n--- Delete Patient ---");
        int patientId = readInt("Enter Patient ID to delete: ", 1, Integer.MAX_VALUE);

        Patient patient = getPatientById(patientId);
        if (patient == null) {
            System.out.println("\n[!] No patient found with ID: " + patientId);
            return;
        }

        System.out.print("Are you sure you want to delete patient '" + patient.getName() + "' (ID: " + patientId + ")? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        String sql = "DELETE FROM patients WHERE patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("\n[✓] Patient deleted successfully (along with associated appointments/bills).");
            } else {
                System.out.println("\n[!] Failed to delete patient.");
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error during patient deletion: " + e.getMessage());
        }
    }

    /**
     * Helper to retrieve a single Patient object from the database by ID.
     */
    public static Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                            rs.getInt("patient_id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getString("phone"),
                            rs.getString("disease")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error finding patient by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if a patient exists in the database.
     */
    public static boolean patientExists(int patientId) {
        return getPatientById(patientId) != null;
    }

    // Table printing helpers

    private void printPatientTableHeader() {
        System.out.println("+------+----------------------+------+----------+--------------+----------------------+");
        System.out.println("| ID   | Name                 | Age  | Gender   | Phone        | Disease              |");
        System.out.println("+------+----------------------+------+----------+--------------+----------------------+");
    }

    private void printPatientTableFooter() {
        System.out.println("+------+----------------------+------+----------+--------------+----------------------+");
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
                System.out.println("[!] Invalid input! Please enter a valid number.");
            }
        }
    }
}
