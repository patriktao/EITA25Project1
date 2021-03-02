class Indiv {
    protected String name = "";

    public Indiv(String name){
        this.name = name;        
    }     

    public String toString(){
        return this.name;
    }

    public Boolean equals(Indiv person){
        return this.name.equals(person.toString());
    }

}