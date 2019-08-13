package ru.belugagroup.khrapachevrv.questioningapp.ui.partners;

import java.util.List;

import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondent;
import ru.belugagroup.khrapachevrv.questioningapp.ui.mvp.MvpView;

interface PartnersView extends MvpView {

    void partnersSuccess(List<DbRespondent> partnerList);
}
