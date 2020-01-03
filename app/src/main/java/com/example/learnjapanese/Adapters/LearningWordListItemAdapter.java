package com.example.learnjapanese.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnjapanese.R;
import com.example.learnjapanese.WordElement;

import java.util.ArrayList;

public class LearningWordListItemAdapter extends RecyclerView.Adapter<LearningWordListItemAdapter.ViewHolder> implements Filterable {
    private static final String TAG="LearningWordItemAdapter";

    private ArrayList<String> mWord;
    private ArrayList<String> mTranscription;
    private ArrayList<ArrayList<String>> mTranslation;
    private ArrayList<ArrayList<String>> mTags;
    private ArrayList<Integer> mProgressBars;

    public ArrayList<WordElement> wordElementAll;
    OnNoteListener mOnNoteListener;
    public interface OnNoteListener{
        void onNoteClick(int position);
    }

    public LearningWordListItemAdapter(ArrayList<WordElement> wordElement, OnNoteListener onNoteListener) {
        mWord=new ArrayList<>();
        mTranscription=new ArrayList<>();
        mTranslation=new ArrayList<>();
        mTags=new ArrayList<>();
        mProgressBars=new ArrayList<>();
        for(int i=0;i<wordElement.size();++i){
            mWord.add(wordElement.get(i).word);
            mTranscription.add(wordElement.get(i).transcription);
            mTranslation.add(wordElement.get(i).translation);
            mTags.add(wordElement.get(i).tags);
            mProgressBars.add(wordElement.get(i).progressBar);
        }
        wordElementAll = new ArrayList<>();
        for(int i =0;i<mWord.size();++i){
            wordElementAll.add(new WordElement(mWord.get(i),
                    mTranscription.get(i),
                    mTranslation.get(i),
                    mTags.get(i),
                    mProgressBars.get(i)));
        }
        mOnNoteListener = onNoteListener;
        System.out.println("--------------------------");
        System.out.println(mWord+"\n"+mTranscription+"\n"+mTranslation+"\n"+mTags);
    }

//    public LearningWordListItemAdapter(ArrayList<String> word,ArrayList<String> transcription,ArrayList<String> translation,ArrayList<String> tags,ArrayList<Integer> progressBars) {
//        mWord=word;
//        mTranscription=transcription;
//        mTranslation=translation;
//        mTags=tags;
//        mProgressBars=progressBars;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_wordview,parent,false);
        return new ViewHolder(view,mOnNoteListener);
    }

    private String getStringFromList(ArrayList<String> list){
        StringBuilder stringBuilder = new StringBuilder();
        for( String line:list){
            stringBuilder.append(line).append(";");
        }
        return stringBuilder.toString();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHodler:called");

        holder.mWord.setText(mWord.get(position));
        holder.mTranscription.setText(mTranscription.get(position));
        holder.mTranslation.setText(getStringFromList(mTranslation.get(position)));
        holder.mTags.setText(getStringFromList(mTags.get(position)));
        holder.mProgressBars[0].setProgress(mProgressBars.get(position));
        holder.mProgressBars[1].setProgress(mProgressBars.get(position));
        holder.mProgressBars[2].setProgress(mProgressBars.get(position));

    }

    @Override
    public int getItemCount() {
        return mWord.size();
    }
    ArrayList<WordElement> filteredWords;
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredWords= new ArrayList<>();
            if (charSequence.toString().isEmpty()){
                filteredWords.addAll(wordElementAll);
            }else {
                for (WordElement word : wordElementAll) {
                    if (word.word.toLowerCase().contains(charSequence.toString().toLowerCase()) || word.transcription.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredWords.add(word);
                    } else {
                        boolean isFound = false;
                        for (String str : word.translation) {
                            if (str.toLowerCase().contains(charSequence)) {
                                filteredWords.add(word);
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound)
                            for (String str : word.tags) {
                                if (str.toLowerCase().contains(charSequence)) {
                                    filteredWords.add(word);
                                    break;
                                }
                            }
                    }
                }
            }
            //System.out.println(filteredWords);
            FilterResults filterResults = new FilterResults();
            filterResults.values=filteredWords;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mWord.clear();
            mTranscription.clear();
            mTranslation.clear();
            mTags.clear();
            mProgressBars.clear();
            for (WordElement word:filteredWords){
                mWord.add(word.word);
                mTranscription.add(word.transcription);
                mTranslation.add(word.translation);
                mTags.add(word.tags);
                mProgressBars.add(word.progressBar);
            }
            notifyDataSetChanged();
        }
    };
    @Override
    public Filter getFilter() {
        return filter;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mWord;
        TextView mTranscription;
        TextView mTranslation;
        TextView mTags;
        ProgressBar[] mProgressBars;
        RelativeLayout relativeLayout;
        OnNoteListener onNoteListener;

        ViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            mWord = itemView.findViewById(R.id.textViewWord);
            mTranscription = itemView.findViewById(R.id.textViewWordTranscription);
            mTranslation = itemView.findViewById(R.id.textViewWordTranslation);
            mTags = itemView.findViewById(R.id.textViewWordTag);
            mProgressBars = new ProgressBar[3];
            mProgressBars[0] = itemView.findViewById(R.id.progressBarSmall);
            mProgressBars[1] = itemView.findViewById(R.id.progressBarMedium);
            mProgressBars[2] = itemView.findViewById(R.id.progressBarHigh);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutWordView);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            System.out.println("Clicked===" + getAdapterPosition());
            int k = getAdapterPosition();
            if (filteredWords!=null)
            for (int i = 0; i < wordElementAll.size(); ++i) {
                if (wordElementAll.get(i).word == filteredWords.get(k).word) {
                    k = i;
                    break;
                }
            }

            onNoteListener.onNoteClick(k);
        }
    }

}
