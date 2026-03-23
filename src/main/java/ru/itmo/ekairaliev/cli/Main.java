package ru.itmo.ekairaliev.cli;

import ru.itmo.ekairaliev.service.CustodyService;
import ru.itmo.ekairaliev.service.SampleService;
import ru.itmo.ekairaliev.service.SealService;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SampleService sampleService = new SampleService();
        SealService sealService = new SealService(sampleService);
        CustodyService custodyService = new CustodyService(sampleService);

        new CliApplication(sampleService, sealService, custodyService).run();
    }
}
