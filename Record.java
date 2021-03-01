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
    private HashMap<String, HashMap<String, Boolean>> ACL = new HashMap();    

    public Record(Doctor doctor, Nurse nurse, Patient patient, String department, String data){
        this.doctor = doctor;
        this.nurse = nurse;
        this.department = department;
        this.data = data;
        this.patient = patient;
        this.auditLog = new ArrayList();
	    HashMap<String, Boolean> doctorEntry = new HashMap();
	    doctorEntry.put("read", true );
        this.ACL.put("CN=" + doctor.toString() , doctorEntry);
	    HashMap<String, Boolean> nurseEntry = new HashMap();
	    nurseEntry.put("read", true);
        this.ACL.put("CN=" + nurse.toString() , nurseEntry);
        

    }

    private Boolean hasReadAccess(Indiv person){
        switch(person.getClass().getSimpleName()){
            case "Doctor": {
                return false;
            }

            case "Person": {
                
                return false;
            }

            case "Nurse": {
                return true;
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

    public String write(String name, String data){
        Boolean success = ACL.get(name).get("write");
        addAuditEntry(name, "write", success);

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
