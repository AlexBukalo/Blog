package main.service;

import main.model.GlobalSettings;
import main.model.repository.UserRepository;
import main.request.SettingsRequest;
import main.service.dto.Settings;
import main.model.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {
    @Autowired
    private GlobalSettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    public Settings getSettings() {

        return new Settings(settingsRepository.findAll());
    }

    public void setSettings(SettingsRequest request, User user) {
        main.model.User userDB = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

        if (userDB.getIsModerator() == 1) {
            List<GlobalSettings> globalSettingsList = settingsRepository.findAll();

            for (GlobalSettings globalSettings : globalSettingsList) {
                if (globalSettings.getCode().equals("MULTIUSER_MODE")) {
                    if (request.getMultiuserMode().equals("true")) {
                        globalSettings.setValue("YES");
                    } else globalSettings.setValue("NO");
                    settingsRepository.save(globalSettings);
                }

                if (globalSettings.getCode().equals("POST_PREMODERATION")) {
                    if (request.getPostPremoderation().equals("true")) {
                        globalSettings.setValue("YES");
                    } else globalSettings.setValue("NO");
                    settingsRepository.save(globalSettings);
                }

                if (globalSettings.getCode().equals("STATISTICS_IS_PUBLIC")) {
                    if (request.getStatisticsIsPublic().equals("true")) {
                        globalSettings.setValue("YES");
                    } else globalSettings.setValue("NO");
                    settingsRepository.save(globalSettings);
                }
            }
        }
    }
}
