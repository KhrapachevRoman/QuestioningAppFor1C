package ru.belugagroup.khrapachevrv.questioningapp.models.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.DaoException;

@Entity(active = true, nameInDb = "person_db")
public class DbPerson {

    @Id
    private Long id;

    @NotNull
    private String name;
    private Boolean deleteMark;
    @NotNull
    @Index(unique = true)
    private String externalId;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 988612546)
    private transient DbPersonDao myDao;
    @Generated(hash = 313408164)
    public DbPerson(Long id, @NotNull String name, Boolean deleteMark,
            @NotNull String externalId) {
        this.id = id;
        this.name = name;
        this.deleteMark = deleteMark;
        this.externalId = externalId;
    }
    @Generated(hash = 1909220004)
    public DbPerson() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getDeleteMark() {
        return this.deleteMark;
    }
    public void setDeleteMark(Boolean deleteMark) {
        this.deleteMark = deleteMark;
    }
    public String getExternalId() {
        return this.externalId;
    }
    public void setExternalId(String externalId) {
        this.externalId = externalId;
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
    @Generated(hash = 654000115)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDbPersonDao() : null;
    }
    

}
