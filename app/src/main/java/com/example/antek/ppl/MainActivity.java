package com.example.antek.ppl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.antek.ppl.model.Question;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String SF_LEFT_QUESTS_KEY = "SF_LEFT_QUESTS_KEY";
    private static final String SF_CURRENT_QUEST_KEY = "SF_CURRENT_QUEST_KEY";
    private static final String SF_FILE_LEFT_BY_USER_KEY = "SF_FILE_LEFT_BY_USER_KEY";
    private static final String SF_MISTAKES_KEY = "SF_MISTAKES_KEY";
    private static final String SF_RIGHTS_KEY = "SF_RIGHTS_KEY";
    private static final String DEFAULT_FILENAME = "procedury_2";
    private static final String QUESTION_LEFT_LIST = "1";
    private static final String QUESTION_LIST = "2";
    private static final String MISTAKES = "3";
    private static final String RIGHTS = "4";
    private static final String GOOD_CHECKED_FLAG = "5";
    private static final String INDEXES = "6";
    private static final String CURRENT_QUEST = "7";
    private static final int DELAY_TIME = 700;


    View badAnswerCover;
    TextView[] answers;
    TextView quest;
    TextView answ1;
    TextView answ2;
    TextView answ3;
    TextView answ4;
    TextView leftQuestions;
    CheckBox checkBox;
    Button reset;
    Spinner spinner;

    Random random = new Random();
    Handler handler = new Handler();

    ArrayList<Integer> questionsLeftList;
    ArrayList<Question> questionList;
    //-1 is used to indicate no more questions are left
    int currentQuest;
    int[] indexes;
    String currentFile;
    //good checked flag is used to block clicks when user answered right and is waiting for next question that is postDelayed by handler
    boolean goodCheckedFlag;
    int mistakes;
    int rightAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            questionList = new ArrayList<>();
            indexes = new int[4];
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            String fileToLoad = sharedPreferences.getString(SF_FILE_LEFT_BY_USER_KEY,DEFAULT_FILENAME);
            Helpers.readFromFileToList(questionList, fileToLoad, this);
            currentFile = fileToLoad;
            bindViews();
            if(sharedPreferences.contains(fileToLoad + SF_LEFT_QUESTS_KEY)){
                String leftQuestListSerialized = sharedPreferences.getString(fileToLoad + SF_LEFT_QUESTS_KEY,"");
                questionsLeftList = Helpers.parseIntegerList(leftQuestListSerialized);
            } else {
                questionsLeftList = initializeLeftQuestionsSystem();
            }
            currentQuest = sharedPreferences.getInt(fileToLoad + SF_CURRENT_QUEST_KEY, generateNextQuestionNumber());
            mistakes = sharedPreferences.getInt(currentFile + SF_MISTAKES_KEY, 0);
            rightAnswers = sharedPreferences.getInt(currentFile + SF_RIGHTS_KEY, 0);
            updateCounters();
            loadNthQuestion(currentQuest);
        } else {
            questionList = savedInstanceState.getParcelableArrayList(QUESTION_LIST);
            indexes = savedInstanceState.getIntArray(INDEXES);
            questionsLeftList = savedInstanceState.getIntegerArrayList(QUESTION_LEFT_LIST);
            mistakes = savedInstanceState.getInt(MISTAKES);
            rightAnswers = savedInstanceState.getInt(RIGHTS);
            goodCheckedFlag = savedInstanceState.getBoolean(GOOD_CHECKED_FLAG);
            currentQuest = savedInstanceState.getInt(CURRENT_QUEST);
            bindViews();
            loadNthQuestion(currentQuest);
            updateCounters();
        }
    }

    private ArrayList<Integer> initializeLeftQuestionsSystem() {
        ArrayList<Integer> newQuestionsLeftList = new ArrayList<>();
        for (int i = 0; i < questionList.size(); i++) {
            newQuestionsLeftList.add(i);
        }
        return newQuestionsLeftList;
    }

    private int generateNextQuestionNumber() {
        if (!checkBox.isChecked()) {
            int number = random.nextInt(questionList.size());
            return number;
        } else {
            if (questionsLeftList.size() > 0) {
                int index = random.nextInt(questionsLeftList.size());
                int number = questionsLeftList.get(index);
                return number;
            }
        }
        return -1;
    }

    private void updateCounters() {
        if (checkBox.isChecked()) {
            if (mistakes + rightAnswers > 0) {
                float percents = (rightAnswers / (float) (mistakes + rightAnswers)) * 100;
                BigDecimal bd = new BigDecimal(Float.toString(percents));
                bd = bd.round(new MathContext(4, RoundingMode.HALF_UP));
                String percentsString = bd.toString();
                if(bd.floatValue() >= 75.00){
                    setTextViewGoodAnswered(leftQuestions);
                } else{
                    setTextViewWrongAnswered(leftQuestions);
                }
                leftQuestions.setText("Pozostało pytań: " + questionsLeftList.size() + " | " + rightAnswers + "/" + (mistakes + rightAnswers) + " | " + percentsString + "%");
            } else {
                setTextViewNormalState(leftQuestions);
                leftQuestions.setText("Pozostało pytań: " + questionsLeftList.size() + " | " + 0 + "/" + 0);
            }
        } else {
            if (mistakes + rightAnswers > 0) {
                float percents = (rightAnswers / (float) (mistakes + rightAnswers)) * 100;
                BigDecimal bd = new BigDecimal(Float.toString(percents));
                bd = bd.round(new MathContext(4, RoundingMode.HALF_UP));
                String percentsString = bd.toString();
                if(bd.floatValue() >= 75.00){
                    setTextViewGoodAnswered(leftQuestions);
                } else{
                    setTextViewWrongAnswered(leftQuestions);
                }
                leftQuestions.setText(rightAnswers + "/" + (mistakes + rightAnswers) + " | " + percentsString + "%");
            } else {
                setTextViewNormalState(leftQuestions);
                leftQuestions.setText(0 + "/" + 0);
            }
        }
    }

    private void loadNthQuestion(int n) {
        if (n != -1) {
            Question question = questionList.get(n);
            indexes = new int[4];
            ArrayList lotteryNumbersLeft = new ArrayList<>(Arrays.asList(new Integer[]{0, 1, 2, 3}));

            for (int i = 0; i < 4; i++) {
                int indexOfLotteryNumberArrayDrawn = random.nextInt(4 - i);
                indexes[i] = (int) lotteryNumbersLeft.get(indexOfLotteryNumberArrayDrawn);
                lotteryNumbersLeft.remove(indexOfLotteryNumberArrayDrawn);
            }

            answers[indexes[0]].setText(question.goodAnswer);
            answers[indexes[1]].setText(question.answ2);
            answers[indexes[2]].setText(question.answ3);
            answers[indexes[3]].setText(question.answ4);
            quest.setText(question.question);

        } else {
            Toast.makeText(this, "Nie ma więcej pytań, użyj guzika \"RESET\" loadNthQuestion", Toast.LENGTH_SHORT).show();
        }
    }


    private void bindViews() {
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.topics, R.layout.view_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //trick to prevent onItemSelected being called when app is started
        spinner.setSelection(searchFileNameIndex(currentFile),false);

        spinner.setOnItemSelectedListener(this);
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionsLeftList = initializeLeftQuestionsSystem();
                mistakes = 0;
                rightAnswers = 0;
                currentQuest = generateNextQuestionNumber();
                loadNthQuestion(currentQuest);
                updateCounters();
            }
        });
        leftQuestions = (TextView) findViewById(R.id.left_questions);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    questionsLeftList = initializeLeftQuestionsSystem();
                    updateCounters();
                }else{
                    updateCounters();
                }
            }
        });
        quest = (TextView) findViewById(R.id.question);
        answ1 = (TextView) findViewById(R.id.answ1);
        answ2 = (TextView) findViewById(R.id.answ2);
        answ3 = (TextView) findViewById(R.id.answ3);
        answ4 = (TextView) findViewById(R.id.answ4);
        badAnswerCover = findViewById(R.id.cover);
        answers = new TextView[]{answ1, answ2, answ3, answ4};
        for (int i = 0; i < answers.length; i++) {
            final int finalI = i;
            answers[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        answerClicked(finalI, ((TextView) v).getText());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void answerClicked(final int textViewClickedIndex, CharSequence text) throws InterruptedException {
        //if no next question has been found and -1 has not been assigned
        if (!(currentQuest == -1)) {
            questionsLeftList.remove(Integer.valueOf(currentQuest));
            if (text.equals(questionList.get(currentQuest).goodAnswer)) {
                //to prevent from double counting good answers
                if (!goodCheckedFlag) {
                    goodCheckedFlag = true;
                    rightAnswers++;
                    updateCounters();
                    setTextViewGoodAnswered(answers[textViewClickedIndex]);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuest = generateNextQuestionNumber();
                            loadNthQuestion(currentQuest);
                            for (TextView textView : answers) {
                                setTextViewNormalState(textView);
                            }
                            goodCheckedFlag = false;
                        }
                    }, DELAY_TIME);
                }
            } else {
                //to prevent for starting bad checked behaviour when good was checked and user is waiting for next question and accidently clicks sth
                if (!goodCheckedFlag) {
                    reset.setClickable(false);
                    mistakes++;
                    updateCounters();
                    setTextViewGoodAnswered(answers[indexes[0]]);
                    setTextViewWrongAnswered(answers[textViewClickedIndex]);
                    badAnswerCover.setVisibility(View.VISIBLE);
                    badAnswerCover.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (TextView textView : answers) {
                                setTextViewNormalState(textView);
                            }
                            currentQuest = generateNextQuestionNumber();
                            loadNthQuestion(currentQuest);
                            v.setVisibility(View.INVISIBLE);
                            reset.setClickable(true);
                            v.setOnClickListener(null);
                        }
                    });
                }
            }

        } else {
            Toast.makeText(this, "Nie ma więcej pytań, użyj guzika \"RESET\" answerClicked", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(QUESTION_LEFT_LIST, questionsLeftList);
        outState.putParcelableArrayList(QUESTION_LIST, questionList);
        outState.putInt(MISTAKES, mistakes);
        outState.putInt(RIGHTS, rightAnswers);
        outState.putBoolean(GOOD_CHECKED_FLAG, goodCheckedFlag);
        outState.putIntArray(INDEXES, indexes);
        outState.putInt(CURRENT_QUEST, currentQuest);
    }

    private void setTextViewWrongAnswered(TextView tv) {
        tv.setBackgroundColor(ContextCompat.getColor(this, R.color.bad_answer));
    }

    private void setTextViewGoodAnswered(TextView tv) {
        tv.setBackgroundColor(ContextCompat.getColor(this, R.color.good_answer));
    }

    private void setTextViewNormalState(TextView tv) {
        tv.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //save state on previous topic
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(currentFile + SF_CURRENT_QUEST_KEY, currentQuest);
        String serializedList = questionsLeftList.toString();
        serializedList = serializedList.substring(1, serializedList.length()-1);
        editor.putString(currentFile + SF_LEFT_QUESTS_KEY, serializedList);
        editor.putInt(currentFile + SF_RIGHTS_KEY, rightAnswers);
        editor.putInt(currentFile + SF_MISTAKES_KEY, mistakes);
        editor.apply();

        String[] topics = getResources().getStringArray(R.array.topics_filenames);
        currentFile = topics[position];
        questionList = new ArrayList<>();
        Helpers.readFromFileToList(questionList, currentFile, this);
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        if(sharedPreferences.contains(currentFile + SF_LEFT_QUESTS_KEY)){
            String leftQuestListSerialized = sharedPreferences.getString(currentFile + SF_LEFT_QUESTS_KEY,"");
            questionsLeftList = Helpers.parseIntegerList(leftQuestListSerialized);
        } else {
            questionsLeftList = initializeLeftQuestionsSystem();
        }
        currentQuest = sharedPreferences.getInt(currentFile + SF_CURRENT_QUEST_KEY, generateNextQuestionNumber());
        mistakes = sharedPreferences.getInt(currentFile + SF_MISTAKES_KEY, 0);
        rightAnswers = sharedPreferences.getInt(currentFile + SF_RIGHTS_KEY, 0);
        updateCounters();
        loadNthQuestion(currentQuest);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "on nothing selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SF_FILE_LEFT_BY_USER_KEY, currentFile);
        editor.putInt(currentFile + SF_CURRENT_QUEST_KEY, currentQuest);
        String serializedList = questionsLeftList.toString();
        serializedList = serializedList.substring(1, serializedList.length()-1);
        editor.putString(currentFile + SF_LEFT_QUESTS_KEY, serializedList);
        editor.putInt(currentFile + SF_RIGHTS_KEY, rightAnswers);
        editor.putInt(currentFile + SF_MISTAKES_KEY, mistakes);
        editor.apply();
    }

    private int searchFileNameIndex(String filename){
        String[] topics = getResources().getStringArray(R.array.topics_filenames);
        for(int i = 0; i < topics.length; i++ ){
            if(topics[i].equals(filename))
                return i;
        }
        return  -1;
    }
}
