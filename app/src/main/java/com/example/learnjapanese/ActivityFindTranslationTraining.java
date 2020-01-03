package com.example.learnjapanese;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnjapanese.Adapters.Options;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ActivityFindTranslationTraining extends AppCompatActivity {

    Options mOptions;
    Button[] buttonAnswer;
    TextView textViewWord;
    TextView textViewWordTranscription;
    ImageView imageViewShowTranscription;
    ProgressBar progressBar;
    private Context mContext;
    boolean isTranscriptionShowed;
    boolean infRepeat;
    boolean allowedToClick = true;
    int wordCount = -1;
    int currentWordCount;
    int TRAINING_TYPE;
    public static String TAG = "TrainingFindWord";
    TextToSpeech textToSpeech;
    boolean isAnswerCorrect;
    int colorStartIncorrect = 0xFF393e49;
    int colorEndIncorrect = 0xFFc72c41;

    int colorStartCorrect = 0xFF393e49;
    int colorEndCorrect = 0xFF91bd3a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(this);
        super.onCreate(savedInstanceState);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.buttoncolor, typedValue, true);
        colorStartIncorrect = typedValue.data;
        colorStartCorrect = typedValue.data;
        theme.resolveAttribute(R.attr.buttoncolorcorrect, typedValue, true);
        colorEndCorrect = typedValue.data;
        theme.resolveAttribute(R.attr.buttoncolorincorrect, typedValue, true);
        colorEndIncorrect = typedValue.data;



        Intent intent = getIntent();
        TRAINING_TYPE = intent.getIntExtra("Type", 0);
        Log.d(TAG, "onCreate: TYPE_TRAINING=" + TRAINING_TYPE);
        Slidr.attach(this);
        mContext = this;
        setContentView(R.layout.activity_find_translation_training);
        mOptions = new Options(this);
        mOptions.LoadJsonSettings();
        mOptions.LoadJsonWords();
        isTranscriptionShowed=mOptions.isTranscriptionShowed;
        //Log.d(TAG, "onCreate: "+mOptions.mWordsToLearn.toString());
        buttonAnswer = new Button[4];
        buttonAnswer[0] = findViewById(R.id.buttonAnswer1);
        buttonAnswer[1] = findViewById(R.id.buttonAnswer2);
        buttonAnswer[2] = findViewById(R.id.buttonAnswer3);
        buttonAnswer[3] = findViewById(R.id.buttonAnswer4);

        for (int i = 0; i < 4; ++i) {
            final int finalI = i;
            buttonAnswer[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new androidx.appcompat.app.AlertDialog.Builder(ActivityFindTranslationTraining.this)
                            .setTitle("Выбранное слово")
                            .setMessage(buttonAnswer[finalI].getText())
                            .create().show();
                    return false;
                }
            });
        }


        if (TRAINING_TYPE == 1)
            for (int i = 0; i < 4; ++i) {
                buttonAnswer[i].setTextSize(20);
            }
        textViewWord = findViewById(R.id.textViewWord);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.JAPAN);
                }else{
                    Toast.makeText(mContext, "Synth ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textToSpeech.isSpeaking())
                    textToSpeech.speak(textViewWord.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });
        textViewWordTranscription = findViewById(R.id.textViewTranscription);
        textViewWordTranscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textToSpeech.isSpeaking())
                    textToSpeech.speak(textViewWordTranscription.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });
        imageViewShowTranscription = findViewById(R.id.imageViewShowTranscription);
        progressBar = findViewById(R.id.progressBar);

        imageViewShowTranscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTranscriptionShowed = !isTranscriptionShowed;
                if (!isTranscriptionShowed) {
                    textViewWordTranscription.setVisibility(TextView.INVISIBLE);
                    if (TRAINING_TYPE == 1) {
                        for (int i = 0; i < 4; ++i) {
                            buttonAnswer[i].setText(mOptions.mWordsToLearn.get(randCount[i]).word);
                        }
                    }
                } else {
                    if (TRAINING_TYPE == 1) {
                        for (int i = 0; i < 4; ++i) {
                            String transcription = "";
                            if (isTranscriptionShowed) {
                                if (!mOptions.mWordsToLearn.get(randCount[i]).transcription.isEmpty())
                                    transcription = "[" + mOptions.mWordsToLearn.get(randCount[i]).transcription + "]";
                            }
                            buttonAnswer[i].setText(mOptions.mWordsToLearn.get(randCount[i]).word + transcription);
                        }
                    }
                    textViewWordTranscription.setVisibility(TextView.VISIBLE);
//                    textViewWordTranscription.setText("["+correctWord.transcription+"]");
                }
                mOptions.isTranscriptionShowed=isTranscriptionShowed;
                mOptions.SaveShowedTranscription();
            }
        });
        infRepeat = mOptions.boolInfRepeat;
        progressBar.setMax(mOptions.mWordsToLearn.size());
        progressBar.setProgress(0);
        wordCount = mOptions.mWordsToLearn.size();

        currentWordCount = 0;
        Collections.shuffle(mOptions.mWordsToLearn);


        UpdateTask();

    }

    int intCorrectBtnAnswer;
    WordElement correctWord;
    int[] randCount;

    public void UpdateTask() {
        isAnswerCorrect=true;
        for (WordElement word : mOptions.mWordsToLearn)
            Log.d(TAG, "UpdateTask: Words=" + word.word + '\n');


        intCorrectBtnAnswer = (int) (Math.random() * 4);
        //intCorrectWord = (int) (Math.random() * mOptions.mWordsToLearn.size());

        correctWord = mOptions.mWordsToLearn.get(currentWordCount);

        if (TRAINING_TYPE == 0)
            textViewWord.setText(correctWord.word);
        else if (TRAINING_TYPE == 1)
            textViewWord.setText(correctWord.translation.toString());


        if (TRAINING_TYPE == 0) {
            if (!correctWord.transcription.isEmpty())
                textViewWordTranscription.setText("[" + correctWord.transcription + "]");
            else
                textViewWordTranscription.setText("");

            if (isTranscriptionShowed)
                textViewWordTranscription.setSelected(true);
            else
                textViewWordTranscription.setVisibility(TextView.INVISIBLE);

        } else if (TRAINING_TYPE == 1)
            textViewWordTranscription.setText("");

        randCount = new int[4];
        ArrayList<Integer> a = new ArrayList<>(mOptions.mWordsToLearn.size());
        for (int i = 0; i < mOptions.mWordsToLearn.size(); i++) {
            a.add(i);
        }
        Collections.shuffle(a);
        boolean hasCorrectAnswer = false;
        for (int i = 0; i < 4; ++i) {
            if (a.get(i) == currentWordCount) {
                intCorrectBtnAnswer = i;
                hasCorrectAnswer = true;
            }
            randCount[i] = a.get(i);
        }

        if (!hasCorrectAnswer)
            randCount[intCorrectBtnAnswer] = currentWordCount;


        for (int i = 0; i < 4; i++) {
            buttonAnswer[i].setBackgroundColor(colorStartCorrect);
            String translation = "\n";


            if (TRAINING_TYPE == 0) {
                for (String str : mOptions.mWordsToLearn.get(randCount[i]).translation) {
                    translation = translation + str + "; ";
                }
                buttonAnswer[i].setText(translation);
            } else if (TRAINING_TYPE == 1) {
                String transcription = "";
                if (isTranscriptionShowed) {
                    if (!mOptions.mWordsToLearn.get(randCount[i]).transcription.isEmpty())
                        transcription = "[" + mOptions.mWordsToLearn.get(randCount[i]).transcription + "]";
                }
                buttonAnswer[i].setText(mOptions.mWordsToLearn.get(randCount[i]).word + transcription);
            }

            buttonAnswer[i].setEllipsize(TextUtils.TruncateAt.MARQUEE);
            buttonAnswer[i].setSingleLine(true);
            buttonAnswer[i].setMarqueeRepeatLimit(-1);
            buttonAnswer[i].setSelected(true);
            final int currentBtn = i;
            buttonAnswer[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!allowedToClick)
                        return;
                    if (currentBtn == intCorrectBtnAnswer) {
                        if (isAnswerCorrect) {
                            currentWordCount++;
                            progressBar.setProgress(currentWordCount);
                        }
                        if (currentWordCount >= wordCount) {
                            if (!infRepeat) {

                                //Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.my_fade_in);
//                                    if(isTranscriptionShowed){
//                                        textViewWordTranscription.startAnimation(animation);
//                                    }
                                onCorrectAnswerAnimation(view);
                                onPostDelayedAnimation(view, false);
                                onCompleteAlert();

                                return;
                            } else {
                                Collections.shuffle(mOptions.mWordsToLearn);
                                currentWordCount = 0;
                                progressBar.setProgress(currentWordCount);
                                onCorrectAnswerAnimation(view);
                                onPostDelayedAnimation(view, true);
                            }

                        }


                        onCorrectAnswerAnimation(view);


                        allowedToClick = false;

                        onPostDelayedAnimation(view, true);


                    } else {
                        isAnswerCorrect=false;
                        onIncorrectAnswerAnimation(view);
                    }
                }
            });
        }
    }


    private void onPostDelayedAnimation(View view, final boolean goUpdate) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                allowedToClick = true;
                if (goUpdate)
                    UpdateTask();
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.my_fade_in);
                if (isTranscriptionShowed) {
                    textViewWordTranscription.startAnimation(animation);
                }
                textViewWord.startAnimation(animation);
                for (int i = 0; i < 4; i++)
                    buttonAnswer[i].startAnimation(animation);

            }
        }, 300);
    }


    private void onIncorrectAnswerAnimation(View view) {




        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorStartIncorrect, colorEndIncorrect);
        colorAnim.setDuration(600);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }


    private void onCorrectAnswerAnimation(View view) {

        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", colorStartCorrect, colorEndCorrect);
        colorAnim.setDuration(600);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.my_fade_out);
        if (isTranscriptionShowed)
            textViewWordTranscription.startAnimation(animation);
        textViewWord.startAnimation(animation);
        for (int i = 0; i < 4; i++)
            buttonAnswer[i].startAnimation(animation);
    }

    private void onCompleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFindTranslationTraining.this);

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
                        currentWordCount = 0;
                        progressBar.setProgress(0);

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
