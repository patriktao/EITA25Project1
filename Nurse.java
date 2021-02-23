class Nurse extends Indiv{
    private String department = "";
    public Nurse(String name, String department){
	super(name);
        this.department = department;
    }

    public String toString(){
        return super.name + " from department: " + department; 
    }
}   