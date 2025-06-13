package pergudangan.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User extends AbstractUser {
    protected final StringProperty name;
    protected final StringProperty password;
    protected final StringProperty role;
    protected final StringProperty status;

    public User(String name, String password, String role, String status) {
        this.name = new SimpleStringProperty(name);
        this.password = new SimpleStringProperty(password);
        this.role = new SimpleStringProperty(role);
        this.status = new SimpleStringProperty(status);
    }

    public User(String name, String password) {
        this(name, password, "User", "Aktif");
    }

    public User() {
        this("", "", "User", "Aktif");
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public StringProperty passwordProperty() {
        return password;
    }

    @Override
    public StringProperty roleProperty() {
        return role;
    }

    @Override
    public StringProperty statusProperty() {
        return status;
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name.set(name);
        }
    }

    @Override
    public String getPassword() {
        return password.get();
    }

    @Override
    public void setPassword(String password) {
        if (password != null && !password.trim().isEmpty()) {
            this.password.set(password);
        }
    }

    @Override
    public String getRole() {
        return role.get();
    }

    @Override
    public void setRole(String role) {
        if (role != null && !role.trim().isEmpty()) {
            this.role.set(role);
        }
    }

    @Override
    public String getStatus() {
        return status.get();
    }

    @Override
    public void setStatus(String status) {
        if (status != null && !status.trim().isEmpty()) {
            this.status.set(status);
        }
    }

    @Override
    public String displayInfo() {
        return "User biasa: " + getName();
    }

    @Override
    public String toString() {
        return "User{" +
                "name=" + getName() +
                ", role=" + getRole() +
                ", status=" + getStatus() +
                '}';
    }
}
