package main.model.repository;

import main.model.GlobalSettings;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    private final SettingsRepository settingsRepository;

    public DataLoader(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Iterable<GlobalSettings> globalSettingsList = settingsRepository.findAll();
        List<GlobalSettings> settings = new ArrayList<>();

        for (GlobalSettings setting : globalSettingsList) {
            settings.add(setting);
        }

        if (settings.size() == 0) {
            settingsRepository.save(new GlobalSettings("MULTIUSER_MODE", "Многопользовательский режим", "YES"));

            settingsRepository.save(new GlobalSettings("POST_PREMODERATION", "Премодерация постов", "YES"));

            settingsRepository.save(new GlobalSettings("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "YES"));
        }
    }
}
