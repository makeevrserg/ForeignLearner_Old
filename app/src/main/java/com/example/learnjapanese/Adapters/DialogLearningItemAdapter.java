package com.example.learnjapanese.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnjapanese.R;

import java.util.ArrayList;

public class DialogLearningItemAdapter extends RecyclerView.Adapter<DialogLearningItemAdapter.DialogItemViewHolder> {
    private static final String TAG="DialogLearningAdapter";

    private ArrayList<Integer> mItemImage;
    private ArrayList<String> mItemOption;
    private String mItemWordCount;
    OnNoteListener mOnNoteListener;
    public interface OnNoteListener{
        void onNoteClick(int position);
    }


    public DialogLearningItemAdapter(ArrayList<Integer> mItemImage, ArrayList<String> mItemOption, String mItemWordCount,OnNoteListener onNoteListener) {
        this.mItemWordCount = mItemWordCount;
        this.mItemOption = mItemOption;
        this.mItemImage = mItemImage;
        mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public DialogItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.learningoptions_item,parent,false);
        return new DialogItemViewHolder(view,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final DialogItemViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHodler:called");

        holder.textViewWordCount.setText(mItemWordCount);
        holder.textViewOption.setText(mItemOption.get(position));
        holder.imageView.setImageResource(mItemImage.get(position));
//        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG,"onClick: clicked on "+position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mItemImage.size();
    }

    public class DialogItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewOption;
        TextView textViewWordCount;
        ImageView imageView;
        RelativeLayout relativeLayout;
        OnNoteListener onNoteListener;

        DialogItemViewHolder(View itemView,OnNoteListener onNoteListener){
            super(itemView);
            textViewOption =itemView.findViewById(R.id.textViewOption);
            textViewWordCount=itemView.findViewById(R.id.textViewWordCount);
            imageView =itemView.findViewById(R.id.imageView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            System.out.println("Clicked==="+getAdapterPosition());
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }
}
