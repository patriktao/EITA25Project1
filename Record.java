import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

class Record {
    private Doctor doctor;
    private Nurse nurse;
    private String department = "";
    private String data = "";
    private ArrayList<String> auditLog;
    private HashMap<String, HashMap<String, Boolean>> ACL = new HashMap();    

    public Record(Doctor doctor, Nurse nurse, String department, String data){
        this.doctor = doctor;
        this.nurse = nurse;
        this.department = department;
        this.data = data;
        this.auditLog = new ArrayList();
	HashMap<String, Boolean> clientEntry = new HashMap();
	clientEntry.put("read", true);
        this.ACL.put("Oscar Cederb erg (os7138ce-s)/Elna Seyer (el3888se-s)/Patrik Tao (pa0676ta-s)/Gabriel Borglund (ga4112bo-s)", clientEntry);
    }
    
    public String read(String name){
        Boolean success = ACL.get(name).get("read");
        addAuditEntry(name, "read", success);

	    if(success){   
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
