package com.com.grupocontinental;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
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




    private LinearLayout div_recycler_autos,ver_filtros,caja_filtros,abrir_link,caja_mensaje_cotizacion,div_opciones_envio;
    private ExecutorService executorService;
    private JSONArray json_datos_autos;
    private Context context;
    private static String SERVIDOR_CONTROLADOR;
    private RecyclerView recycler_autos;
    private AdadpterListaAutos adadpterListaAutos;
    private ArrayList<ListaAutos> listaAutosArrayList,listaAutosFiltrados;
    private String vista_actual_str,palabra_buscada_str,foto_1_str,foto_2_str,foto_3_str,numero_foto,mensualidad_seleccionada_str,precio_str,pago_inicial_str,link_str,nombre_usuario_str,correo_usuario_str,marca_str,modelo_str,ano_str,kms_str,comentario_str,url_foto_envio;
    private ImageView buscar_palabra,borrar_palabra,cerrar_caja_filtros,imagen_auto,btn_imagen_2,btn_imagen_3;
    private EditText palabra_buscada, pago_inicial_et;
    private SeekBar controlador_precio;
    private RadioGroup grupo_filtros;
    private TextView filtros_tv,marca_auto_tv,modelo_auto_tv,precio_auto_tv,comentarios_auto_tv,lista_pagos_mens_tv,precio_total_tv,mensualidad_calculada,enviar_informacion,cancelar_cotizacion,enviar_cotizacion,mensaje_cotizacion,aceptar_envio;
    private ScrollView div_infromacion_auto;
    String url_server;
    private Spinner plazo_meses_sp;
    private AdapterMensualidades adapterMensualidades;
    public ArrayList<SpinnerModel> listaMensualidades = new ArrayList<>();
    private int precio_numero;
    double valor_total,division_mensualidad,pago_inicial_valor,valor_resta;
    private SharedPreferences datosUsuario;
    private SharedPreferences.Editor editor;
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
        plazo_meses_sp=findViewById(R.id.plazo_meses_sp);
        precio_total_tv=findViewById(R.id.precio_total_tv);
        mensualidad_calculada=findViewById(R.id.mensualidad_calculada);
        pago_inicial_et =findViewById(R.id.pago_inicial_et);
        abrir_link=findViewById(R.id.abrir_link);
        setListaMensualidades();
        vista_actual_str="vista_principal";
        datosUsuario = getSharedPreferences("Usuario",this.MODE_PRIVATE);
        nombre_usuario_str=datosUsuario.getString("nombre_usuario","no");
        correo_usuario_str=datosUsuario.getString("correo_usuario","no");
        caja_mensaje_cotizacion=findViewById(R.id.caja_mensaje_cotizacion);
        cancelar_cotizacion=findViewById(R.id.cancelar_cotizacion);
        enviar_cotizacion=findViewById(R.id.enviar_cotizacion);
        adapterMensualidades = new AdapterMensualidades(Principal.this, R.layout.lista_mensualidades, listaMensualidades, getResources());
        plazo_meses_sp.setAdapter(adapterMensualidades);
        enviar_informacion=findViewById(R.id.enviar_informacion);
        div_opciones_envio=findViewById(R.id.div_opciones_envio);
        mensaje_cotizacion=findViewById(R.id.mensaje_cotizacion);
        aceptar_envio=findViewById(R.id.aceptar_envio);

        palabra_buscada.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Realiza las acciones que desees al presionar "Enter"
                    // Puedes dejar este bloque vacío si no deseas realizar ninguna acción
                    return true;
                }
                return false;
            }
        });
        palabra_buscada.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        // Evitar el salto de línea
                        for (int i = start; i < end; i++) {
                            if (source.charAt(i) == '\n') {
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });

        aceptar_envio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caja_mensaje_cotizacion.setVisibility(View.GONE);
                div_infromacion_auto.setVisibility(View.VISIBLE);
                div_opciones_envio.setVisibility(View.VISIBLE);
                aceptar_envio.setVisibility(View.GONE);

            }
        });
        cancelar_cotizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caja_mensaje_cotizacion.setVisibility(View.GONE);
                div_infromacion_auto.setVisibility(View.VISIBLE);
            }
        });
        enviar_informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!pago_inicial_et.getText().toString().equals("")){
                    if(!mensualidad_seleccionada_str.equals("Selecciona una opcion")){
                        caja_mensaje_cotizacion.setVisibility(View.VISIBLE);
                        div_infromacion_auto.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(getApplicationContext(),"Seleccione un plazo de tiempo",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Se necesita una pago inicial",Toast.LENGTH_LONG).show();
                }


            }
        });
        enviar_cotizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                div_opciones_envio.setVisibility(View.GONE);
                mensaje_cotizacion.setText("Enviando cotizacion....");

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        enviar_informacion_ticket();
                    }
                });
            }
        });
        abrir_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent implícito para abrir el navegador web
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link_str));
                startActivity(intent);
            }
        });
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
                                String deuda_actual1 = o1.getKms();
                                String deuda_actual2 = o2.getKms();

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
                        filtros_tv.setText("Kilometraje ascedente");

                        break;

                    case R.id.kms_mayor:
                        Collections.sort(listaAutosArrayList, new Comparator<ListaAutos>() {
                            @Override
                            public int compare(ListaAutos o1, ListaAutos o2) {
                                // Parsea los valores de cobros a Double y compara
                                String deuda_actual1 = o1.getKms();
                                String deuda_actual2 = o2.getKms();

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
        plazo_meses_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lista_pagos_mens_tv = findViewById(R.id.lista_pagos_mensualidades);
                if (lista_pagos_mens_tv == null) {
                    lista_pagos_mens_tv = (TextView) view.findViewById(R.id.lista_pagos_mensualidades);
                } else {
                    lista_pagos_mens_tv = (TextView) view.findViewById(R.id.lista_pagos_mensualidades);
                }
                pago_inicial_str= pago_inicial_et.getText().toString();
                if (pago_inicial_str.equals("")||pago_inicial_str.equals(null)){
                    pago_inicial_valor=0;
                }else {
                    pago_inicial_valor=Integer.parseInt(pago_inicial_str);

                }
                valor_resta=valor_total-pago_inicial_valor;
                Log.e("valor_total", String.valueOf(valor_resta));
                mensualidad_seleccionada_str = lista_pagos_mens_tv.getText().toString();
                Log.e("MENSUALIDAD",mensualidad_seleccionada_str);
                precio_numero = Integer.parseInt(precio_str);
                valor_total=precio_numero*1.08;
                valor_total = Math.round(valor_total * 100.0) / 100.0;

                if (mensualidad_seleccionada_str.equals("12 meses")){
                    division_mensualidad=valor_resta/12;
                    precio_total_tv.setText("Precio total de $"+valor_total);

                } else if (mensualidad_seleccionada_str.equals("18 meses")){
                    division_mensualidad=valor_resta/18;
                    precio_total_tv.setText("Precio total de $"+valor_total);

                } else if (mensualidad_seleccionada_str.equals("24 meses")){
                    division_mensualidad=valor_resta/24;
                    precio_total_tv.setText("Precio total de $"+valor_total);

                }
                else if (mensualidad_seleccionada_str.equals("36 meses")){
                    division_mensualidad=valor_resta/36;
                    precio_total_tv.setText("Precio total de $"+valor_total);

                }
                division_mensualidad = Math.round(division_mensualidad * 100.0) / 100.0;


                Log.e("valor_total", String.valueOf(valor_total));
                mensualidad_calculada.setText("$"+division_mensualidad);
                Log.e("division_mensualidad",String.valueOf(division_mensualidad));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
        precio_str=precio;
        link_str=link;
        Log.e("precio", String.valueOf(precio));



        foto_1_str=foto_1;
        foto_2_str=foto_2;
        foto_3_str=foto_3;
        numero_foto="posicion_1";
        url_server="http://192.168.100.4/grupoContinental/vista/img/autos/";
        Picasso.get().load(url_server+foto_1).into(imagen_auto);
        url_foto_envio=foto_1;
        marca_auto_tv.setText(marca);
        marca_str=marca;
        modelo_auto_tv.setText(modelo+" "+ano);
        modelo_str=modelo;
        ano_str=ano;
        precio_auto_tv.setText("Precio desde $"+precio);

        comentarios_auto_tv.setText(comentarios);
        comentario_str=comentarios;
        kms_str=kms;
    }

    @Override
    public void onBackPressed() {
        if (vista_actual_str.equals("informacion_autos")){
            div_recycler_autos.setVisibility(View.VISIBLE);
            div_infromacion_auto.setVisibility(View.GONE);
            vista_actual_str="vista_principal";

        } else if (vista_actual_str.equals("vista_principal")) {
            Intent intent = new Intent(Principal.this, com.com.grupocontinental.Login.class);
            startActivity(intent);
        }


    }
    public void setListaMensualidades()
    {
        listaMensualidades.clear();
        String coy[] = {"Selecciona una opcion", "12 meses","18 meses","24 meses","36 meses"};
        for (int i=0; i<coy.length;i++)
        {
            final SpinnerModel sched = new SpinnerModel();
            sched.ponerNombre(coy[i]);
            //sched.ponerImagen("spinner"+i);
            sched.ponerImagen("spi_"+i);
            listaMensualidades.add(sched);
        }
    }
    public void enviar_informacion_ticket()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"crear_ticket_prueba.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("respuesta:",response);
                        if(response.contains("success")){
                            mensaje_cotizacion.setText("La cotizacion  fue enviada con exito puedes revisar tu correo");
                            aceptar_envio.setVisibility(View.VISIBLE);
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
                map.put("nombre_cliente",nombre_usuario_str);
                map.put("correo_cliente",correo_usuario_str);
                map.put("marca",marca_str);
                map.put("modelo",modelo_str);
                map.put("ano",ano_str);
                map.put("kilometraje",kms_str);
                map.put("comentarios",comentario_str);
                map.put("precio",precio_str);
                map.put("enganche",pago_inicial_str);
                map.put("plazo",mensualidad_seleccionada_str   );
                map.put("comision_apertura","comision_apertura");
                map.put("cantidad_mensualidad", String.valueOf(division_mensualidad));
                map.put("precio_total", String.valueOf(valor_total));
                map.put("link_auto", link_str);
                map.put("url_foto", url_foto_envio);


                return map;
            }
        };
        requestQueue.add(request);
    }
}

