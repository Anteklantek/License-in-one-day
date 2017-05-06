package com.example.antek.ppl.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Antek on 2017-04-29.
 */

public class Question implements Parcelable {

    public String question;
    public String goodAnswer;
    public String answ2;
    public String answ3;
    public String answ4;

    public Question() {
    }

    protected Question(Parcel in) {
        question = in.readString();
        goodAnswer = in.readString();
        answ2 = in.readString();
        answ3 = in.readString();
        answ4 = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(goodAnswer);
        dest.writeString(answ2);
        dest.writeString(answ3);
        dest.writeString(answ4);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}