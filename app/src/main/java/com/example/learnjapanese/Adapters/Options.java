package com.example.learnjapanese.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.learnjapanese.WordElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Options {
    public int showWordsMatchWordsCount=8;
    public boolean boolInfRepeat;
    public boolean boolShowLearnedWords;
    public boolean isTranscriptionShowed=false;
    private String stringFirstToShow;
    private ArrayList<String> arStrTagList;
    public String[] ArrayStringOfAllTags;
    public boolean[] boolCheckedTags;
    public ArrayList<WordElement> mWordElement;
    public ArrayList<WordElement> mWordsToLearn;
    private String jsonData;
    private static final String JSONDATA = "JsonData";
    private static final String JSON_WORDS_TO_LEARN_ENGLISH = "JSON_WORDS_TO_LEARN_ENGLISH";
    private static final String JSON_WORDS_TO_LEARN_JAPANESE = "JSON_WORDS_TO_LEARN_JAPANESE";
    private static final String JSONSETTINGS = "JsonSettings";
    private static final String SHARED_PREFS = "sharedPrefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public int countSelectedWods;
    private FragmentActivity fragmentActivity;
    private static String TAG="Options";
    public Options(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }




    private void LoadWordsToLearn() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.fragmentActivity).getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String jsonStr;
        if (SELECTED_LANGUAGE=="ENGLISH")
            jsonStr= sharedPreferences.getString(JSON_WORDS_TO_LEARN_ENGLISH, "None");
        else
            jsonStr= sharedPreferences.getString(JSON_WORDS_TO_LEARN_JAPANESE, "None");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray==null)
            return;
        JSONObject jsonObject;
        mWordsToLearn=new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                jsonObject = (jsonArray.getJSONObject(i));
                mWordsToLearn.add(new WordElement(jsonObject.getString("Word"),
                        jsonObject.getString("Transcription"),
                        jsonObject.getJSONArray("Translation"),
                        jsonObject.getJSONArray("Tags"),
                        jsonObject.getString("Progress"))
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void SaveWordsToLearn(){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for(WordElement word:mWordsToLearn){
            JSONArray jsonArrayTranslation = new JSONArray();
            jsonObject = new JSONObject();
            for(String line: word.translation){
                jsonArrayTranslation.put(line);
            }
            JSONArray jsonArrayTags = new JSONArray();
            for(String line: word.tags){
                jsonArrayTags.put(line);
            }

            try {
                jsonObject.put("Word",word.word);
                jsonObject.put("Transcription",word.transcription);
                jsonObject.put("Progress",word.progressBar);
                jsonObject.put("Translation",jsonArrayTranslation);
                jsonObject.put("Tags",jsonArrayTags);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);
        }

        SharedPreferences sharedPreferences = Objects.requireNonNull(this.fragmentActivity).getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        if (SELECTED_LANGUAGE=="ENGLISH")
            editor.putString(JSON_WORDS_TO_LEARN_ENGLISH,jsonArray.toString());
        else
            editor.putString(JSON_WORDS_TO_LEARN_JAPANESE,jsonArray.toString());
        editor.apply();
    }

    public void LoadJsonWords(){
        LoadWordsFromMySqlite();
        LoadWordsToLearn();
    }
    public int CountWords(ArrayList<String> mTagName) {
        int count = 0;
        mWordsToLearn= new ArrayList<>();
        for (WordElement word : mWordElement) {
            boolean alreadyContains=false;
            for(String tag:mTagName) {
                boolean isCont=false;
                for(String wordTag:word.tags) {
                    if (wordTag.replaceAll(" ", "").
                            equals(tag.replaceAll(" ", "")))
                        isCont = true;
                }
                if (isCont && !alreadyContains) {
                    alreadyContains=true;
                    count++;
                    mWordsToLearn.add(word);
                }
            }
        }
        countSelectedWods=count;
        SaveWordsToLearn();
        return count;
    }

    public  String SELECTED_LANGUAGE="JAPANESE";
    public static String SHARED_PREFS_LANGUAGE="LANGUAGE";
    public static String ENGLISH="ENGLISH";
    public static String JAPANESE="JAPANESE";
    public void LoadJsonSettings() {
        CreateSharedPrefs();
        boolInfRepeat = sharedPreferences.getBoolean("boolInfRepeat", false);
        boolShowLearnedWords=sharedPreferences.getBoolean("boolShowLearnedWords", false);
        isTranscriptionShowed=sharedPreferences.getBoolean("isTranscriptionShowed", false);

        SELECTED_LANGUAGE=sharedPreferences.getString(SHARED_PREFS_LANGUAGE,"JAPANESE");
        countSelectedWods=SELECTED_LANGUAGE==ENGLISH?sharedPreferences.getInt("countSelectedWords_ENGLISH", 0):sharedPreferences.getInt("countSelectedWords_JAPANESE", 0);
        showWordsMatchWordsCount=sharedPreferences.getInt("showWordsMatchWordsCount", 8);
        Log.d(TAG, "LoadJsonSettings: Loaded Language="+SELECTED_LANGUAGE+"; Wordscount="+countSelectedWods);
        String StrboolCheckedTags;

        if (SELECTED_LANGUAGE=="ENGLISH")
            StrboolCheckedTags=sharedPreferences.getString("boolCheckedTags_ENGLISH", "");
        else
            StrboolCheckedTags=sharedPreferences.getString("boolCheckedTags_JAPANESE", "");

        try {
            JSONArray jsonarBoolChecked = new JSONArray(StrboolCheckedTags);
            if (jsonarBoolChecked.length() > 0) {
                boolCheckedTags = new boolean[jsonarBoolChecked.length()];
                for (int i = 0; i < jsonarBoolChecked.length(); ++i)
                    boolCheckedTags[i] = jsonarBoolChecked.getBoolean(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("CommitPrefEdits")
    private void CreateSharedPrefs(){
         sharedPreferences = Objects.requireNonNull(this.fragmentActivity).getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
         editor = sharedPreferences.edit();
    }
    public void SaveShowedTranscription(){
        editor.putBoolean("isTranscriptionShowed",isTranscriptionShowed);
        editor.apply();
    }
    public void SaveJsonSettings(){
        CreateSharedPrefs();
        editor.putBoolean("boolInfRepeat",boolInfRepeat);
        editor.putString("stringFirstToShow",stringFirstToShow);
        editor.putBoolean("boolShowLearnedWords",boolShowLearnedWords);
        Log.d(TAG, "SaveJsonSettings: SaveCountWords="+countSelectedWods);
        editor.putInt(SELECTED_LANGUAGE==ENGLISH?"countSelectedWords_ENGLISH":"countSelectedWords_JAPANESE",countSelectedWods);
        editor.putInt("showWordsMatchWordsCount",showWordsMatchWordsCount);
        JSONArray jsonArrayBoolTag= new JSONArray();
        if (boolCheckedTags!=null) {
            for (boolean bool : boolCheckedTags) {
                jsonArrayBoolTag.put(bool);

            }
            if (SELECTED_LANGUAGE=="ENGLISH")
                editor.putString("boolCheckedTags_ENGLISH", jsonArrayBoolTag.toString());
            else
                editor.putString("boolCheckedTags_JAPANESE", jsonArrayBoolTag.toString());
        }
        editor.apply();
    }


    private void LoadWordsFromMySqlite() {
        DBHelper dbHelper = new DBHelper(fragmentActivity);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor;

        if (SELECTED_LANGUAGE.equals(ENGLISH))
            cursor = database.query(DBHelper.TABLE_WORD_ENGLISH,
                    null, null, null, null, null, null);
        else
            cursor = database.query(DBHelper.TABLE_WORD,
                    null, null, null, null, null, null);


        mWordElement = new ArrayList<>();
        if (cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int wordIndex = cursor.getColumnIndex(DBHelper.KEY_WORD);
            int transcriptionIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSCRIPTION);
            int translationIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSLATION);
            int tagsIndex = cursor.getColumnIndex(DBHelper.KEY_TAGS);
            int progressIndex = cursor.getColumnIndex(DBHelper.KEY_PROGRESS);
            do{
                try {
                    JSONArray translation = new JSONArray(cursor.getString(translationIndex));
                    JSONArray tags = new JSONArray(cursor.getString(tagsIndex));
                    WordElement wordElement = new WordElement(
                            cursor.getString(wordIndex),
                            cursor.getString(transcriptionIndex),
                            translation,
                            tags,
                            cursor.getString(progressIndex));
                    mWordElement.add(wordElement);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        CreateAllTags();
    }

    private void CreateAllTags(){
        ArrayList<String> ListOfAllTags = new ArrayList<>();
        for(WordElement word:mWordElement) {
            for(String tag:word.tags){
                //tag= tag.replaceAll(" ","");
                if (!ListOfAllTags.contains(tag))
                    ListOfAllTags.add(tag);
            }
        }
        ArrayStringOfAllTags = new String[ListOfAllTags.size()];
        Log.d(TAG, "LoadWordsFromMySqlite: "+ Arrays.toString(ArrayStringOfAllTags));
        for(int i =0;i<ListOfAllTags.size();++i)
            ArrayStringOfAllTags[i]=ListOfAllTags.get(i);
        Arrays.sort(ArrayStringOfAllTags);
        boolCheckedTags = new boolean[ListOfAllTags.size()];
    }


}
