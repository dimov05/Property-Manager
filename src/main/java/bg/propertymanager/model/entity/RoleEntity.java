package bg.propertymanager.model.entity;

import bg.propertymanager.model.enums.UserRolesEnum;

import javax.persistence.*;


@Entity
@Table(name = "roles")
public class RoleEntity {

    private Long id;

    private UserRolesEnum role;
    private String name;

    public RoleEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public RoleEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public UserRolesEnum getRole() {
        return role;
    }

    public RoleEntity setRole(UserRolesEnum role) {
        this.role = role;
        return this;
    }

    public String getName() {
        return role.name();
    }

    public RoleEntity setName(String name) {
        this.name = name;
        return this;
    }
}
