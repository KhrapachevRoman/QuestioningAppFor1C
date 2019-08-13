package ru.belugagroup.khrapachevrv.questioningapp.ui.polls;

import java.util.List;

import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplate;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpView;



interface PollsView extends MvpView {
    Long getPartnersDbId();
    void partnersSuccess(List<DbTemplate> templateList);
}