package com.telemedicine.matrika.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.model.chat.Message;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.extensions.DateExtensions;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context       mContext;
    private List<Message>       messages = new ArrayList<>();
    private OnMessageListener   mOnMessageListener;

    public ChatMessageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setMessages(List<Message> messages) {
        if(messages == null || messages.isEmpty()) {
            this.messages = new ArrayList<>();
            notifyDataSetChanged();
        }
        else {
            this.messages = messages;
            notifyDataSetChanged();
            if(mOnMessageListener != null) mOnMessageListener.notifyMessage(messages.size() - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderRole().equals(LocalStorage.USER.getRole()) ? 0 : 1;
    }

    private Message message(int position){
        return messages.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            default:
            case 0: return new SenderViewHolder(layoutInflater.inflate(R.layout.sample_chat_sender_message, parent, false));
            case 1: return new ReceiverViewHolder(layoutInflater.inflate(R.layout.sample_chat_receiver_message, parent, false));
        }
    }

    private static class SenderViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView   message;
        private final CardView            filesHolder;
        private final RecyclerView        rcvFiles;
        private FileAdapter               fileAdapter;
        private final AppCompatTextView   sentAt;
        private final View                divider;

        private SenderViewHolder(View v) {
            super(v);
            message = v.findViewById(R.id.sender_Message_Tv);
            filesHolder = v.findViewById(R.id.sender_Files_Layout);
            rcvFiles = v.findViewById(R.id.sender_Files_Rcv);
            sentAt = v.findViewById(R.id.sender_SentAt_Tv);
            divider = v.findViewById(R.id.sender_Divider_View);
            setAdapter();
        }

        private void setAdapter(){
            fileAdapter = new FileAdapter();
            rcvFiles.setHasFixedSize(true);
            rcvFiles.setAdapter(fileAdapter);
        }
    }

    private static class ReceiverViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView   message;
        private final CardView            filesHolder;
        private final RecyclerView        rcvFiles;
        private FileAdapter               fileAdapter;
        private final AppCompatTextView   sentAt;
        private final View                divider;

        private ReceiverViewHolder(View v) {
            super(v);
            message = v.findViewById(R.id.receiver_Message_Tv);
            filesHolder = v.findViewById(R.id.receiver_Files_Layout);
            rcvFiles = v.findViewById(R.id.receiver_Files_Rcv);
            sentAt = v.findViewById(R.id.receiver_SentAt_Tv);
            divider = v.findViewById(R.id.receiver_Divider_View);
            setAdapter();
        }

        private void setAdapter(){
            fileAdapter = new FileAdapter();
            rcvFiles.setHasFixedSize(true);
            rcvFiles.setAdapter(fileAdapter);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int pos) {
        if(holder instanceof SenderViewHolder){
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;

            senderViewHolder.divider.setVisibility(isUserSentMultipleMessage(pos, message(pos).getSenderRole()) ? View.GONE : View.VISIBLE);

            senderViewHolder.message.setVisibility(message(pos).getMessage() != null ? View.VISIBLE : View.GONE);
            senderViewHolder.message.setText(message(pos).getMessage());

            if(message(pos).getFiles() != null && !message(pos).getFiles().isEmpty()){
                senderViewHolder.filesHolder.setVisibility(View.VISIBLE);
                senderViewHolder.rcvFiles.setLayoutManager(new GridLayoutManager(mContext, message(pos).getFiles().size() > 1 ? (message(pos).getFiles().size() > 4 ? 3 : 2) : 1));
                senderViewHolder.fileAdapter.setFiles(message(pos).getFiles());
            }
            else senderViewHolder.filesHolder.setVisibility(View.GONE);

            senderViewHolder.sentAt.setText(new DateExtensions(message(pos).getSentAt().getTime()).defaultTimeFormat());

            senderViewHolder.itemView.setOnClickListener(v ->
                    senderViewHolder.sentAt.setVisibility(senderViewHolder.sentAt.getVisibility() == View.GONE ? View.VISIBLE : View.GONE)
            );
        }
        else {
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;

            receiverViewHolder.divider.setVisibility(isUserSentMultipleMessage(pos, message(pos).getSenderRole()) ? View.GONE : View.VISIBLE);

            receiverViewHolder.message.setVisibility(message(pos).getMessage() != null ? View.VISIBLE : View.GONE);
            receiverViewHolder.message.setText(message(pos).getMessage());

            if(message(pos).getFiles() != null && !message(pos).getFiles().isEmpty()){
                receiverViewHolder.filesHolder.setVisibility(View.VISIBLE);
                receiverViewHolder.rcvFiles.setLayoutManager(new GridLayoutManager(mContext, message(pos).getFiles().size() > 1 ? (message(pos).getFiles().size() > 4 ? 3 : 2) : 1));
                receiverViewHolder.fileAdapter.setFiles(message(pos).getFiles());
            }
            else receiverViewHolder.filesHolder.setVisibility(View.GONE);

            receiverViewHolder.sentAt.setText(new DateExtensions(message(pos).getSentAt().getTime()).defaultTimeFormat());

            receiverViewHolder.itemView.setOnClickListener(v ->
                    receiverViewHolder.sentAt.setVisibility(receiverViewHolder.sentAt.getVisibility() == View.GONE ? View.VISIBLE : View.GONE)
            );
        }
    }

    public boolean isUserSentMultipleMessage(int position, String id){
        return (position + 1) == getItemCount() || message(position + 1).getSenderRole().equals(id);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.mOnMessageListener = onMessageListener;
    }

    public interface OnMessageListener {
        void notifyMessage(int itemCount);
    }
}
