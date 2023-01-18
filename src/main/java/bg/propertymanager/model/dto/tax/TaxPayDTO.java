package bg.propertymanager.model.dto.tax;

import bg.propertymanager.model.enums.TaxStatusEnum;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TaxPayDTO {
    private Long id;
    private TaxStatusEnum taxStatus;
    private BigDecimal paidAmount;

    public TaxPayDTO() {
    }

    public Long getId() {
        return id;
    }

    public TaxPayDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public TaxPayDTO setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }

    @DecimalMin(value = "0", message = "Paid amount must be more than 0")
    @NotNull(message = "Paid amount must be more than 0")
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public TaxPayDTO setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
        return this;
    }
}
