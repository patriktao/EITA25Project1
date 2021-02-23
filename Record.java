class Record {
    private Doctor doctor;
    private Nurse nurse;
    private String department = "";
    private String data = "";
    private HashMap<String, HashMap<String, Boolean>> ACL = new HashMap();    

    public Record(Doctor doctor, Nurse nurse, String department, String data){
        this.doctor = doctor;
        this.nurse = nurse;
        this.department = department;
        this.data = data;
        
    }
    
    public String read(String name){
	if(ACL.get(name).get("read")){
	    return data;
	} else {
	    return "access denied";
        }
    }

    public String write(String name, String data){
	if(ACL.get(name).get("write")){    
            this.data = data;
	    return "updated data to " + data;
        } else {
            return "access denied";
        }
    }
 
    public String toString(){
        return "Doctor: " + doctor + ", Nurse: " + nurse + ", Department: " + department + ", Data: " + data;
    }
}