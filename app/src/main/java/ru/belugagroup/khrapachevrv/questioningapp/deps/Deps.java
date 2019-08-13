package ru.belugagroup.khrapachevrv.questioningapp.deps;

import javax.inject.Singleton;

import dagger.Component;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.NetworkModule;
import ru.belugagroup.khrapachevrv.questioningapp.ui.partners.PartnersActivity;
import ru.belugagroup.khrapachevrv.questioningapp.ui.polls.PollsActivity;
import ru.belugagroup.khrapachevrv.questioningapp.ui.settings.SettingsActivity;

@Singleton
@Component(modules = {NetworkModule.class,})
public interface Deps {
    void inject(PartnersActivity partnersActivity);
    void inject(PollsActivity pollsActivity);
    void inject(SettingsActivity settingsActivity);
}
