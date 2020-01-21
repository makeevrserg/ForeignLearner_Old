package com.example.learnjapanese;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnjapanese.Adapters.DBHelper;
import com.r0adkll.slidr.Slidr;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ActivityImportExport extends AppCompatActivity {
    private TextView textViewImportWords;
    private Spinner spinnerFiles;
    private String selectedFile="";
    private String jsonTextData;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String JSONDATA = "JsonData";


    public  String SELECTED_LANGUAGE="JAPANESE";
    public static String SHARED_PREFS_LANGUAGE="LANGUAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = Objects.requireNonNull(this).getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        SELECTED_LANGUAGE=sharedPreferences.getString(SHARED_PREFS_LANGUAGE,"JAPANESE");
        Toast.makeText(this, "Now Language "+SELECTED_LANGUAGE, Toast.LENGTH_SHORT).show();

        ThemeSettings themeSettings = new ThemeSettings(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);
        Slidr.attach(this);
        textViewImportWords = findViewById(R.id.textViewImportWords);
        spinnerFiles = findViewById(R.id.spinnerJsonFiles);
        ApplyFilesData();
    }

    private void ApplyFilesData(){
        List<String> filesList = GetReadableFiles();
        selectedFile = filesList.get(0);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiles.setAdapter(adapter);
        spinnerFiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFile = adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(view.getContext(), "Selected"+itemValue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        textViewImportWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedFile.contains(".csv")){
                    //CsvToSqlite();
                    //Toast.makeText(view.getContext(), "Selected csv " + selectedFile, Toast.LENGTH_SHORT).show();
                } else if (selectedFile.contains(".xlsx")){
                    XLSToSqlite(DBHelper.TABLE_WORD,"JAPANESE");
                    //Toast.makeText(view.getContext(), "Selected xlsx " + selectedFile, Toast.LENGTH_SHORT).show();
                }
                //saveData();
            }
        });
    }

    private ProgressDialog progressDialog;
    private void ShowProgressDialog(){
        progressDialog = new ProgressDialog(ActivityImportExport.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Идет загрузка слов");
        progressDialog.setMessage("Пожалуйста подождите");
        progressDialog.show();

    }




    private void XLSToSqlite(final String TABLE, final String LANGUAGE) {

        ShowProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                dbHelper = new DBHelper(ActivityImportExport.this);
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.delete(TABLE, null, null);
                ContentValues contentValues = new ContentValues();

                File inputFile = new File(Environment.getExternalStorageDirectory() + "/LearnJapanese", selectedFile);
                XSSFWorkbook workbook = null;

                try {
                    workbook = new XSSFWorkbook(new FileInputStream(inputFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (workbook==null)
                    return;

                XSSFSheet myExcelSheet = workbook.getSheet(LANGUAGE);
                final int rows = myExcelSheet.getPhysicalNumberOfRows();
                for (int i = 2; i < rows; ++i) {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage("Идет загрузка слов\n" + finalI + "/" + rows);

                        }
                    });
                    Row cRow = myExcelSheet.getRow(i);

                    String progress = cRow.getCell(0).getStringCellValue();
                    String[] tagsAr;
                    tagsAr = (cRow.getCell(1,Row.RETURN_BLANK_AS_NULL)==null)? new String[]{""} :cRow.getCell(1,Row.RETURN_BLANK_AS_NULL).getStringCellValue().split(";");
                    String word = cRow.getCell(2).getStringCellValue();
                    String transcription = (cRow.getCell(3,Row.RETURN_BLANK_AS_NULL)==null)?"":cRow.getCell(3,Row.RETURN_BLANK_AS_NULL).getStringCellValue();
                    String[] translationAr = cRow.getCell(4).getStringCellValue().split(";");
                    String translation = "[";
                    String tags = "[";
                    for (int k = 0; k < translationAr.length; ++k)
                        if (k != translationAr.length - 1)
                            translation += ("\"" + translationAr[k] + "\",");
                        else
                            translation += ("\"" + translationAr[k] + "\"]");


                    for (int k = 0; k < tagsAr.length; ++k)
                        if (k != tagsAr.length - 1)
                            tags += ("\"" + tagsAr[k] + "\",");
                        else
                            tags += ("\"" + tagsAr[k] + "\"]");

                    contentValues.put(DBHelper.KEY_WORD, word);
                    contentValues.put(DBHelper.KEY_TRANSCRIPTION, transcription);
                    contentValues.put(DBHelper.KEY_TRANSLATION, translation);
                    contentValues.put(DBHelper.KEY_TAGS, tags);
                    //Log.d(TAG, "run: "+tags);
                    contentValues.put(DBHelper.KEY_PROGRESS, progress.substring(0, progress.length() - 1));
                    database.insert(TABLE, null, contentValues);
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                dbHelper.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        progressDialog.cancel();
                        if (LANGUAGE!="ENGLISH")
                            XLSToSqlite(DBHelper.TABLE_WORD_ENGLISH,"ENGLISH");

                    }
                });
            }
        }).start();
    }

    DBHelper dbHelper;
    private void CsvToSqlite() {
        final ArrayList<String> csvText = GetFileText();

        Log.d(TAG, "CsvToSqlite: Loading");
        ShowProgressDialog();




        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                dbHelper = new DBHelper(ActivityImportExport.this);
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.delete(DBHelper.TABLE_WORD,null,null);
                ContentValues contentValues = new ContentValues();
                for (int i = 0; i < csvText.size(); ++i) {
                    progressDialog.setMessage("Идет загрузка слов\n"+i+"/"+csvText.size());
                    String mLine = csvText.get(i);
                    String[] line = mLine.split(";");
                    String[] translationAr = line[4].split("/");
                    String[] tagsAr = line[1].split("/");

                    String translation="[";
                    String tags="[";
                    for(int k =0;k<translationAr.length;++k)
                        if (k!=translationAr.length-1)
                            translation+=("\""+translationAr[k]+"\",");
                        else
                            translation+=("\""+translationAr[k]+"\"]");



                    for(int k =0;k<tagsAr.length;++k)
                        if (k!=tagsAr.length-1)
                            tags+=("\""+tagsAr[k]+"\",");
                        else
                            tags+=("\""+tagsAr[k]+"\"]");

                    contentValues.put(DBHelper.KEY_WORD,line[2]);
                    contentValues.put(DBHelper.KEY_TRANSCRIPTION,line[3]);
                    contentValues.put(DBHelper.KEY_TRANSLATION, translation);
                    contentValues.put(DBHelper.KEY_TAGS, tags);
                    contentValues.put(DBHelper.KEY_PROGRESS, line[0].substring(0, line[0].length() - 1));
                    database.insert(DBHelper.TABLE_WORD,null,contentValues);

                }
                Log.d(TAG, "CsvToSqlite: Loaded!");
                Cursor cursor = database.query(DBHelper.TABLE_WORD,
                        null,null,null,null,null,null);


                if (cursor.moveToFirst()){
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                    int wordIndex = cursor.getColumnIndex(DBHelper.KEY_WORD);
                    int transcriptionIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSCRIPTION);
                    int translationIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSLATION);
                    int tagsIndex = cursor.getColumnIndex(DBHelper.KEY_TAGS);
                    int progressIndex = cursor.getColumnIndex(DBHelper.KEY_PROGRESS);
                    do{
                        Log.d(TAG, "TableText=: "
                                +"ID="+cursor.getInt(idIndex)
                                +",word="+cursor.getString(wordIndex)
                                +",transcription="+cursor.getString(transcriptionIndex)
                                +",translation="+cursor.getString(translationIndex)
                                +",tags="+cursor.getString(tagsIndex)
                                +",progress="+cursor.getString(progressIndex)+"\n");
                    } while (cursor.moveToNext());
                }else{
                    Log.d(TAG, "CsvToSqlite: NothingToShow");
                }
                cursor.close();
                dbHelper.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();
                        progressDialog.cancel();

                    }
                });
            }
        }).start();


    }

    public final static String TAG="ActivityImportExport";

    private List<String> GetReadableFiles(){
        File folder = new File(Environment.getExternalStorageDirectory()+"/LearnJapanese");
        ArrayList<String> fileNames = new ArrayList<>();
        for(final File f : Objects.requireNonNull(folder.listFiles())){
            if (f.isFile()){
                if(f.getName().matches(".*\\.csv") || f.getName().matches(".*\\.xlsx")){
                    fileNames.add(f.getName());
                }
            }
        }
        return fileNames;
    }
    private ArrayList<String> GetFileText(){
        ArrayList<String> sb = new ArrayList<>();
        File textFile = new File(Environment.getExternalStorageDirectory()+"/LearnJapanese",selectedFile);
        System.out.println("SelectedFile="+Environment.getExternalStorageDirectory()+"/LearnJapanese"+selectedFile);
        try {
            FileInputStream fis = new FileInputStream(textFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buff = new BufferedReader(isr);
            String line;
            while ((line=buff.readLine())!=null ){
                sb.add(line+'\n');
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return sb;
    }
}
