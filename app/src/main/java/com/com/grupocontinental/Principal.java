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
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Principal extends AppCompatActivity {

    private LinearLayout div_recycler_autos,ver_filtros,caja_filtros;
    private ExecutorService executorService;
    private JSONArray json_datos_autos;
    private Context context;
    private static String SERVIDOR_CONTROLADOR;
    private RecyclerView recycler_autos;
    private AdadpterListaAutos adadpterListaAutos;
    private ArrayList<ListaAutos> listaAutosArrayList,listaAutosFiltrados;
    private String vista_actual_str,palabra_buscada_str,foto_1_str,foto_2_str,foto_3_str,numero_foto;
    private ImageView buscar_palabra,borrar_palabra,cerrar_caja_filtros,imagen_auto,btn_imagen_2,btn_imagen_3;
    private EditText palabra_buscada;
    private SeekBar controlador_precio;
    private RadioGroup grupo_filtros;
    private TextView filtros_tv,marca_auto_tv,modelo_auto_tv,precio_auto_tv,comentarios_auto_tv;
    private ScrollView div_infromacion_auto;
    String url_server;
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
        ver_filtros=findViewById(R.id.ver_filtros);
        caja_filtros=findViewById(R.id.caja_filtros);
        grupo_filtros=findViewById(R.id.grupo_filtros);
        filtros_tv=findViewById(R.id.filtros_tv);
        cerrar_caja_filtros=findViewById(R.id.cerrar_caja_filtros);
        imagen_auto=findViewById(R.id.imagen_auto);
        marca_auto_tv=findViewById(R.id.marca_auto_tv);
        modelo_auto_tv=findViewById(R.id.modelo_auto_tv);
        precio_auto_tv=findViewById(R.id.precio_auto_tv);
        comentarios_auto_tv=findViewById(R.id.comentarios_auto_tv);
        btn_imagen_2=findViewById(R.id.btn_imagen_2);
        btn_imagen_3=findViewById(R.id.btn_imagen_3);
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
        ver_filtros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                caja_filtros.setVisibility(View.VISIBLE);
            }
        });
        grupo_filtros.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.marca_menor:
                        Collections.sort(listaAutosArrayList, new Comparator<ListaAutos>() {
                            @Override
                            public int compare(ListaAutos o1, ListaAutos o2) {
                                // Parsea los valores de cobros a Double y compara
                                String nombre1 = o1.getMarca();
                                String nombre2 = o2.getMarca();
                                Log.e("nombre1", nombre1);
                                Log.e("nombre2", nombre2);
                                return nombre1.compareTo(nombre2); // Ordena alfabéticamente de A a Z

                            }
                        });
                        adadpterListaAutos = new AdadpterListaAutos(listaAutosArrayList);
                        recycler_autos.setAdapter(adadpterListaAutos);
                        // Notifica al adaptador para que se actualice con la nueva ordenación
                        adadpterListaAutos.notifyDataSetChanged();
                        caja_filtros.setVisibility(View.GONE);
                        filtros_tv.setText("Marca A-Z");

                        break;

                    case R.id.marca_mayor:
                        Collections.sort(listaAutosArrayList, new Comparator<ListaAutos>() {
                            @Override
                            public int compare(ListaAutos o1, ListaAutos o2) {
                                String nombre1 = o1.getMarca();
                                String nombre2 = o2.getMarca();
                                Log.e("nombre1", nombre1);
                                Log.e("nombre2", nombre2);
                                return nombre2.compareTo(nombre1); // Ordena alfabéticamente de Z a A
                            }
                        });
                        adadpterListaAutos = new AdadpterListaAutos(listaAutosArrayList);
                        recycler_autos.setAdapter(adadpterListaAutos);
                        // Notifica al adaptador para que se actualice con la nueva ordenación
                        adadpterListaAutos.notifyDataSetChanged();
                        caja_filtros.setVisibility(View.GONE);
                        filtros_tv.setText("Marca Z-A");

                        break;
                    case R.id.kms_menor:
                        Collections.sort(listaAutosArrayList, new Comparator<ListaAutos>() {
                            @Override
                            public int compare(ListaAutos o1, ListaAutos o2) {
                                // Parsea los valores de cobros a Double y compara
                                String fecha1 = o1.getKms();
                                String fecha2 = o2.getKms();
                                Log.e("fecha1", fecha1);
                                Log.e("fecha1", fecha2);
                                return fecha1.compareTo(fecha2); // Ordena alfabéticamente de A a Z

                            }
                        });
                        adadpterListaAutos = new AdadpterListaAutos(listaAutosArrayList);
                        recycler_autos.setAdapter(adadpterListaAutos);
                        adadpterListaAutos.notifyDataSetChanged();
                        caja_filtros.setVisibility(View.GONE);
                        filtros_tv.setText("Kilometraje ascedente");

                        break;

                    case R.id.kms_mayor:
                        Collections.sort(listaAutosArrayList, new Comparator<ListaAutos>() {
                            @Override
                            public int compare(ListaAutos o1, ListaAutos o2) {
                                // Parsea los valores de cobros a Double y compara
                                String fecha1 = o1.getKms();
                                String fecha2 = o2.getKms();
                                Log.e("fecha1", fecha1);
                                Log.e("fecha2", fecha2);
                                return fecha2.compareTo(fecha1);
                            }
                        });
                        adadpterListaAutos = new AdadpterListaAutos(listaAutosArrayList);
                        recycler_autos.setAdapter(adadpterListaAutos);
                        // Notifica al adaptador para que se actualice con la nueva ordenación
                        adadpterListaAutos.notifyDataSetChanged();
                        caja_filtros.setVisibility(View.GONE);
                        filtros_tv.setText("Kilometraje descendente");

                        break;

                    case R.id.precio_menor:
                        Collections.sort(listaAutosArrayList, new Comparator<ListaAutos>() {
                            @Override
                            public int compare(ListaAutos o1, ListaAutos o2) {
                                String deuda_actual1 = o1.getPrecio();
                                String deuda_actual2 = o2.getPrecio();

                                // Reemplazar valores vacíos con "0"
                                if (deuda_actual1.isEmpty()) {
                                    deuda_actual1 = "0";
                                }
                                if (deuda_actual2.isEmpty()) {
                                    deuda_actual2 = "0";
                                }

                                double deuda1 = Double.parseDouble(deuda_actual1);
                                double deuda2 = Double.parseDouble(deuda_actual2);
                                Log.e("deuda1", String.valueOf(deuda1));
                                Log.e("deuda2", String.valueOf(deuda2));
                                return Double.compare(deuda1, deuda2);
                            }
                        });
                        adadpterListaAutos = new AdadpterListaAutos(listaAutosArrayList);
                        recycler_autos.setAdapter(adadpterListaAutos);
                        adadpterListaAutos.notifyDataSetChanged();
                        caja_filtros.setVisibility(View.GONE);
                        filtros_tv.setText("Precio ascedente");

                        break;

                    case R.id.precio_mayor:
                        Collections.sort(listaAutosArrayList, new Comparator<ListaAutos>() {
                            @Override
                            public int compare(ListaAutos o1, ListaAutos o2) {
                                String deuda_actual1 = o1.getPrecio();
                                String deuda_actual2 = o2.getPrecio();

                                // Reemplazar valores vacíos con "0"
                                if (deuda_actual1.isEmpty()) {
                                    deuda_actual1 = "0";
                                }
                                if (deuda_actual2.isEmpty()) {
                                    deuda_actual2 = "0";
                                }

                                double deuda1 = Double.parseDouble(deuda_actual1);
                                double deuda2 = Double.parseDouble(deuda_actual2);
                                Log.e("deuda1", String.valueOf(deuda1));
                                Log.e("deuda2", String.valueOf(deuda2));
                                return Double.compare(deuda2, deuda1);
                            }
                        });
                        adadpterListaAutos = new AdadpterListaAutos(listaAutosArrayList);
                        recycler_autos.setAdapter(adadpterListaAutos);
                        adadpterListaAutos.notifyDataSetChanged();
                        caja_filtros.setVisibility(View.GONE);
                        filtros_tv.setText("Precio descendente");

                        break;




                    default:
                        // No se ha seleccionado ningún RadioButton
                        break;
                }
            }
        });
        cerrar_caja_filtros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                caja_filtros.setVisibility(View.GONE);

            }
        });
        btn_imagen_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_imagen_2.setVisibility(View.VISIBLE);

                if (numero_foto.equals("posicion_1")){
                    Picasso.get().load(url_server+foto_2_str).into(imagen_auto);
                    btn_imagen_2.setVisibility(View.GONE);
                    numero_foto="posicion_2";
                }
                if (numero_foto.equals("posicion_3")){
                    Picasso.get().load(url_server+foto_1_str).into(imagen_auto);
                    btn_imagen_3.setVisibility(View.VISIBLE);

                    numero_foto="posicion_1";
                }
            }
        });
        btn_imagen_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numero_foto.equals("posicion_1")){
                    Picasso.get().load(url_server+foto_3_str).into(imagen_auto);
                    numero_foto="posicion_3";
                    btn_imagen_3.setVisibility(View.GONE);
                }
                if (numero_foto.equals("posicion_2")){
                    Picasso.get().load(url_server+foto_1_str).into(imagen_auto);
                    numero_foto="posicion_1";
                    btn_imagen_2.setVisibility(View.VISIBLE);

                }
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
        foto_1_str=foto_1;
        foto_2_str=foto_2;
        foto_3_str=foto_3;
        numero_foto="posicion_1";
        url_server="http://192.168.100.4/grupoContinental/vista/img/autos/";
        Picasso.get().load(url_server+foto_1).into(imagen_auto);
        marca_auto_tv.setText(marca);
        modelo_auto_tv.setText(modelo);
        precio_auto_tv.setText("Precio desde $"+precio);
        comentarios_auto_tv.setText(comentarios);
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

