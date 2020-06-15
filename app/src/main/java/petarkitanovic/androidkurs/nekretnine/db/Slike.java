package petarkitanovic.androidkurs.nekretnine.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Slike.TABLE_NAME)
public class Slike {

    public static final String TABLE_NAME = "slike";

    public static final String FIELD_ID = "id";
    public static final String FIELD_SLIKA = "slika";
    public static final String FIELD_NAME = "nekretnine";

    @DatabaseField(columnName = FIELD_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = FIELD_SLIKA)
    private String mSLika;


    @DatabaseField(columnName = FIELD_NAME,foreign = true,foreignAutoRefresh = true)
    private Nekretnine mNekretnine;


    public Slike() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmSLika() {
        return mSLika;
    }

    public void setmSLika(String mSLika) {
        this.mSLika = mSLika;
    }

    public Nekretnine getmNekretnine() {
        return mNekretnine;
    }

    public void setmNekretnine(Nekretnine mNekretnine) {
        this.mNekretnine = mNekretnine;
    }
}
