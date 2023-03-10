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
    private TaxStatusEnum taxStatus;
    private BigDecimal paidAmount;

    public TaxEditDTO() {
    }

    public Long getId() {
        return id;
    }

    public TaxEditDTO setId(Long id) {
        this.id = id;
        return this;
    }

    @NotNull
    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public TaxEditDTO setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }

    @DecimalMin("0")
    @NotNull(message = "Paid amount must be positive")
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public TaxEditDTO setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
        return this;
    }
}
