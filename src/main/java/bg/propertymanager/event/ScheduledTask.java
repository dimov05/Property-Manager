package bg.propertymanager.event;

import bg.propertymanager.service.TaxService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class ScheduledTask {
    private final TaxService taxService;

    public ScheduledTask(TaxService taxService) {
        this.taxService = taxService;
    }

    @Scheduled(cron = "0 0 2 1 * ?") // Every 1st day of month at 02:00 am, periodicTaxes will be added for every apartment
    public void createPeriodicTaxesEveryMonth() {
        System.out.println("i am scheduled method");
        taxService.createPeriodicTaxesForEveryApartment();
    }
}
