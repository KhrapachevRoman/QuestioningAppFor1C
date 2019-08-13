package ru.belugagroup.khrapachevrv.questioningapp.models.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(active = true, nameInDb = "template_db")
public class DbTemplate {

    @Id
    private Long id;

    @NotNull
    private String introduction;
    private String conclusion;
    private String title;
    private Boolean deleteMark;
    @NotNull
    @Index(unique = true)
    private String externalId;

    @ToMany(referencedJoinProperty = "templateId")
    @OrderBy("id ASC")
    private List<DbQuestion> questionList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getDeleteMark() {
        return deleteMark;
    }

    public void setDeleteMark(Boolean deleteMark) {
        this.deleteMark = deleteMark;
    }

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 258173985)
    private transient DbTemplateDao myDao;

    @Generated(hash = 1097489722)
    public DbTemplate(Long id, @NotNull String introduction, String conclusion, String title,
            Boolean deleteMark, @NotNull String externalId) {
        this.id = id;
        this.introduction = introduction;
        this.conclusion = conclusion;
        this.title = title;
        this.deleteMark = deleteMark;
        this.externalId = externalId;
    }

    @Generated(hash = 1734745349)
    public DbTemplate() {
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 135079299)
    public List<DbQuestion> getQuestionList() {
        if (questionList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DbQuestionDao targetDao = daoSession.getDbQuestionDao();
            List<DbQuestion> questionListNew = targetDao._queryDbTemplate_QuestionList(id);
            synchronized (this) {
                if (questionList == null) {
                    questionList = questionListNew;
                }
            }
        }
        return questionList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1378007343)
    public synchronized void resetQuestionList() {
        questionList = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 867586393)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDbTemplateDao() : null;
    }
}
