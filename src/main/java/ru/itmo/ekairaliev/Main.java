package ru.itmo.ekairaliev;

import ru.itmo.ekairaliev.model.Sample;
import ru.itmo.ekairaliev.model.Seal;
import ru.itmo.ekairaliev.service.CustodyService;
import ru.itmo.ekairaliev.service.SampleService;
import ru.itmo.ekairaliev.service.SealService;
import ru.itmo.ekairaliev.validation.ValidationException;

public final class Main {

    public static void main(String[] args) {
        SampleService sampleService = new SampleService();
        SealService sealService = new SealService(sampleService);
        CustodyService custodyService = new CustodyService(sampleService);

        try {
            Sample sample = sampleService.add("Blood Tube #1");
            Seal seal = sealService.add(sample.getId(), "SEAL-0001", "emil");
            custodyService.add(
                    sample.getId(),
                    "tech_1",
                    "storage_admin",
                    "ITMO Lab",
                    "Delivered",
                    "emil"
            );

            System.out.println(sample);
            System.out.println(seal);
            System.out.println(custodyService.currentOwner(sample.getId()).orElse("No owner"));
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}
