package net.sgztech.timeboat.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DD extends RecyclerView.Adapter<DD.DialViewHolder> {


    @NonNull
    @Override
    public DialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DialViewHolder holder, int position) {

    }



    @Override
    public int getItemCount() {
        return 0;
    }

    class DialViewHolder extends RecyclerView.ViewHolder{

        public DialViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
