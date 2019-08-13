package ru.belugagroup.khrapachevrv.questioningapp.ui.persons;

import java.util.List;

import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbPerson;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondent;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpView;

interface PersonsView extends MvpView {

    void personsSuccess(List<DbPerson> personList);
}
