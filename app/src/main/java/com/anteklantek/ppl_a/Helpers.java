package com.anteklantek.ppl_a;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.anteklantek.ppl_a.model.Question;

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
                String[] parts = line.split("@@");
                Question question = new Question();
                question.lp = Integer.parseInt(parts[0]);
                String[] parts2 = parts[1].split("@");
                question.code = parts2[0];
                question.question = parts2[1];
                question.goodAnswer = parts2[2];
                question.answ2 = parts2[3];
                question.answ3 = parts2[4];
                question.answ4 = parts2[5];
                list.add(question);
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

    static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

}
