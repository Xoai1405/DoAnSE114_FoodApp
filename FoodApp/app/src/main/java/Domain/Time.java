package Domain;

public class Time {
    private int ID;
    private String Value;
    public Time() {
    }
    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public String getValue() {
        return Value;
    }
    public void setValue(String value) {
        Value = value;
    }
    @Override
    public String toString() {
        return Value ;
    }
}
