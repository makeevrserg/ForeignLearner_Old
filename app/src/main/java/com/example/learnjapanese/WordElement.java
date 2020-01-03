package com.example.learnjapanese;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WordElement {
    public String word;
    public String transcription;
    public ArrayList<String> translation;
    public ArrayList<String> tags;
    public int progressBar;



    public WordElement(String word, String transcription, ArrayList<String> translation, ArrayList<String> tags, int progressBar) {
        this.word = word;
        this.transcription = transcription;
        this.translation = translation;
        this.tags = tags;
        this.progressBar=progressBar;
    }

    public void Print(){
        System.out.println("Word="+word);
        System.out.println("Transcription="+transcription);
        System.out.println("Translation="+translation);
        System.out.println("Tags="+tags);
    }
    public WordElement(String word, String transcription, JSONArray translation, JSONArray tags, String progress) {
        this.word = word;
        this.transcription = transcription;
        this.translation = new ArrayList<>();
        this.progressBar=Integer.parseInt(progress);


        this.translation = new ArrayList<>();
        for(int i =0;i<translation.length();++i){
            try {
                this.translation.add(translation.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        this.tags = new ArrayList<>();
        for(int i =0;i<tags.length();++i){
            try {
                this.tags.add(tags.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public WordElement() {
        this.word = "";
        transcription = "";
        this.translation = new ArrayList<>();
        this.tags = new ArrayList<>();
    }
}
