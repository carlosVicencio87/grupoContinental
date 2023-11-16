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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private ArrayList<ListaAutos> listaAutosArrayList,listaAutosFiltrados;
    private String vista_actual_str,palabra_buscada_str;
    private ImageView buscar_palabra,borrar_palabra;
    private EditText palabra_buscada;
    private SeekBar controlador_precio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_principal);
        listaAutosFiltrados = new ArrayList<>();
        recycler_autos=findViewById(R.id.recycler_autos);
        listaAutosArrayList=new ArrayList<>();
        recycler_autos.setLayoutManager(new LinearLayoutManager(Principal.this, LinearLayoutManager.VERTICAL, false));
        listaAutosArrayList.clear();
        executorService= Executors.newSingleThreadExecutor();
        div_recycler_autos=findViewById(R.id.div_recycler_autos);
        div_infromacion_auto=findViewById(R.id.div_infromacion_auto);
        palabra_buscada=findViewById(R.id.palabra_buscada);
        buscar_palabra=findViewById(R.id.buscar_palabra);
        borrar_palabra=findViewById(R.id.borrar_palabra);
        controlador_precio=findViewById(R.id.controlador_precio);
        SERVIDOR_CONTROLADOR = new Servidor().local;

        controlador_precio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Este método se llama cuando se cambia el valor de la SeekBar
                // Puedes realizar acciones aquí según el valor actual (progress)
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Este método se llama cuando se inicia el seguimiento del tacto en la SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Este método se llama cuando se detiene el seguimiento del tacto en la SeekBar
                int selectedValue = seekBar.getProgress();
                Toast.makeText(Principal.this, "Valor seleccionado: " + selectedValue, Toast.LENGTH_SHORT).show();
                filtrarPorPrecio(selectedValue);
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                pedirAutos();


            }
        });
        buscar_palabra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                palabra_buscada_str = palabra_buscada.getText().toString().toLowerCase().trim(); // Convertir la palabra a minúsculas para la búsqueda sin distinción entre mayúsculas y minúsculas

                Log.e("palabra_buscada_str", palabra_buscada_str);

                listaAutosFiltrados.clear(); // Limpiar la lista de resultados filtrados
                buscar_palabra.setVisibility(View.GONE);
                borrar_palabra.setVisibility(View.VISIBLE);
                for (ListaAutos auto : listaAutosArrayList) {
                    // Realizar la búsqueda en los campos relevantes, como nombre, apellido, etc.
                    if (auto.getMarca().toLowerCase().contains(palabra_buscada_str) ||
                            auto.getModelo().toLowerCase().contains(palabra_buscada_str) ||
                            auto.getAno().toLowerCase().contains(palabra_buscada_str)||auto.getKms().toLowerCase().contains(palabra_buscada_str)||auto.getComentarios().toLowerCase().contains(palabra_buscada_str)) {
                        listaAutosFiltrados.add(auto); // Agregar a la lista de resultados filtrados
                    }
                }

                // Actualizar el RecyclerView con los resultados filtrados
                adadpterListaAutos=new AdadpterListaAutos(listaAutosFiltrados);
                recycler_autos.setAdapter(adadpterListaAutos);

            }
        });
        borrar_palabra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscar_palabra.setVisibility(View.VISIBLE);
                borrar_palabra.setVisibility(View.GONE);
                palabra_buscada.setText("");
                adadpterListaAutos = new AdadpterListaAutos(listaAutosArrayList);
                recycler_autos.setAdapter(adadpterListaAutos);
            }
        });
    }
    private void filtrarPorPrecio(int precioSeleccionado) {
        listaAutosFiltrados.clear(); // Limpiar la lista de resultados filtrados
        for (ListaAutos auto : listaAutosArrayList) {
            // Filtrar los autos cuyo precio esté dentro de un rango definido alrededor del valor seleccionado
            int precioAuto = Integer.parseInt(auto.getPrecio());
            int rango = 100000; // Puedes ajustar este valor según tus necesidades
            if (precioAuto >= precioSeleccionado - rango && precioAuto <= precioSeleccionado + rango) {
                listaAutosFiltrados.add(auto); // Agregar a la lista de resultados filtrados
            }
        }

        // Actualizar el RecyclerView con los resultados filtrados
        adadpterListaAutos = new AdadpterListaAutos(listaAutosFiltrados);
        recycler_autos.setAdapter(adadpterListaAutos);
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
                                    String strAno= jsonObject.getString("ano");
                                    String strKms= jsonObject.getString("kms");

                                    String strPrecio=jsonObject.getString("precio");
                                    String strLink=jsonObject.getString("link");
                                    String strFoto_1=jsonObject.getString("foto_1");
                                    String strFoto_2=jsonObject.getString("foto_2");
                                    String strFoto_3=jsonObject.getString("foto_3");
                                    String strComentarios=jsonObject.getString("comentarios");

                                    Log.e("strId", strId);
                                    Log.e("strMarca", strMarca);
                                    Log.e("strModelo", strModelo);
                                    Log.e("strPrecio", strPrecio);
                                    listaAutosArrayList.add( new ListaAutos(strId,strMarca,strModelo,strAno,strKms,strPrecio,strLink,strFoto_1,strFoto_2,strFoto_3,strComentarios));

                                }
                                adadpterListaAutos=new AdadpterListaAutos(listaAutosArrayList);
                                recycler_autos.setAdapter(adadpterListaAutos);
                                vista_actual_str="vista_principal";

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
    public void masInformacion(String id,String marca,String modelo,String ano,String kms,String precio,String link,String foto_1,String foto_2,String foto_3, String comentarios){
        div_recycler_autos.setVisibility(View.GONE);
        div_infromacion_auto.setVisibility(View.VISIBLE);
        vista_actual_str="informacion_autos";
        Log.e("marca",marca+modelo+kms);
    }

    @Override
    public void onBackPressed() {
        if (vista_actual_str.equals("informacion_autos")){
            div_recycler_autos.setVisibility(View.VISIBLE);
            div_infromacion_auto.setVisibility(View.GONE);
        }


    }
}

