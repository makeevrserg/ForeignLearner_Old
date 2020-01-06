package com.example.learnjapanese;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learnjapanese.Adapters.Options;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ActivityMatchWords extends AppCompatActivity{

    private static final String TAG ="ActivityMatchWords";
    Options mOptions;
    ProgressBar progressBar;
    ImageView imageViewTranscription;
    boolean isTranscriptionShowed = true;
    private int[] wordPosition;
    private int progressBarProgress=0;
    private boolean[] isLeftClickable;
    private boolean[] isRightClickable;
    private int[] colors;
    LinearLayout linearLayoutWords;
    LinearLayout linearLayoutTranslation;

    Button[] buttonsWords;
    Button[] buttonsTrasnaltion;

    int currentStage;
    ArrayList<ArrayList<WordElement>> wordStages;
    int countStages;
    int sizeOfStage;

    int colorEndIncorrect = 0xFFc72c41;
    int colorDefault = 0xFF393e49;
    int colorEndCorrect = 0xFF91bd3a;
    int colorSelectedButton = 0xFFfe6845;
    int buttonTextColor=0xFFEEEEEE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(this);
        super.onCreate(savedInstanceState);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.buttoncolor, typedValue, true);
        colorDefault = typedValue.data;
        theme.resolveAttribute(R.attr.buttoncolorcorrect, typedValue, true);
        colorEndCorrect = typedValue.data;
        theme.resolveAttribute(R.attr.buttoncolorincorrect, typedValue, true);
        colorEndIncorrect = typedValue.data;
        theme.resolveAttribute(R.attr.buttoncolorselected, typedValue, true);
        colorSelectedButton = typedValue.data;

        theme.resolveAttribute(R.attr.buttontextcolor, typedValue, true);
        buttonTextColor = typedValue.data;

        Slidr.attach(this);
        setContentView(R.layout.activity_match_words);
        progressBar = findViewById(R.id.progressBarMatchWords);
        imageViewTranscription = findViewById(R.id.imageViewShowTranscriptionMatchWords);
        linearLayoutTranslation=findViewById(R.id.linearLayoutTranslation);
        linearLayoutWords=findViewById(R.id.linearLayoutWords);
        mOptions=new Options(this);
        mOptions.LoadJsonSettings();
        mOptions.LoadJsonWords();
        isTranscriptionShowed=mOptions.isTranscriptionShowed;
        CreateStages();
        Initialize();

    }

    public void CreateStages(){
        wordStages = new ArrayList<>();

        Log.d(TAG, "CreateStages: wordPerTime="+mOptions.showWordsMatchWordsCount);
        if (mOptions.mWordsToLearn.size()>=mOptions.showWordsMatchWordsCount)
            countStages=mOptions.mWordsToLearn.size()/mOptions.showWordsMatchWordsCount;
        else {
            countStages = 0;
        }

        for(int i =0;i<countStages+1;++i)
            wordStages.add(new ArrayList<WordElement>());
        currentStage=0;
        progressBar.setProgress(0);
        int st=0;
        for(int i =0;i<mOptions.mWordsToLearn.size();++i){

            wordStages.get(st).add(mOptions.mWordsToLearn.get(i));
            if ((i+1)>0 && (i+1)%mOptions.showWordsMatchWordsCount==0)
                st++;
        }
    }

    public void Initialize(){
        sizeOfStage=wordStages.get(currentStage).size();
        //wordStages.set(currentStage,new ArrayList<WordElement>());

        linearLayoutWords.removeAllViews();
        linearLayoutTranslation.removeAllViews();
        imageViewTranscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTranscriptionShowed = !isTranscriptionShowed;
                if (!isTranscriptionShowed) {
                    for(int i =0;i<sizeOfStage;++i)
                        buttonsWords[i].setText(mWords.get(i));
                } else {
                    for(int i =0;i<sizeOfStage;++i){
                        String tr = mTranscription.get(i).replace("[","").replace("]","");
                        if (!tr.isEmpty())
                            tr="\n"+"["+tr+"]";
                        buttonsWords[i].setText(mWords.get(i)+tr);
                    }
                }
                mOptions.isTranscriptionShowed=isTranscriptionShowed;
                mOptions.SaveShowedTranscription();
            }
        });

        wordPosition = new int[sizeOfStage];
        isLeftClickable = new boolean[sizeOfStage];
        isRightClickable = new boolean[sizeOfStage];
        mWords = new ArrayList<>();
        mTranscription=new ArrayList<>();
        mTranslation=new ArrayList<>();
        Collections.shuffle(wordStages.get(currentStage));
        ArrayList<Integer> a = new ArrayList<>();
        for(int i =0;i<sizeOfStage;++i){
            a.add(i);
        }
        Collections.shuffle(a);
        for(int i =0;i<sizeOfStage;++i) {
            wordPosition[i] = a.get(i);
            isLeftClickable[i] = true;
            isRightClickable[i] = true;
            mWords.add(wordStages.get(currentStage).get(i).word);
            mTranscription.add(wordStages.get(currentStage).get(i).transcription.replace("[","").replace("]",""));
            mTranslation.add(wordStages.get(currentStage).get(i).translation.toString().replace("[","").replace("]",""));

        }
        Log.d(TAG, "wordPosition= "+ Arrays.toString(wordPosition));
        progressBar.setMax(mOptions.mWordsToLearn.size());
        buttonsTrasnaltion= new Button[sizeOfStage];
        buttonsWords= new Button[sizeOfStage];

        Log.d(TAG, Arrays.toString(wordPosition));
        for(int i =0;i<sizeOfStage;++i){
            buttonsWords[i] = new Button(this);
            buttonsTrasnaltion[i] = new Button(this);
        }

        for(int i =0;i<sizeOfStage;++i){
            if (isTranscriptionShowed)
                CreateButton(linearLayoutWords,buttonsWords[i],mWords.get(i),mTranscription.get(i).replace("[","").replace("]",""));
            else
                CreateButton(linearLayoutWords,buttonsWords[i],mWords.get(i),"");

            CreateButton(linearLayoutTranslation,buttonsTrasnaltion[i],"","");
            buttonsTrasnaltion[i].setSingleLine(true);

            final int finalI = i;
            buttonsWords[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButtonClicked(view,buttonsWords[finalI],finalI,0);
                }
            });
            buttonsTrasnaltion[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButtonClicked(view,buttonsTrasnaltion[finalI],finalI,1);
                }
            });
        }
        for(int i =0;i<sizeOfStage;++i){
            buttonsTrasnaltion[wordPosition[i]].setText(mTranslation.get(i).replace("[","").replace("]",""));
        }
    }

    int oldClick=-1;
    int oldSide=-1;
    View oldView;
    boolean allowedToClick=true;
    public void ButtonClicked(View view,Button btn,int position,int side){
        if ((side==0 && !isLeftClickable[position])||(side==1 && !isRightClickable[position]))
            return;
        if (!allowedToClick)
            return;
        if (oldClick==-1){
            oldClick=position;
            oldView=view;
            onTapChangeColor(view);
            oldSide=side;
            Log.d(TAG, "Case1: ");
        }else if(side==oldSide && oldClick==position){
            onResetColor(oldView);
            oldClick=-1;
            Log.d(TAG, "Case2: ");
        }else if (oldSide==side){
            onResetColor(oldView);
            onTapChangeColor(view);
            oldView=view;
            oldClick=position;
            Log.d(TAG, "Case3: ");
        }else if(side!=oldSide){
            Log.d(TAG, "Case4: oldClick="+oldClick+";position="+position+";wordPosition[oldClick]="+wordPosition[oldClick]+";wordPosition[position]="+wordPosition[position]);
            if (side==0){
                if (wordPosition[position]==oldClick){
                    onCorrectAnswerAnimation(view);
                    onCorrectAnswerAnimation(oldView);
                    isLeftClickable[position]=false;
                    isRightClickable[oldClick]=false;
                    progressBarProgress++;
                    progressBar.setProgress(progressBarProgress);
                }else{
                    onIncorrectAnswerAnimation(oldView);
                    onIncorrectAnswerAnimation(view);
                }
                Log.d(TAG, "Case4_1: ");
            }else if(side==1){
                if (wordPosition[oldClick]==position){
                    onCorrectAnswerAnimation(view);
                    onCorrectAnswerAnimation(oldView);
                    progressBarProgress++;
                    progressBar.setProgress(progressBarProgress);
                    isRightClickable[position]=false;
                    isLeftClickable[oldClick]=false;
                }else{
                    onIncorrectAnswerAnimation(oldView);
                    onIncorrectAnswerAnimation(view);
                }
                Log.d(TAG, "Case4_2: ");
            }
            oldView=null;
            oldSide=-1;
            oldClick=-1;
            if (progressBarProgress>=mOptions.mWordsToLearn.size())

                onCompleteAlert();
            if (progressBarProgress!=0 && (progressBarProgress)%mOptions.showWordsMatchWordsCount==0) {
                currentStage++;
                Initialize();
            }
        }
    }



    private void OnResetViewAnimation(){

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.my_fade_out);
        for(int i =0;i<buttonsTrasnaltion.length;++i){
            buttonsTrasnaltion[i].startAnimation(animation);
            buttonsWords[i].startAnimation(animation);
        }

        progressBarProgress = 0;
        progressBar.setProgress(progressBarProgress);
        CreateStages();
        Initialize();

        animation = AnimationUtils.loadAnimation(this, R.anim.my_fade_in);
        for(int i =0;i<buttonsTrasnaltion.length;++i){
            buttonsTrasnaltion[i].startAnimation(animation);
            buttonsWords[i].startAnimation(animation);
        }
    }
    private void onCompleteAlert() {

        if (mOptions.boolInfRepeat) {
            OnResetViewAnimation();
            return;
        }
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ActivityMatchWords.this);
            builder.setTitle("Начать тренировку заново?")
                    .setCancelable(false)
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressBarProgress = 0;
                            progressBar.setProgress(progressBarProgress);
                            CreateStages();
                            Initialize();

                        }
                    });
            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();

    }


    private void CreateButton(LinearLayout linearLayout, final Button btn, String word, String transcription){
        btn.setBackgroundColor(colorDefault);
        if (!transcription.isEmpty())
            transcription="\n["+transcription+"]";
        btn.setText(word+transcription);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(5,5,5,5);
        btn.setPadding(5,5,5,5);
        btn.setLayoutParams(params);
        btn.setTextColor(buttonTextColor);
        btn.setLines(2);
        btn.setAllCaps(false);
        btn.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        btn.setMarqueeRepeatLimit(-1);
        btn.setSelected(true);
        linearLayout.addView(btn);
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(ActivityMatchWords.this)
                        .setTitle("Выбранное слово")
                        .setMessage(btn.getText())
                        .create().show();
                return false;
            }
        });
    }

    private ArrayList<String> mWords;
    private ArrayList<String> mTranscription;
    private ArrayList<String> mTranslation;


    private void onResetColor(View view){

        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorSelectedButton, colorDefault);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }


    private void onTapChangeColor(View view) {

        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorDefault, colorSelectedButton);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();

    }

    private void onIncorrectAnswerAnimation(final View view) {
        //int colorStart = 0xFF393e49;
        allowedToClick=false;
        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorDefault, colorEndIncorrect);
        colorAnim.setDuration(600);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(2);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {


                ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                        "backgroundColor", colorEndIncorrect, colorDefault);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.setRepeatMode(ValueAnimator.REVERSE);
                colorAnim.start();
                allowedToClick=true;
                //onResetColor(view);
            }
        }, 1200);
    }


    private void onCorrectAnswerAnimation(View view) {

        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorDefault, colorEndCorrect);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

}
