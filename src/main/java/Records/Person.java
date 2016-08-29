package Records;

public class Person {
    private String name;
    private String familyName;
    private String id;
    private boolean isAffiliated;
    private boolean isEmployed;

    public void affiliate() {
        isAffiliated = true;
    }

    public void setEmployed() {
        isEmployed = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAffiliated() {
        return isAffiliated;
    }

    public boolean isEmployed() {
        return isEmployed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person author = (Person) o;

        if (!name.equals(author.name)) return false;
        if (!familyName.equals(author.familyName)) return false;
        return id.equals(author.id);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        if (familyName != null) result = 31 * result + familyName.hashCode();
        if (id != null) result = 31 * result + id.hashCode();
        return result;
    }
}
