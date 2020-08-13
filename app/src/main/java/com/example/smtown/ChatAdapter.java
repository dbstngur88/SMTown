package com.example.smtown;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context context;
    ArrayList<Chat> arrayChat;
    String strNickname;


    public ChatAdapter(Context context, ArrayList<Chat> arrayChat, String strNickname) {
        this.context = context;
        this.arrayChat = arrayChat;
        this.strNickname = strNickname;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_chat,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout.LayoutParams prmContent = (LinearLayout.LayoutParams)holder.txtContent.getLayoutParams();
        LinearLayout.LayoutParams prmDate = (LinearLayout.LayoutParams)holder.txtDate.getLayoutParams();
        LinearLayout.LayoutParams prmEmail = (LinearLayout.LayoutParams)holder.txtEmail.getLayoutParams();

        String nickName = arrayChat.get(position).getNickName();
        holder.txtContent.setTextColor(Color.BLACK);
        prmContent.gravity= Gravity.LEFT;
        prmDate.gravity= Gravity.LEFT;
        prmEmail.gravity= Gravity.LEFT;
        holder.txtEmail.setVisibility(View.VISIBLE);
        holder.txtEmail.setTextColor(Color.BLUE);
        holder.txtDate.setTextColor(Color.GREEN);

        holder.txtDate.setText(arrayChat.get(position).getWdate());
        holder.txtContent.setText(arrayChat.get(position).getContent());
        holder.txtEmail.setText(arrayChat.get(position).getNickName());
    }

    @Override
    public int getItemCount() {
        return arrayChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtContent, txtDate, txtEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContent=itemView.findViewById(R.id.txtContent);
            txtDate=itemView.findViewById(R.id.txtDate);
            txtEmail=itemView.findViewById(R.id.txtEmail);
        }
    }
}