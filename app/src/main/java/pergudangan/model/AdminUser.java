package pergudangan.model;

public class AdminUser extends User {

    public AdminUser(String name, String password) {
        super(name, password, "Admin", "Aktif");
    }

    @Override
    public String displayInfo() {
        return "Admin: " + getName();
    }
}
