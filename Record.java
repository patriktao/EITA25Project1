import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

class Record {
    private Doctor doctor;
    private Nurse nurse;
    private Patient patient;
    private String department = "";
    private String data = "";
    private ArrayList<String> auditLog;

    public Record(Doctor doctor, Nurse nurse, Patient patient, String department, String data){
        this.doctor = doctor;
        this.nurse = nurse;
        this.department = department;
        this.data = data;
        this.patient = patient;
        this.auditLog = new ArrayList();

    }

    private Boolean hasReadAccess(Indiv person){
        switch(person.getClass().getSimpleName()){
            case "Doctor": {
                return ((Doctor) person).department.equals(this.department) || person.equals(this.doctor);
            }

            case "Patient": {
                return person.equals(patient);
            }

            case "Nurse": {
                return ((Nurse) person).department.equals(this.department) || person.equals(this.nurse);
            }

            case "Gov": {
                return true;
            }
            default: {return false;}
        }
    }


    private Boolean hasWriteAccess(Indiv person){
        switch(person.getClass().getSimpleName()){
            case "Doctor": {
                return person.equals(this.doctor);
            }

            case "Person": {
                return person.equals(patient);
            }

            case "Nurse": {
                return person.equals(this.nurse);
            }

            case "Gov": {
                return false;
            }
            default: {return false;}
        }
    }
    
    public String read(Indiv person){
        Boolean success = hasReadAccess(person);

        System.out.println(person.getClass().getSimpleName());
        
        addAuditEntry(person.toString(), "read", success);

	    if(success ){   
            return data;
        } else {
            return "access denied"; 
        }
    }

    public String write(Indiv person, String data){
        Boolean success = hasWriteAccess(person);
        addAuditEntry(person.toString(), "write", success);

	    if(success){    
            this.data = data;
	        return "updated data to " + data;
        } else {
            return "access denied";
        }
    }

    public void addAuditEntry(String name, String action, Boolean success){
        String entry = LocalDateTime.now() + " - " + name + " tried to " + action + ": ";
        entry += (success ? "approved" : "denied");
        auditLog.add(entry);
    }
 
    public String toString(){
        return "Doctor: " + doctor + ", Nurse: " + nurse + ", Department: " + department + ", Data: " + data;
    }
}
