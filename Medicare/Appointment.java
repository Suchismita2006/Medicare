/**
 * Appointment class represents an appointment between a patient and a doctor.
 *
 * Demonstrates:
 * - Encapsulation: Private fields with public getters and setters.
 */
public class Appointment {

    private int appointmentId;
    private int patientId;
    private int doctorId;
    private String appointmentDate;
    private String status;

    // Optional descriptive fields loaded from JOIN queries for convenient display
    private String patientName;
    private String doctorName;

    /**
     * Default constructor
     */
    public Appointment() {
    }

    /**
     * Parameterized constructor for basic record
     */
    public Appointment(int appointmentId, int patientId, int doctorId, String appointmentDate, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.status = status;
    }

    /**
     * Parameterized constructor with patient and doctor names
     */
    public Appointment(int appointmentId, int patientId, String patientName,
                       int doctorId, String doctorName, String appointmentDate, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.status = status;
    }

    // Getters and Setters

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    /**
     * Displays appointment details in a formatted table row.
     */
    public void displayDetails() {
        String pName = (patientName != null) ? patientName : "ID: " + patientId;
        String dName = (doctorName != null) ? doctorName : "ID: " + doctorId;
        System.out.printf("| %-4d | %-20s (ID:%-2d) | %-20s (ID:%-2d) | %-12s | %-11s |\n",
                appointmentId, pName, patientId, dName, doctorId, appointmentDate, status);
    }

    @Override
    public String toString() {
        return "Appointment [ID=" + appointmentId + ", PatientID=" + patientId +
                ", DoctorID=" + doctorId + ", Date=" + appointmentDate + ", Status=" + status + "]";
    }
}
