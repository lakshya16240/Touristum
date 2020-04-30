package com.teamcool.touristum.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamcool.touristum.Activities.ChatAcivity;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Chat;
import com.teamcool.touristum.data.model.Message;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AllChatsAdapter extends RecyclerView.Adapter<AllChatsAdapter.ChatViewHolder> {

    private Context context;
    private ArrayList<Chat> chats;
    private onChatClickListener chatClickListener;
    private onChatLongClickListener chatLongClickListener;
    private int VIEW_MODE;

    public interface onChatClickListener{
        void selectedChat(Chat chat);
    }
    public interface onChatLongClickListener{
        void selectedChat(Chat chat);
    }

    public AllChatsAdapter(Context context,onChatClickListener chatClickListener,onChatLongClickListener chatLongClickListener) {
        this.context = context;
        this.chatClickListener = chatClickListener;
        this.chatLongClickListener = chatLongClickListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        if(VIEW_MODE == ChatAcivity.VIEW_MODE_CLIENT)
            holder.tv_name.setText("Talking to : " + chats.get(position).getEmployeeName());
        else if(VIEW_MODE == ChatAcivity.VIEW_MODE_EMPLOYEE)
            holder.tv_name.setText("Talking to : " + chats.get(position).getClientName());

        ArrayList<Message> messages = chats.get(position).getMessages();
        holder.tv_message.setText(messages.get(messages.size() - 1).getMessage());

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public void setVIEW_MODE(int VIEW_MODE) {
        this.VIEW_MODE = VIEW_MODE;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_message;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_chatName);
            tv_message = itemView.findViewById(R.id.tv_message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatClickListener.selectedChat(chats.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    chatLongClickListener.selectedChat(chats.get(getAdapterPosition()));
                    return true;
                }
            });

        }
    }
}
