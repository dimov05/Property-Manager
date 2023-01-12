package bg.propertymanager.model.dto.tax;

import bg.propertymanager.model.enums.TaxStatusEnum;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TaxEditDTO {
    private Long id;
    private BigDecimal amount;
    private TaxStatusEnum taxStatus;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;

    public TaxEditDTO() {
    }

    public Long getId() {
        return id;
    }

    public TaxEditDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @DecimalMin("0")
    public BigDecimal getAmount() {
        return amount;
    }

    public TaxEditDTO setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public TaxEditDTO setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public TaxEditDTO setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public TaxEditDTO setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }
}
