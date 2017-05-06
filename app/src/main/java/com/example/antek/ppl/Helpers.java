package com.example.antek.ppl;

import android.content.Context;
import android.util.Log;

import com.example.antek.ppl.model.Question;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Antek on 2017-04-30.
 */

class Helpers {


    static void readFromFileToList(ArrayList<Question> list, String fileName, Context context) {
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_PRIVATE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                String[] parts = line.split(";");
                Question question = new Question();
                question.question = parts[0];
                question.goodAnswer = parts[1];
                question.answ2 = parts[2];
                question.answ3 = parts[3];
                question.answ4 = parts[4];
                list.add(question);
                Log.v("ccccc", line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
    }


    static ArrayList<Integer> parseIntegerList(String list) {
        ArrayList<Integer> listToBeReturned = new ArrayList<>();
        String[] temp = list.split(", ");
        if (temp[0].equals("")) {
            return listToBeReturned;
        } else {
            for (String s : temp) {
                listToBeReturned.add(Integer.parseInt(s));
            }
        }
        return listToBeReturned;
    }

}
