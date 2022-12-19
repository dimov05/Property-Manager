package bg.propertymanager.model.entity;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class MessageEntity {
    private Long id;
    private UserEntity author;
    private LocalDateTime createdDate;
    private String content;
    private BuildingEntity building;

    public MessageEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public MessageEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @ManyToOne
    public UserEntity getAuthor() {
        return author;
    }

    public MessageEntity setAuthor(UserEntity author) {
        this.author = author;
        return this;
    }

    @ManyToOne
    public BuildingEntity getBuilding() {
        return building;
    }

    public MessageEntity setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public MessageEntity setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MessageEntity setContent(String content) {
        this.content = content;
        return this;
    }
}
