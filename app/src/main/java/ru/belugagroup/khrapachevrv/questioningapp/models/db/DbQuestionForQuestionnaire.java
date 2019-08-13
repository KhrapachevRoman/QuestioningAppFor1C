package ru.belugagroup.khrapachevrv.questioningapp.models.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(active = true, nameInDb = "answer_db")
public class DbQuestionForQuestionnaire {

    @Id
    private Long id;

    @NotNull
    private Long questionnaireId;
    private Long questionId;
    private Boolean checked;

    @ToOne(joinProperty = "questionId")
    private DbQuestion question;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 194336584)
    private transient DbQuestionForQuestionnaireDao myDao;

    @Generated(hash = 232141483)
    public DbQuestionForQuestionnaire(Long id, @NotNull Long questionnaireId, Long questionId,
            Boolean checked) {
        this.id = id;
        this.questionnaireId = questionnaireId;
        this.questionId = questionId;
        this.checked = checked;
    }

    @Generated(hash = 2112474937)
    public DbQuestionForQuestionnaire() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionnaireId() {
        return this.questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public Long getQuestionId() {
        return this.questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Boolean getChecked() {
        return this.checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Generated(hash = 527827701)
    private transient Long question__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1212427233)
    public DbQuestion getQuestion() {
        Long __key = this.questionId;
        if (question__resolvedKey == null || !question__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DbQuestionDao targetDao = daoSession.getDbQuestionDao();
            DbQuestion questionNew = targetDao.load(__key);
            synchronized (this) {
                question = questionNew;
                question__resolvedKey = __key;
            }
        }
        return question;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 696270045)
    public void setQuestion(DbQuestion question) {
        synchronized (this) {
            this.question = question;
            questionId = question == null ? null : question.getId();
            question__resolvedKey = questionId;
        }
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1613801917)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDbQuestionForQuestionnaireDao() : null;
    }


}
