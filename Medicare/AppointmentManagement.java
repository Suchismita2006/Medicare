import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * AppointmentManagement handles booking, viewing, searching, and cancelling appointments.
 *
 * Demonstrates:
 * - Foreign key integration between patients and doctors
 * - PreparedStatement with SQL JOIN queries
 * - ArrayList for temporary storage of appointments
 * - Exception handling and validation of patient/doctor IDs and date format
 */
public class AppointmentManagement {

    private Scanner scanner;

    public AppointmentManagement(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Appointment Management Sub-Menu Loop
     */
    public void appointmentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("=========================================");
            System.out.println("         APPOINTMENT MANAGEMENT          ");
            System.out.println("=========================================");
            System.out.println("1. Book Appointment");
            System.out.println("2. View All Appointments");
            System.out.println("3. Search Appointment");
            System.out.println("4. Cancel Appointment");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    bookAppointment();
                    break;
                case "2":
                    viewAllAppointments();
                    break;
                case "3":
                    searchAppointment();
                    break;
                case "4":
                    cancelAppointment();
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
     * Books a new appointment after verifying patient and doctor exist.
     */
    public void bookAppointment() {
        System.out.println("\n--- Book New Appointment ---");

        // 1. Validate Patient ID
        int patientId = readInt("Enter Patient ID: ", 1, Integer.MAX_VALUE);
        Patient patient = PatientManagement.getPatientById(patientId);
        if (patient == null) {
            System.out.println("[!] Patient with ID " + patientId + " does not exist. Please register the patient first.");
            return;
        }
        System.out.println("-> Selected Patient: " + patient.getName() + " (Age: " + patient.getAge() + ")");

        // 2. Validate Doctor ID
        int doctorId = readInt("Enter Doctor ID: ", 1, Integer.MAX_VALUE);
        Doctor doctor = DoctorManagement.getDoctorById(doctorId);
        if (doctor == null) {
            System.out.println("[!] Doctor with ID " + doctorId + " does not exist.");
            return;
        }
        System.out.println("-> Selected Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() +
                ") | Fee: Rs. " + doctor.getConsultationFee());

        // 3. Appointment Date
        String date = readDate("Enter Appointment Date (YYYY-MM-DD) [Press ENTER for Today]: ");

        String status = "Scheduled";
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, patientId);
            pstmt.setInt(2, doctorId);
            pstmt.setDate(3, java.sql.Date.valueOf(date));
            pstmt.setString(4, status);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int appointmentId = rs.getInt(1);
                        System.out.println("\n==================================================");
                        System.out.println("         APPOINTMENT BOOKED SUCCESSFULLY!         ");
                        System.out.println("==================================================");
                        System.out.println(" Appointment ID : #" + appointmentId);
                        System.out.println(" Patient        : " + patient.getName() + " (ID: " + patientId + ")");
                        System.out.println(" Doctor         : " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
                        System.out.println(" Date           : " + date);
                        System.out.println(" Status         : " + status);
                        System.out.println(" Consultation   : Rs. " + doctor.getConsultationFee());
                        System.out.println("==================================================");
                    }
                }
            } else {
                System.out.println("\n[!] Failed to book appointment.");
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error while booking appointment: " + e.getMessage());
        }
    }

    /**
     * Views all appointments in the system using an ArrayList.
     */
    public void viewAllAppointments() {
        System.out.println("\n--- All Appointments ---");

        String sql = "SELECT a.appointment_id, a.patient_id, p.name AS patient_name, " +
                "a.doctor_id, d.name AS doctor_name, a.appointment_date, a.status " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "ORDER BY a.appointment_id ASC";

        ArrayList<Appointment> appointmentList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Appointment app = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("appointment_date"),
                        rs.getString("status")
                );
                appointmentList.add(app);
            }

            if (appointmentList.isEmpty()) {
                System.out.println("No appointments found in the system.");
                return;
            }

            printAppointmentTableHeader();
            for (Appointment app : appointmentList) {
                app.displayDetails();
            }
            printAppointmentTableFooter();
            System.out.println("Total Appointments: " + appointmentList.size());

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error while retrieving appointments: " + e.getMessage());
        }
    }

    /**
     * Searches for an appointment by ID.
     */
    public void searchAppointment() {
        System.out.println("\n--- Search Appointment by ID ---");
        int appId = readInt("Enter Appointment ID to search: ", 1, Integer.MAX_VALUE);

        String sql = "SELECT a.appointment_id, a.patient_id, p.name AS patient_name, " +
                "a.doctor_id, d.name AS doctor_name, a.appointment_date, a.status " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Appointment app = new Appointment(
                            rs.getInt("appointment_id"),
                            rs.getInt("patient_id"),
                            rs.getString("patient_name"),
                            rs.getInt("doctor_id"),
                            rs.getString("doctor_name"),
                            rs.getString("appointment_date"),
                            rs.getString("status")
                    );
                    System.out.println("\n[✓] Appointment Record Found:");
                    printAppointmentTableHeader();
                    app.displayDetails();
                    printAppointmentTableFooter();
                } else {
                    System.out.println("\n[!] No appointment found with ID: " + appId);
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error during appointment search: " + e.getMessage());
        }
    }

    /**
     * Cancels an appointment by changing its status to 'Cancelled'.
     */
    public void cancelAppointment() {
        System.out.println("\n--- Cancel Appointment ---");
        int appId = readInt("Enter Appointment ID to cancel: ", 1, Integer.MAX_VALUE);

        // Check if appointment exists
        String checkSql = "SELECT status FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, appId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\n[!] No appointment found with ID: " + appId);
                    return;
                }
                String currentStatus = rs.getString("status");
                if ("Cancelled".equalsIgnoreCase(currentStatus)) {
                    System.out.println("\n[!] This appointment is already Cancelled.");
                    return;
                }
            }

            System.out.print("Are you sure you want to cancel appointment #" + appId + "? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("yes") && !confirm.equals("y")) {
                System.out.println("Cancellation aborted.");
                return;
            }

            String updateSql = "UPDATE appointments SET status = 'Cancelled' WHERE appointment_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, appId);
                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("\n[✓] Appointment #" + appId + " has been marked as CANCELLED.");
                } else {
                    System.out.println("\n[!] Failed to cancel appointment.");
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Database error during appointment cancellation: " + e.getMessage());
        }
    }

    // Helper methods

    private void printAppointmentTableHeader() {
        System.out.println("+------+----------------------------+----------------------------+--------------+-------------+");
        System.out.println("| ID   | Patient (ID)               | Doctor (ID)                | Date         | Status      |");
        System.out.println("+------+----------------------------+----------------------------+--------------+-------------+");
    }

    private void printAppointmentTableFooter() {
        System.out.println("+------+----------------------------+----------------------------+--------------+-------------+");
    }

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

    private String readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return LocalDate.now().toString(); // Default to today's date
            }
            try {
                LocalDate parsed = LocalDate.parse(input);
                return parsed.toString();
            } catch (DateTimeParseException e) {
                System.out.println("[!] Invalid date format! Please use YYYY-MM-DD (e.g., 2026-11-20).");
            }
        }
    }
}
