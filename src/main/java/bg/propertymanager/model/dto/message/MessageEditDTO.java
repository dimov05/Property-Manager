package bg.propertymanager.model.dto.message;

import bg.propertymanager.model.entity.BuildingEntity;
import bg.propertymanager.model.entity.UserEntity;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

public class MessageEditDTO {
    private Long id;
    private String title;
    private String content;

    public MessageEditDTO() {
    }

    public Long getId() {
        return id;
    }

    public MessageEditDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @NotEmpty(message = "Title of message should not be empty")
    public String getTitle() {
        return title;
    }

    public MessageEditDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    @NotEmpty(message = "Content of message should not be empty")
    public String getContent() {
        return content;
    }

    public MessageEditDTO setContent(String content) {
        this.content = content;
        return this;
    }
}
