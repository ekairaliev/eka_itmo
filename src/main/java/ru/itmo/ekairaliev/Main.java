package ru.itmo.ekairaliev;

import ru.itmo.ekairaliev.model.CustodyEvent;
import ru.itmo.ekairaliev.model.Sample;
import ru.itmo.ekairaliev.model.Seal;
import ru.itmo.ekairaliev.service.CustodyService;
import ru.itmo.ekairaliev.service.SampleService;
import ru.itmo.ekairaliev.service.SealService;
import ru.itmo.ekairaliev.validation.ValidationException;

import java.util.List;
import java.util.Optional;

public final class Main {

    public static void main(String[] args) {
        // 1) Создаём сервисы (коллекции в памяти)
        SampleService sampleService = new SampleService();
        SealService sealService = new SealService(sampleService);
        CustodyService custodyService = new CustodyService(sampleService);

        try {
            // 2) Создаём sample
            Sample sample = sampleService.add("Blood Tube #1");
            System.out.println("Создан sample: " + sample);

            long sampleId = sample.getId();

            // 3) Добавляем пломбу к sample
            Seal seal = sealService.add(sampleId, "SEAL-0001", "emil");
            System.out.println("Добавлена пломба: " + seal);

            // 4) Передаём sample от одного пользователя другому (custody_event)
            CustodyEvent ev1 = custodyService.add(
                    sampleId,
                    "lab_tech_1",
                    "storage_admin",
                    "ITMO Lab / Room 101",
                    "Передал на хранение",
                    "emil"
            );
            System.out.println("Событие custody: " + ev1);

            // 5) Проверяем текущего владельца по последнему событию
            Optional<String> owner = custodyService.currentOwner(sampleId);
            System.out.println("Текущий владелец sampleId=" + sampleId + ": " +
                    owner.orElse("owner НЕ определён (нет событий)"));

            // 6) Ломаем пломбу
            Seal broken = sealService.breakSeal(seal.getId());
            System.out.println("Пломба после break: " + broken);

            // 7) Выводим все объекты в коллекциях
            System.out.println("\n=== ВСЕ SAMPLES ===");
            printList(sampleService.getAll());

            System.out.println("\n=== ВСЕ SEALS ===");
            printList(sealService.getAll());

            System.out.println("\n=== ВСЕ CUSTODY EVENTS (по sample) ===");
            List<CustodyEvent> events = custodyService.listBySample(sampleId);
            printList(events);

        } catch (ValidationException e) {
            // твои "понятные ошибки" для пользователя
            System.out.println(e.getMessage());
        } catch (Exception e) {
            // всё остальное (чтобы не падало молча)
            System.out.println("Неожиданная ошибка: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static <T> void printList(List<T> list) {
        if (list.isEmpty()) {
            System.out.println("(пусто)");
            return;
        }
        for (T item : list) {
            System.out.println(item);
        }
    }
}