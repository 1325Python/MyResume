package com.example.weatherjson;

import static android.widget.Toast.LENGTH_SHORT;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText enterZipCode;

    Button submitButton;

    String zipCode, cN, weather, sunRiseStandard, sunSetStandard;

    int time, sunRise, sunSet;

    double temp_max, temp, temp_min, feels_like, chanceOfPrecipitation;

    String EST, day, dateToDisplay;

    TextView displayCity, displayMaxTemp, displayTemp, displayMinTemp, displayFeelsLike, showWeather, showTime, showDay, showDate, showSunRise, showSunSet, showPOP;

    ImageView projectWeather;

    boolean checkIfAlpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterZipCode = findViewById(R.id.id_enter_zipCode);
        submitButton = findViewById(R.id.id_submit_button);
        displayCity = findViewById(R.id.id_display_city);
        displayMaxTemp = findViewById(R.id.id_max_temp);
        projectWeather = findViewById(R.id.id_weather_projection);
        displayTemp = findViewById(R.id.id_display_temp);
        displayMinTemp = findViewById(R.id.id_temp_min);
        displayFeelsLike = findViewById(R.id.id_feels_like);
        showWeather = findViewById(R.id.id_show_weather);
        showTime = findViewById(R.id.id_show_time);
        showDay = findViewById(R.id.id_show_dayOfTheWeek);
        showDate = findViewById(R.id.id_show_date);
        showSunRise = findViewById(R.id.id_show_SunRise);
        showSunSet = findViewById(R.id.id_show_sunSet);
        showPOP = findViewById(R.id.id_show_precipitation);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < enterZipCode.getText().length(); i++) {
                    if (!Character.isDigit(enterZipCode.getText().toString().charAt(i)))
                        checkIfAlpha = true;
                    else
                        checkIfAlpha = false;
                }
                if (enterZipCode.getText().length() == 5 && !checkIfAlpha) {
                    zipCode = enterZipCode.getText().toString();
                    new asyncTask().execute(zipCode);
                    displayCity.setText(cN);
                    displayMaxTemp.setText("Highest: " + temp_max + " F");
                    displayTemp.setText("Temperature Right Now: " + temp + " F");
                    displayMinTemp.setText("Lowest: " + temp_min + " F");
                    displayFeelsLike.setText("Feels Like: " + feels_like + " F");
                    showWeather.setText(weather);
                    showTime.setText(EST);
                    showDay.setText(day);
                    showSunRise.setText(sunRiseStandard + " AM");
                    showSunSet.setText(sunSetStandard + " PM");
                    showPOP.setText("Chance of precipitation: " + chanceOfPrecipitation);
                } else {
                    Toast.makeText(MainActivity.this, "Enter a valid zipcode", LENGTH_SHORT).show();
                }
            }
        });
    }

    private class asyncTask extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            Thread t1 = new Thread() {
                public void run() {
                    try {
                        URL weatherAPI = new URL("https://api.openweathermap.org/data/2.5/forecast?zip="+strings[0]+"&appid=16e8364dadd72ea6b56a6a8f89ad80f8");
                        URLConnection url = weatherAPI.openConnection();
                        InputStream inputStream = url.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String input;
                        StringBuilder jsonText = new StringBuilder();

                        while ((input = bufferedReader.readLine()) != null) {
                            jsonText.append(input);
                        }
                        bufferedReader.close();

                        String JSONstring = jsonText.toString();

                        JSONObject weatherObject = new JSONObject(JSONstring);

                        sunRise = weatherObject.getJSONObject("city").getInt("sunrise");
                        sunRiseStandard = convertEpochTime(sunRise);
                        sunSet = weatherObject.getJSONObject("city").getInt("sunset");
                        sunSetStandard = convertEpochTime(sunSet);

                        JSONObject cityName = weatherObject.getJSONObject("city");
                        cN = cityName.getString("name");

                        JSONArray list = weatherObject.getJSONArray("list");

                        chanceOfPrecipitation = list.getJSONObject(0).getDouble("pop");

                        time = list.getJSONObject(0).getInt("dt");
                        EST = convertEpochTime(time);
                        day = returnDay(time);

                        Date date = new Date((long)time*1000);

                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        dateToDisplay = sdf.format(date);

                        JSONObject main = list.getJSONObject(0).getJSONObject("main");

                        temp_max = main.getDouble("temp_max");
                        temp_max = 1.8*(temp_max-273.15) + 32;

                        temp = main.getDouble("temp");
                        temp = 1.8*(temp-273.15) + 32;

                        temp_min = main.getDouble("temp_min");
                        temp_min = 1.8*(temp_min-273.15) + 32;

                        feels_like = main.getDouble("feels_like");
                        feels_like = 1.8*(feels_like-273.15)+32;


                        JSONArray WeatherArray = list.getJSONObject(0).getJSONArray("weather");

                        weather = WeatherArray.getJSONObject(0).getString("main");


                        Log.d("List?", weatherObject.getString("list"));

                        Log.d("Weather",WeatherArray.getJSONObject(0).getString("main"));


                    } catch (IOException |
                             JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            t1.start();
            try {
                t1.join();
            }catch(InterruptedException e) {
                System.out.println("Error");
            }
            return weather + " " + dateToDisplay;
        }
        @Override
        protected void onPostExecute(String result)
        {
            if(result.substring(0,weather.length()).equalsIgnoreCase("clear"))
            {
                projectWeather.setImageResource(R.drawable.sun);
            }
            else if(result.substring(0, weather.length()).equalsIgnoreCase("clouds"))
            {
                projectWeather.setImageResource(R.drawable.clouds);
            }
            else if(result.substring(0, weather.length()).equalsIgnoreCase("partly cloudy"))
            {
                projectWeather.setImageResource(R.drawable.partlycloudy);
            }
            else if(result.substring(0, weather.length()).equalsIgnoreCase("snow"))
            {
                projectWeather.setImageResource(R.drawable.snow);
            }
            else if(result.substring(0, weather.length()).equalsIgnoreCase("rain"))
            {
                projectWeather.setImageResource(R.drawable.rain);
            }
            showDate.setText(result.substring(weather.length()+1));
        }
    }
    public static String convertEpochTime(int epochTime)
    {
        epochTime = epochTime-1767225600;
        int timeInCurrentDay = epochTime%86400;
        timeInCurrentDay = (int)timeInCurrentDay/60;
        int minutes = timeInCurrentDay%60;
        timeInCurrentDay = ((int)timeInCurrentDay/60)+7;
        timeInCurrentDay = timeInCurrentDay%12;
        if(minutes%10!=0)
            return timeInCurrentDay + ":" + minutes;
        else
            return timeInCurrentDay + ":0" + minutes;
    }
    public static String returnDay(int epochTime)
    {
        epochTime = epochTime-1767225600;
        int dayOfTheWeek = (int)epochTime/86400;
        dayOfTheWeek = dayOfTheWeek%7;
        if(dayOfTheWeek==1)
            return "Friday";
        else if(dayOfTheWeek==2)
            return "Saturday";
        else if(dayOfTheWeek==3)
            return "Sunday";
        else if(dayOfTheWeek==4)
            return "Monday";
        else if(dayOfTheWeek==5)
            return "Tuesday";
        else if(dayOfTheWeek==6)
            return "Wednesday";
        else
            return "Thursday";
    }

}