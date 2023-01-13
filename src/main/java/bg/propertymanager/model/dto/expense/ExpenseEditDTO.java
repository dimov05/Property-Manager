package bg.propertymanager.model.dto.expense;

import bg.propertymanager.model.enums.TaxStatusEnum;
import bg.propertymanager.model.enums.TaxTypeEnum;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenseEditDTO {
    private Long id;
    private TaxStatusEnum taxStatus;

    public ExpenseEditDTO() {
    }

    public Long getId() {
        return id;
    }

    public ExpenseEditDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @NotNull(message = "Tax status should not be null")
    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public ExpenseEditDTO setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }
}
