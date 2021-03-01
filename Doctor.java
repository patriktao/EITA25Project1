class Doctor extends Indiv{
    private String department = "";
    public Doctor(String name, String department){
	super(name);
        this.department = department;
    }
    
    public String toString(){
        return super.name;
    }

}
