package petarkitanovic.androidkurs.nekretnine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import petarkitanovic.androidkurs.nekretnine.db.DatabaseHelper;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private DatabaseHelper databaseHelper;
    private OnItemClickListener listener;
    Context context;


    public MyAdapter(DatabaseHelper databaseHelper, OnItemClickListener listener) {
        this.databaseHelper = databaseHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);

        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {
            holder.tvNaziv.setText(getDatabaseHelper().getNekretnineDao().queryForAll().get(position).getmNaziv());
            holder.tvAdresa.setText("Adresa: " + getDatabaseHelper().getNekretnineDao().queryForAll().get(position).getmAdresa());
            holder.tvTelefon.setText("Telefon: " + getDatabaseHelper().getNekretnineDao().queryForAll().get(position).getmTelefon());
            holder.tvKvadratura.setText("Kvadratura: " + getDatabaseHelper().getNekretnineDao().queryForAll().get(position).getmKvadratura()+ "m2");
            holder.tvCena.setText("Cena: " + getDatabaseHelper().getNekretnineDao().queryForAll().get(position).getmCena() + "eur");


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        int list = 0;
        try {
            list = getDatabaseHelper().getNekretnineDao().queryForAll().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView tvNaziv;
        private TextView tvAdresa;
        private TextView tvTelefon;
        private TextView tvKvadratura;
        private TextView tvCena;

        private OnItemClickListener vhListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener vhListener) {
            super(itemView);

            tvNaziv = itemView.findViewById(R.id.tvNaziv);
            tvAdresa = itemView.findViewById(R.id.tvAdresa);
            tvTelefon = itemView.findViewById(R.id.tvBrojTelefona);
            tvKvadratura = itemView.findViewById(R.id.tvKvadratura);
            tvCena = itemView.findViewById(R.id.tvCena);
            this.vhListener = vhListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            vhListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {

            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
