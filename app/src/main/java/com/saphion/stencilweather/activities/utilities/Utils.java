package com.saphion.stencilweather.activities.utilities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sachinshinde.wordlearner.R;
import com.sachinshinde.wordlearner.module.Meaning;
import com.sachinshinde.wordlearner.module.SubWords;
import com.sachinshinde.wordlearner.module.Word;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by sachin on 21/10/14.
 */
public class Utils {

    public static final String WordsFile = "WordList";
    public static final String SessionFile = "Session";
    public static final String FINAL_URL_SINGLE = "http://my-dictionary-api.appspot.com/getMeaning?word=";
    public static final String FINAL_URL_MULTIPLE = "http://my-dictionary-api.appspot.com/getMultiMeaning";
    public static final String INTENT_ADD_WORD = "action.wordlearner.ADDWORD";
    public static final String INTENT_DELETE_WORD = "action.wordlearner.DELETEWORD";
    public static final String INTENT_REFRESH = "action.wordlearner.REFRESHWORD";
    public static final String WORDS_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "WordLearner";

    public static void writeListToFile(ArrayList<String> list, String FileName) {

        Set<String> set = new TreeSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);


        if (null == FileName)
            throw new RuntimeException("FileName is null!");

        File mDir = new File(Environment.getExternalStorageDirectory().getPath() + "/WordLearner");
        if (!mDir.exists()) {
            mDir.mkdirs();
        }

        File mFile = new File(mDir.getPath() + "/" + FileName);
        try {
            if (mFile.exists() || mFile.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(mFile);//mContext.openFileOutput(FileName, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(list);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<String> loadListFromFile(String FileName) {
        if (null == FileName)
            return null;
        File mDir = new File(Environment.getExternalStorageDirectory().getPath() + "/WordLearner");
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        ArrayList<String> list = new ArrayList<String>();
        File mFile = new File(mDir.getPath() + "/" + FileName);
        try {
            if (mFile.exists()) {
                FileInputStream fis = new FileInputStream(mFile);//mContext.openFileInput(FileName);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (ArrayList<String>) ois.readObject();
                fis.close();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Set<String> set = new TreeSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);

        return list;
    }

    public static String jsonToWordNikString(String jsonString) {
        String string = "";
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                string = string + (i + 1) + ". " + jsonArray.getJSONObject(i).getString("text") + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("WordLearner", string);
        return string;
    }

//    public static final String URL1 = "http://api.wordnik.com/v4/word.json/";
//    public static final String URL2 = "/definitions?limit=200&includeRelated=true&useCanonical=false&includeTags=false&api_key="+Constants.API_KEY;

    public static boolean hasWord(String word) {
        word = word.trim().toLowerCase(Locale.getDefault());
        File mDir = new File(Environment.getExternalStorageDirectory().getPath() + "/WordLearner/Words");
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        File mFile = new File(mDir.getPath() + "/" + word.trim().toLowerCase(Locale.getDefault()));
        return mFile.exists();
    }

    public static boolean deleteWord(String word) {
        word = word.trim().toLowerCase(Locale.getDefault());
        File mDir = new File(Environment.getExternalStorageDirectory().getPath() + "/WordLearner/Words");
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        File mFile = new File(mDir.getPath() + "/" + word.trim().toLowerCase(Locale.getDefault()));
        return mFile.delete();
    }

    public static String getDefinition(String word) {
        word = word.trim().toLowerCase(Locale.getDefault());
        File mDir = new File(Environment.getExternalStorageDirectory().getPath() + "/WordLearner/Words");
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        File mFile = new File(mDir.getPath() + "/" + word);
        String definition = "";
        try {
            if (mFile.exists()) {
                FileInputStream fis = new FileInputStream(mFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                definition = (String) ois.readObject();
                fis.close();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return definition;
    }

    public static String getWordJSON(String word) {
        word = word.trim().toLowerCase(Locale.getDefault());
        File mDir = new File(Environment.getExternalStorageDirectory().getPath() + "/WordLearner/Words");
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        File mFile = new File(mDir.getPath() + "/" + word);
        String definition = "";
        try {
            if (mFile.exists()) {
//                FileInputStream fis = new FileInputStream(mFile);
//                ObjectInputStream ois = new ObjectInputStream(fis);
//                definition = (String) ois.readObject();
//                fis.close();
                BufferedReader reader = new BufferedReader(new FileReader(mFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    definition += line;
                }
                reader.close();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return definition;
    }

    public static String saveWord(String word, String definition) {
        word = word.trim().toLowerCase(Locale.getDefault());
        File mDir = new File(Environment.getExternalStorageDirectory().getPath() + "/WordLearner/Words");
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        File mFile = new File(mDir.getPath() + "/" + word);
        try {

            if (mFile.exists() || mFile.createNewFile()) {
                FileWriter fileWriter =
                        new FileWriter(mFile);

                // Always wrap FileWriter in BufferedWriter.
                BufferedWriter bufferedWriter =
                        new BufferedWriter(fileWriter);
                bufferedWriter.write(definition);
//                FileOutputStream fos = new FileOutputStream(mFile);
//                ObjectOutputStream oos = new ObjectOutputStream(fos);
//                oos.writeObject(definition);
//                fos.close();
                bufferedWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return definition;
    }


    public static String[] romans= {"i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x"};

    private static String convertListToString(ArrayList<String> examples, String separator, boolean index) {

        String result = "";
        for(int i = 0 ; i < examples.size() ; i++){
            result = result + (index?("<b>" + romans[i] + ".</b> "):"") + examples.get(i).toUpperCase(Locale.US).charAt(0) + examples.get(i).substring(1) + separator;
        }
        return result;

    }



    public static int dpToPx(int i, Context mContext) {

        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        return (int) ((i * displayMetrics.density) + 0.5);

    }



    public static String getTime(long date, String form) {
        final SimpleDateFormat format = new SimpleDateFormat(form, Locale.US);
        format.setTimeZone(TimeZone.getDefault());
        Date now = new Date(date);

        return format.format(now);

    }

    public static void deleteFile(File file)
            throws IOException {

        if (file.isDirectory()) {

            //directory is empty, then delete it
            if (file.list().length == 0) {

                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());

            } else {

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    deleteFile(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }

        } else {
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }




    public static Bitmap loader(Context mContext) {

        int mLevel = 5;

        Bitmap circleBitmap = Bitmap.createBitmap(
                (int) (dpToPx(180, mContext)),
                (int) (dpToPx(180, mContext)),
                Bitmap.Config.ARGB_8888);

        Bitmap cfoSize;

        cfoSize = Bitmap.createBitmap(
                (int) dpToPx(180, mContext),
                dpToPx(180, mContext), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setAlpha(220);
        paint.setAntiAlias(true);

        Canvas c = new Canvas(circleBitmap);

        Paint mypaint = new Paint();
        mypaint.setAntiAlias(true);

        mypaint.setStrokeWidth((float) (cfoSize.getWidth() * 0.0253));

        mypaint.setStyle(Paint.Style.STROKE);
        mypaint.setAntiAlias(true);

        mypaint.setColor(0xffffffff);

        float left = (float) (cfoSize.getWidth() * 0.05);
        float top = (float) (cfoSize.getWidth() * 0.05);
        float right = cfoSize.getWidth() - (float) (cfoSize.getHeight() * 0.05);
        float bottom = cfoSize.getHeight()
                - (float) (cfoSize.getWidth() * 0.05);

        RectF rectf = new RectF(left, top, right, bottom);

        float angle = mLevel * 360;
        angle = angle / 100;

        for (int i = 1; i <= 24; i++) {
            c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, 11, false,
                    mypaint);
        }

        mypaint.setStrokeWidth((float) (cfoSize.getWidth() * 0.0783));

        mypaint.setColor(0xff33b5e5);

        int i;
        for (i = 1; i <= (angle / 15); i++)
            c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, 11, false,
                    mypaint);
        if (angle > -88 + ((i - 1) * 11) + (i - 1) * 4) {
            angle = angle - magnitude(angle);
            c.drawArc(rectf, -88 + ((i - 1) * 11) + (i - 1) * 4, angle, false,
                    mypaint);
        }

        return circleBitmap;

    }

    public static float magnitude(float angle) {
        String t = String.valueOf(angle);
        if ((t).contains(".")) {
            t = t.substring(0, t.indexOf("."));
        }
        return Float.parseFloat(t);
    }


    public static Intent createShareIntent(Context mContext) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(
                Intent.EXTRA_TEXT,
                "Checkout this Amazing App\nWord Learner\nGet it now from Playstore\n"
                        + Uri.parse("http://play.google.com/store/apps/details?id="
                        + mContext.getPackageName()));

        return Intent.createChooser(intent, "Share");
    }


    public static void incAppCount(Context mContext){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("AppCount", getAppCount(mContext) + 1).commit();
    }

    public static int getAppCount(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("AppCount", 0);
    }

    public static void launchMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        myAppLinkToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("http://play.google.com/store/apps/details?id="
                            + packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


}
