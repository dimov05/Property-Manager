package bg.propertymanager.model.dto.message;

import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

public class MessageAddDTO {
    private Long id;
    private UserEntity author;
    private LocalDateTime createdDate;
    private String title;
    private String content;
    private BuildingEntity building;

    public MessageAddDTO() {
    }

    public Long getId() {
        return id;
    }

    public MessageAddDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public MessageAddDTO setAuthor(UserEntity author) {
        this.author = author;
        return this;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public MessageAddDTO setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    @NotEmpty(message = "Title of message should not be empty")
    public String getTitle() {
        return title;
    }

    public MessageAddDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    @NotEmpty(message = "Content of message should not be empty")
    public String getContent() {
        return content;
    }

    public MessageAddDTO setContent(String content) {
        this.content = content;
        return this;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public MessageAddDTO setBuilding(BuildingEntity building) {
        this.building = building;
        return this;
    }
}
