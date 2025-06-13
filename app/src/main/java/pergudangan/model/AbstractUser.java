package pergudangan.model;

import javafx.beans.property.StringProperty;

public abstract class AbstractUser {
    public abstract StringProperty nameProperty();
    public abstract StringProperty passwordProperty();
    public abstract StringProperty roleProperty();
    public abstract StringProperty statusProperty();

    public abstract String getName();
    public abstract void setName(String name);

    public abstract String getPassword();
    public abstract void setPassword(String password);

    public abstract String getRole();
    public abstract void setRole(String role);

    public abstract String getStatus();
    public abstract void setStatus(String status);

    public abstract String displayInfo(); // Polymorphic method
}
