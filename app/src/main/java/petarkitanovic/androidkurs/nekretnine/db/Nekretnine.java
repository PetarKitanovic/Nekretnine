package petarkitanovic.androidkurs.nekretnine.db;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Nekretnine.TABLE_NAME)
public class Nekretnine {

    public static final String TABLE_NAME = "nekretnine";

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAZIV = "naziv";
    public static final String FIELD_OPIS = "opis";
    public static final String FIELD_ADRESA = "adresa";
    public static final String FIELD_TELEFON = "telefon";
    public static final String FIELD_KVADRATURA = "kvadratura";
    public static final String FIELD_BROJ_SOBA = "broj_soba";
    public static final String FIELD_CENA = "cena";
    public static final String FIELD_SLIKE = "slike";


    @DatabaseField(columnName = FIELD_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = FIELD_NAZIV)
    private String mNaziv;

    @DatabaseField(columnName = FIELD_OPIS)
    private String mOpis;

    @DatabaseField(columnName = FIELD_ADRESA)
    private String mAdresa;

    @DatabaseField(columnName = FIELD_TELEFON)
    private String mTelefon;

    @DatabaseField(columnName = FIELD_KVADRATURA)
    private String mKvadratura;

    @DatabaseField(columnName = FIELD_BROJ_SOBA)
    private String mBrojSoba;

    @DatabaseField(columnName = FIELD_CENA)
    private String mCena;

    @ForeignCollectionField(columnName = FIELD_SLIKE, eager = true)
    private ForeignCollection<Slike> mSlike;


    public Nekretnine() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmNaziv() {
        return mNaziv;
    }

    public void setmNaziv(String mNaziv) {
        this.mNaziv = mNaziv;
    }

    public String getmOpis() {
        return mOpis;
    }

    public void setmOpis(String mOpis) {
        this.mOpis = mOpis;
    }

    public String getmAdresa() {
        return mAdresa;
    }

    public void setmAdresa(String mAdresa) {
        this.mAdresa = mAdresa;
    }

    public String getmTelefon() {
        return mTelefon;
    }

    public void setmTelefon(String mTelefon) {
        this.mTelefon = mTelefon;
    }

    public String getmKvadratura() {
        return mKvadratura;
    }

    public void setmKvadratura(String mKvadratura) {
        this.mKvadratura = mKvadratura;
    }

    public String getmBrojSoba() {
        return mBrojSoba;
    }

    public void setmBrojSoba(String mBrojSoba) {
        this.mBrojSoba = mBrojSoba;
    }

    public String getmCena() {
        return mCena;
    }

    public void setmCena(String mCena) {
        this.mCena = mCena;
    }

    public ForeignCollection<Slike> getmSlike() {
        return mSlike;
    }

    public void setmSlike(ForeignCollection<Slike> mSlike) {
        this.mSlike = mSlike;
    }

    @Override
    public String toString() {
        return mNaziv;
    }
}
