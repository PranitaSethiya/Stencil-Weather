package com.saphion.stencilweather.utilities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.saphion.stencilweather.R;

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

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
    public static String[] romans = {"i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x"};

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

//    public static final String URL1 = "http://api.wordnik.com/v4/word.json/";
//    public static final String URL2 = "/definitions?limit=200&includeRelated=true&useCanonical=false&includeTags=false&api_key="+Constants.API_KEY;

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

    private static String convertListToString(ArrayList<String> examples, String separator, boolean index) {

        String result = "";
        for (int i = 0; i < examples.size(); i++) {
            result = result + (index ? ("<b>" + romans[i] + ".</b> ") : "") + examples.get(i).toUpperCase(Locale.US).charAt(0) + examples.get(i).substring(1) + separator;
        }
        return result;

    }


    public static View getProgressView(Activity activity, String msg) {
        final View view = LayoutInflater.from(activity).inflate(
                R.layout.progress_dialog, null);
        View img1 = view.findViewById(R.id.pd_circle1);
        View img2 = view.findViewById(R.id.pd_circle2);
        View img3 = view.findViewById(R.id.pd_circle3);
        int ANIMATION_DURATION = 400;
        Animator anim1 = setRepeatableAnim(activity, img1, ANIMATION_DURATION, R.animator.growndisappear);
        Animator anim2 = setRepeatableAnim(activity, img2, ANIMATION_DURATION, R.animator.growndisappear);
        Animator anim3 = setRepeatableAnim(activity, img3, ANIMATION_DURATION, R.animator.growndisappear);
        setListeners(img1, anim1, anim2, ANIMATION_DURATION);
        setListeners(img2, anim2, anim3, ANIMATION_DURATION);
        setListeners(img3, anim3, anim1, ANIMATION_DURATION);
        anim1.start();
        ((TextView) view.findViewById(R.id.tvMessage)).setText(Html.fromHtml(msg));
        return view;
    }

    public static int dpToPx(float i, Context mContext) {

        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        return (int) ((i * displayMetrics.density) + 0.5);

    }

    public static Animator setRepeatableAnim(Activity activity, View target, final int duration, int animRes) {
        final Animator anim = AnimatorInflater.loadAnimator(activity, animRes);
        anim.setDuration(duration);
        anim.setTarget(target);
        return anim;
    }

    public static void setListeners(final View target, Animator anim, final Animator animator, final int duration) {
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animat) {
                if (target.getVisibility() == View.INVISIBLE) {
                    target.setVisibility(View.VISIBLE);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animator.start();
                    }
                }, duration - 100);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
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
                "Checkout this Amazing App\n" + mContext.getString(R.string.app_name) + "\nGet it now from Playstore\n"
                        + Uri.parse("http://play.google.com/store/apps/details?id="
                        + mContext.getPackageName()));

        return Intent.createChooser(intent, "Share");
    }


    public static void incAppCount(Context mContext) {
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

    public static void hideKeyboard(EditText et, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static AlertDialog getProgressDialog(Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View view = LayoutInflater.from(activity).inflate(
                R.layout.progress_dialog, null);
        View img1 = view.findViewById(R.id.pd_circle1);
        View img2 = view.findViewById(R.id.pd_circle2);
        View img3 = view.findViewById(R.id.pd_circle3);
        int ANIMATION_DURATION = 400;
        Animator anim1 = setRepeatableAnim(activity, img1, ANIMATION_DURATION, R.animator.growndisappear);
        Animator anim2 = setRepeatableAnim(activity, img2, ANIMATION_DURATION, R.animator.growndisappear);
        Animator anim3 = setRepeatableAnim(activity, img3, ANIMATION_DURATION, R.animator.growndisappear);
        setListeners(img1, anim1, anim2, ANIMATION_DURATION);
        setListeners(img2, anim2, anim3, ANIMATION_DURATION);
        setListeners(img3, anim3, anim1, ANIMATION_DURATION);
        anim1.start();

        ((TextView) view.findViewById(R.id.tvMessage)).setText(Html.fromHtml(msg));

        builder.setView(view);
        AlertDialog ad = builder.create();
        ad.setCanceledOnTouchOutside(false);
        ad.show();
        ad.getWindow().setLayout(dpToPx(200, activity), dpToPx(125, activity));
        return ad;
    }


    public static Bitmap getTimeThumb(Context mContext, int mColor, float hour, float minutes) {
        int px = dpToPx(30, mContext);

        Bitmap mBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);

        float x = mBitmap.getWidth() / 2;
        float r = x - dpToPx(2.5f, mContext);
        Canvas canvas = new Canvas(mBitmap);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);

        mPaint.setStrokeWidth(px / 12.5f);

        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mBitmap.getWidth() / 2, mBitmap.getWidth() / 2, r, mPaint);

        mPaint.setColor(Color.WHITE);

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mBitmap.getWidth() / 2, mBitmap.getWidth() / 2, r, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mBitmap.getWidth() / 2, mBitmap.getWidth() / 2, dpToPx(0.5f, mContext), mPaint);

        mPaint.setStrokeWidth(px / 15f);
        hour = hour + minutes / 60.0f;
//        mPaint.setColor(0xFFFF0000);
        canvas.drawLine(x, x, (float) (x + (r - 15) * Math.cos(Math.toRadians((hour / 12.0f * 360.0f) - 90f))), (float) (x + (r - 10) * Math.sin(Math.toRadians((hour / 12.0f * 360.0f) - 90f))), mPaint);
//        mPaint.setColor(0xFF0000FF);
        canvas.drawLine(x, x, (float) (x + r * Math.cos(Math.toRadians((minutes / 60.0f * 360.0f) - 90f))), (float) (x + r * Math.sin(Math.toRadians((minutes / 60.0f * 360.0f) - 90f))), mPaint);

        return mBitmap;
    }


    public static int getTemperatureColor(Context mContext, float temp) {
        int color = mContext.getResources().getColor(R.color.main_button_blue_normal);

        if (temp < -10)
            color = mContext.getResources().getColor(R.color.primary_indigo);
        else if (temp >=-10 && temp <=-5)
            color = mContext.getResources().getColor(R.color.primary_blue);
        else if (temp >-5 && temp < 5)
            color = mContext.getResources().getColor(R.color.primary_light_blue);
        else if (temp >= 5 && temp < 10)
            color = mContext.getResources().getColor(R.color.primary_teal);
        else if (temp >= 10 && temp < 15)
            color = mContext.getResources().getColor(R.color.primary_light_green);
        else if (temp >= 15 && temp < 20)
            color = mContext.getResources().getColor(R.color.primary_green);
        else if (temp >= 20 && temp < 25)
            color = mContext.getResources().getColor(R.color.primary_lime);
        else if (temp >= 25 && temp < 28)
            color = mContext.getResources().getColor(R.color.primary_yellow);
        else if (temp >= 28 && temp < 32)
            color = mContext.getResources().getColor(R.color.primary_amber);
        else if (temp >= 32 && temp < 35)
            color = mContext.getResources().getColor(R.color.primary_orange);
        else if (temp >= 35)
            color = mContext.getResources().getColor(R.color.primary_red);

        return color;

    }

    public static float spToPx(Context context, Float i) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return i * scaledDensity;
    }

    public static Bitmap changeColor(Resources resources, int drawable, int color) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        Bitmap bmp = BitmapFactory.decodeResource(resources,
                drawable, o);
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        ColorFilter tintColor = new LightingColorFilter(color, 0);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColorFilter(tintColor);
        c.drawBitmap(bmp, 0, 0, mPaint);
        return bitmap;
    }
}
