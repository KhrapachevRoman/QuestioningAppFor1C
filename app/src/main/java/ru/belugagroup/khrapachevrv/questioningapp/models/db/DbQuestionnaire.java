package ru.belugagroup.khrapachevrv.questioningapp.models.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(active = true, nameInDb = "questionnaire_db")
public class DbQuestionnaire {

    @Id
    private Long id;

    @NotNull
    private Long respondentId;
    private Long templateId;
    private Long personId;
    private Long dateInMillis;
    private String comment;

    @ToOne(joinProperty = "respondentId")
    private DbRespondent respondent;

    @ToOne(joinProperty = "templateId")
    private DbTemplate template;

    @ToOne(joinProperty = "personId")
    private DbPerson person;

    @ToMany(referencedJoinProperty = "questionnaireId")
    private List<DbQuestionForQuestionnaire> answerList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1389671513)
    private transient DbQuestionnaireDao myDao;

    @Generated(hash = 7310669)
    public DbQuestionnaire(Long id, @NotNull Long respondentId, Long templateId, Long personId, Long dateInMillis,
            String comment) {
        this.id = id;
        this.respondentId = respondentId;
        this.templateId = templateId;
        this.personId = personId;
        this.dateInMillis = dateInMillis;
        this.comment = comment;
    }


    @Generated(hash = 1806200432)
    public DbQuestionnaire() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRespondentId() {
        return this.respondentId;
    }

    public void setRespondentId(Long respondentId) {
        this.respondentId = respondentId;
    }

    public Long getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getDateInMillis() {
        return this.dateInMillis;
    }

    public void setDateInMillis(Long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    @Generated(hash = 365431870)
    private transient Long respondent__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1672575196)
    public DbRespondent getRespondent() {
        Long __key = this.respondentId;
        if (respondent__resolvedKey == null
                || !respondent__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DbRespondentDao targetDao = daoSession.getDbRespondentDao();
            DbRespondent respondentNew = targetDao.load(__key);
            synchronized (this) {
                respondent = respondentNew;
                respondent__resolvedKey = __key;
            }
        }
        return respondent;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1687595813)
    public void setRespondent(@NotNull DbRespondent respondent) {
        if (respondent == null) {
            throw new DaoException(
                    "To-one property 'respondentId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.respondent = respondent;
            respondentId = respondent.getId();
            respondent__resolvedKey = respondentId;
        }
    }

    @Generated(hash = 1847801086)
    private transient Long template__resolvedKey;

    @Generated(hash = 1154009267)
    private transient Long person__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 14518169)
    public DbTemplate getTemplate() {
        Long __key = this.templateId;
        if (template__resolvedKey == null || !template__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DbTemplateDao targetDao = daoSession.getDbTemplateDao();
            DbTemplate templateNew = targetDao.load(__key);
            synchronized (this) {
                template = templateNew;
                template__resolvedKey = __key;
            }
        }
        return template;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 821681587)
    public void setTemplate(DbTemplate template) {
        synchronized (this) {
            this.template = template;
            templateId = template == null ? null : template.getId();
            template__resolvedKey = templateId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1713680572)
    public List<DbQuestionForQuestionnaire> getAnswerList() {
        if (answerList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DbQuestionForQuestionnaireDao targetDao = daoSession.getDbQuestionForQuestionnaireDao();
            List<DbQuestionForQuestionnaire> answerListNew = targetDao._queryDbQuestionnaire_AnswerList(id);
            synchronized (this) {
                if (answerList == null) {
                    answerList = answerListNew;
                }
            }
        }
        return answerList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 49410384)
    public synchronized void resetAnswerList() {
        answerList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 789533632)
    public DbPerson getPerson() {
        Long __key = this.personId;
        if (person__resolvedKey == null || !person__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DbPersonDao targetDao = daoSession.getDbPersonDao();
            DbPerson personNew = targetDao.load(__key);
            synchronized (this) {
                person = personNew;
                person__resolvedKey = __key;
            }
        }
        return person;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 914818734)
    public void setPerson(DbPerson person) {
        synchronized (this) {
            this.person = person;
            personId = person == null ? null : person.getId();
            person__resolvedKey = personId;
        }
    }

    public String getComment() {
        return this.comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1967041669)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDbQuestionnaireDao() : null;
    }
}
