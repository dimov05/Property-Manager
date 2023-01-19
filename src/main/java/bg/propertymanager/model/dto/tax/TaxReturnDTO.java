package bg.propertymanager.model.dto.tax;

import bg.propertymanager.model.enums.TaxStatusEnum;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TaxReturnDTO {
    private Long id;
    private TaxStatusEnum taxStatus;
    private BigDecimal returnedAmount;

    public TaxReturnDTO() {
    }

    public Long getId() {
        return id;
    }

    public TaxReturnDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public TaxStatusEnum getTaxStatus() {
        return taxStatus;
    }

    public TaxReturnDTO setTaxStatus(TaxStatusEnum taxStatus) {
        this.taxStatus = taxStatus;
        return this;
    }

    @DecimalMin(value = "0", message = "Returned amount must be more than 0")
    @NotNull(message = "Returned amount must be more than 0")
    public BigDecimal getReturnedAmount() {
        return returnedAmount;
    }

    public TaxReturnDTO setReturnedAmount(BigDecimal returnedAmount) {
        this.returnedAmount = returnedAmount;
        return this;
    }
}
