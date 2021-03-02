class Patient extends Indiv{
    private Record record;

    public Patient(String name, Record record){
 	super(name);
        this.record = record;
    }

    public String toString(){
        return name;
    }

    public void setInitRecord(Record r){
        if(this.record == null){
            this.record = r;
        }
    }
}  