package bg.propertymanager.model.entity;

import bg.propertymanager.model.enums.UserRolesEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class RoleEntity {

    private Long id;

    private UserRolesEnum role;

    public RoleEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    public UserRolesEnum getRole() {
        return role;
    }

    public void setRole(UserRolesEnum role) {
        this.role = role;
    }
}
