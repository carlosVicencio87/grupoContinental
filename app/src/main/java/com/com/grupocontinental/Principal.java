package com.com.grupocontinental;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Principal extends AppCompatActivity {

    private LinearLayout div_recycler_autos,div_infromacion_auto;
    private ExecutorService executorService;
    private JSONArray json_datos_autos;
    private Context context;
    private static String SERVIDOR_CONTROLADOR;
    private RecyclerView recycler_autos;
    private AdadpterListaAutos adadpterListaAutos;
    private ArrayList<ListaAutos> listaAutosArrayList;
    private String vista_actual_str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_principal);
        recycler_autos=findViewById(R.id.recycler_autos);
        listaAutosArrayList=new ArrayList<>();
        recycler_autos.setLayoutManager(new LinearLayoutManager(Principal.this, LinearLayoutManager.VERTICAL, false));
        listaAutosArrayList.clear();
        executorService= Executors.newSingleThreadExecutor();
        div_recycler_autos=findViewById(R.id.div_recycler_autos);
        div_infromacion_auto=findViewById(R.id.div_infromacion_auto);

        SERVIDOR_CONTROLADOR = new Servidor().local;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                pedirAutos();


            }
        });
    }
    public void pedirAutos()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"pedir_autos_app.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("respuesta:",response);
                        if (response.equals("no_existe")) {

                        }
                        else
                        {
                            try {

                                json_datos_autos =new JSONArray(response);
                                Log.e("lala",""+ json_datos_autos);
                                for (int i = 0; i< json_datos_autos.length(); i++){
                                    JSONObject jsonObject = json_datos_autos.getJSONObject(i);
                                    Log.e("nombreMovies", String.valueOf(jsonObject));
                                    String strId = jsonObject.getString("id");
                                    String strMarca= jsonObject.getString("marca");
                                    String strModelo= jsonObject.getString("modelo");
                                    String strPrecio=jsonObject.getString("precio");
                                    String strLink=jsonObject.getString("link");
                                    String strFoto_1=jsonObject.getString("foto_1");
                                    String strFoto_2=jsonObject.getString("foto_2");
                                    String strFoto_3=jsonObject.getString("foto_3");
                                    Log.e("strId", strId);
                                    Log.e("strMarca", strMarca);
                                    Log.e("strModelo", strModelo);
                                    Log.e("strPrecio", strPrecio);
                                    listaAutosArrayList.add( new ListaAutos(strId,strMarca,strModelo,strPrecio,strLink,strFoto_1,strFoto_2,strFoto_3));

                                }
                                adadpterListaAutos=new AdadpterListaAutos(listaAutosArrayList);
                                recycler_autos.setAdapter(adadpterListaAutos);
                                vista_actual_str="VISTA_RPINCPILA";

                            }
                            catch (JSONException e) {
                                Log.e("errorRespuesta", String.valueOf(e));
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e( "error", "error: " +error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();

                return map;
            }
        };
        requestQueue.add(request);
    }
    public void masInformacion(String id,String marca,String modelo,String precio,String link,String foto_1,String foto_2,String foto_3){
        div_recycler_autos.setVisibility(View.GONE);
        div_infromacion_auto.setVisibility(View.VISIBLE);
        vista_actual_str="informacion_autos";
    }

    @Override
    public void onBackPressed() {
        if (vista_actual_str.equals("informacion_autos")){
            div_recycler_autos.setVisibility(View.VISIBLE);
            div_infromacion_auto.setVisibility(View.GONE);
        }


    }
}

