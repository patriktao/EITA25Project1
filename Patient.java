class Patient extends Indiv{
    private Record record; 
    public Patient(String name, Record record){
 	super(name);
        this.record = record;
    }

    public String toString(){
        return name;
    }
}  