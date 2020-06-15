package petarkitanovic.androidkurs.nekretnine;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import petarkitanovic.androidkurs.nekretnine.db.Slike;

public class SlikaAdapter extends RecyclerView.Adapter<SlikaAdapter.MyViewHolder> {

    private OnItemClickListener listener;
    private List<Slike> slike;
    Context context;


    public SlikaAdapter(Context context, List<Slike> slike, OnItemClickListener listener) {
        this.context = context;
        this.slike = slike;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slika_adapter, parent, false);

        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Uri mUri = Uri.parse(slike.get(position).getmSLika());
        holder.ivMalaSlika.setImageURI(mUri);
    }

    @Override
    public int getItemCount() {
        return slike.size();
    }

    public Slike get(int position) {
        return slike.get(position);
    }

    public void clear() {
        slike.clear();
    }

    public void addAll(List<Slike> slikeList) {
        slike.addAll(slikeList);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivMalaSlika;

        private OnItemClickListener vhListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener vhListener) {
            super(itemView);

            ivMalaSlika = itemView.findViewById(R.id.ivMalaSlika);
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
}