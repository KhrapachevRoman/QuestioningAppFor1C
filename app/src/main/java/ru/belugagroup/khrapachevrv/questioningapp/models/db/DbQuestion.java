package ru.belugagroup.khrapachevrv.questioningapp.models.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(active = true, nameInDb = "question_db")
public class DbQuestion {

    @Id
    private Long id;

    @NotNull
    private String externalId;
    private String simpleId;
    private Long templateId;
    private String text;
    private String hint;
    private Boolean isGroup;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2095758253)
    private transient DbQuestionDao myDao;

    @Generated(hash = 456326910)
    public DbQuestion(Long id, @NotNull String externalId, String simpleId, Long templateId,
            String text, String hint, Boolean isGroup) {
        this.id = id;
        this.externalId = externalId;
        this.simpleId = simpleId;
        this.templateId = templateId;
        this.text = text;
        this.hint = hint;
        this.isGroup = isGroup;
    }

    @Generated(hash = 777137991)
    public DbQuestion() {
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getSimpleId() {
        return simpleId;
    }

    public void setSimpleId(String simpleId) {
        this.simpleId = simpleId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Boolean getIsGroup() {
        return this.isGroup;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
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
    @Generated(hash = 1507390064)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDbQuestionDao() : null;
    }
}

