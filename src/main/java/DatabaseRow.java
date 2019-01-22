public class DatabaseRow {
    private String name;
    private String surname;

    public DatabaseRow(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    @Override
    public String toString() {
        return this.name+"\t|\t"+this.surname+"\t|\t";
    }
}
