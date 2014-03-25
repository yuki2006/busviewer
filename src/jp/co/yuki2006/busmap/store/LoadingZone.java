package jp.co.yuki2006.busmap.store;

import java.io.Serializable;

/**
 * 乗り場名を管理するクラスです。
 *
 * @author yuki
 */
public class LoadingZone implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2L;
    private final String loadingID;
    private final String aliasName;
    private final int dbId;

    public LoadingZone(String loadingID, String aliasName) {
        this(loadingID, aliasName, -1);
    }

    public LoadingZone(String loadingID, String aliasname, int DBid) {
        super();
        if (loadingID == null) {
            this.loadingID = "";
        } else {
            this.loadingID = loadingID;
        }
        if (aliasname == null) {
            this.aliasName = "";
        } else {
            this.aliasName = aliasname;
        }

        this.dbId = DBid;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LoadingZone) {
            LoadingZone loadingZone = (LoadingZone) o;
            return loadingZone.loadingID.equals(this.loadingID);
        } else {
            return false;
        }

    }

    public String getAliasName() {
        return aliasName;
    }

    public int getDbId() {
        return dbId;
    }

    public String getLoadingID() {
        return loadingID;
    }

    public int hashCode() {
        assert false : "hashCodeが呼び出されることは想定されていません。";
        return 42; // 適当な値
    }

    public boolean isCanLoading() {
        if (loadingID.equals("") || loadingID.equals("z")) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (aliasName.length() > 0) {
            return aliasName;
        } else {
            return loadingID;
        }
    }

}
