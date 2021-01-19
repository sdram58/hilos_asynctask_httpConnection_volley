package com.catata.hilo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final static String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=Torrent,es&APPID=afe65bb24deaa16640c55f532603c7c6";
    private final static String URL_COMUNIDADES = "https://onthestage.es/restapi/v1/allcomunidades";
    TextView tvResultado;
    Button btnTarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        tvResultado = findViewById(R.id.tvResultado);
        btnTarea = findViewById(R.id.btnTarea);

        btnTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tareaLarga();
                //tareaLargaHilos();
                //new MyTareaAsincrona().execute();

                new GetAsync(new GetAsync.IAsyncGet() {
                    @Override
                    public void onFinish(Tiempo t) {
                        tvResultado.setText("Tiempo: " + t.kelvinToCelsius(t.temperatura) +"ºC\n"
                        +"Descripción: " + t.descripcion + "\n"
                        +"Humedad: " + t.humedad + "%");
                    }
                }).execute(API_URL);
            }
        });
    }

    private void tareaLargaHilos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("HILO", "Hilo Empezado");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResultado.setText("Finalizado");
                    }
                });

                Log.i("HILO", "Hilo finalizado");
            }
        }).start();
    }

    private void tareaLarga() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tvResultado.setText("Finalizado");
    }

    public void getComunidades(View view) {
        getComunidades();
    }

    class MyTareaAsincrona extends AsyncTask<Void, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvResultado.setText("0");

        }

        @Override
        protected String doInBackground(Void... params) {

            for(int i = 1; i<=10;i++){
                publishProgress(""+i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return "fin";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            tvResultado.setText(tvResultado.getText().toString() + " " + s +" Finalizado");

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            tvResultado.setText(values[0]);
        }
    }


    private void getComunidades(){
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_COMUNIDADES, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String s ="";
                try{

                    JSONArray datos = response.getJSONArray("DATA");
                    for(int i = 0;i<datos.length();i++){
                        JSONObject comunidad = datos.getJSONObject(i);
                        s +="\n" + comunidad.getString("descripcion");
                    }
                }catch (JSONException e){

                }

                tvResultado.setText(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }


}