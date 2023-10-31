package com.com.grupocontinental;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private ExecutorService executorService;

    private LinearLayout div_login,div_principal;
    private EditText correo,contrasena;
    private TextView ingresar,mensaje,ir_catalogo,nombre_usuario;
    private String valCorreo,valContra,correo_final;
    private static String SERVIDOR_CONTROLADOR;
    private int check=0;
    private SharedPreferences datosUsuario;
    private SharedPreferences.Editor editor;
    private boolean correo_exitoso,contrasena_exitoso;
    private JSONArray json_datos_usuario;

    private  String strInicio,strUsuario;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        executorService= Executors.newSingleThreadExecutor();
        SERVIDOR_CONTROLADOR = new Servidor().local;
        datosUsuario = getSharedPreferences("Usuario",this.MODE_PRIVATE);
        strUsuario=datosUsuario.getString("nombre_usuario","no");

        editor=datosUsuario.edit();


        correo=findViewById(R.id.correo);
        contrasena =findViewById(R.id.contrasena);
        ingresar= findViewById(R.id.ingresar);
        mensaje =findViewById(R.id.mensaje);
        div_login=findViewById(R.id.div_login);
        div_principal=findViewById(R.id.div_principal);
        ir_catalogo=findViewById(R.id.ir_catalogo);
        nombre_usuario=findViewById(R.id.nombre_usuario);
        context=this;
        checkSesion();

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valCorreo=correo.getText().toString();
                valContra=contrasena.getText().toString();
                Log.e("datocorreo",valCorreo );
                Log.e("datocontra",valContra );
                correo_final=correo.getText().toString().trim().toLowerCase();
                correo_final=correo.getText().toString().trim().toLowerCase();
                if (!correo_final.equals("")&&correo_final!=null)
                {
                    // String regex = "^(.+)@(.+)$";

                    String regexUsuario = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
                    Pattern pattern = Pattern.compile(regexUsuario);
                    Matcher matcher = pattern.matcher(correo_final);
                    if(matcher.matches()==true){

                        correo_exitoso=true;

                    }
                }
                if(!valCorreo.trim().equals("")){
                    if(!valContra.trim().equals("")){
                        if(correo_exitoso==true){

                            ingresar.setVisibility(View.GONE);
                            mensaje.setText("Iniciando sesión ...");
                            mensaje.setVisibility(View.VISIBLE);
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    hacerPeticion();


                                }
                            });


                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Ingrese un correo valido.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "La contrasena es necesario.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "El correo es necesario.", Toast.LENGTH_LONG).show();
                }

            }

        });


        correo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean tieneFoco) {

                if(!tieneFoco)
                {

                }
                else{
                    Toast.makeText(getApplicationContext(),"Ingrese correo valido.",Toast.LENGTH_LONG).show();

                }
            }
        });
        ir_catalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, com.com.grupocontinental.Principal.class);
                startActivity(intent);
            }
        });
    }


    public void hacerPeticion()
    {
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST,  SERVIDOR_CONTROLADOR+"login_usuario.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("respuesta:",response);
                        if (response.equals("no_existe")) {
                            ingresar.setVisibility(View.VISIBLE);
                            mensaje.setText("El usuario no existe.");
                        }
                        else
                        {
                            try {

                                json_datos_usuario=new JSONArray(response);
                                Log.e("lala",""+json_datos_usuario);
                                for (int i=0;i<json_datos_usuario.length();i++){
                                    JSONObject jsonObject = json_datos_usuario.getJSONObject(i);
                                    //Log.e("nombreMovies", String.valueOf(jsonObject));
                                    String strId = jsonObject.getString("id");
                                    String strId_sesion = jsonObject.getString("id_sesion");
                                    String strNombre_usuario = jsonObject.getString("nombre_usuario");
                                    String strCorreo_usuario=jsonObject.getString("correo_usuario");
                                    String strPassword_usuario=jsonObject.getString("password_usuario");
                                    String strActivo=jsonObject.getString("activo");

                                    nombre_usuario.setText("Bienvenido "+strNombre_usuario);

                                    Log.e("idsesion",strId_sesion);


                                    editor.putString("id",strId);
                                    editor.putString("nombre_usuario",strNombre_usuario);
                                    editor.putString("correo_usuario",strCorreo_usuario);
                                    editor.putString("estatus",strActivo);
                                    editor.putString("id_sesion",strId_sesion);

                                    editor.apply();

                                    Log.e("idsesion",strId_sesion);
                                    div_login.setVisibility(View.GONE);
                                    div_principal.setVisibility(View.VISIBLE);






                                }
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
                map.put("correo",valCorreo);
                map.put("password",valContra);
                return map;
            }
        };
        requestQueue.add(request);
    }



    private void checkSesion() {
        strInicio = datosUsuario.getString("id_sesion", "no");

        Log.e("inicio",""+strInicio);
        if (!strInicio.equals("no"))
        {

            Log.e("idsesion_main",strInicio);
            div_login.setVisibility(View.GONE);
            div_principal.setVisibility(View.VISIBLE);
            nombre_usuario.setText("Bienvenido "+strUsuario);

        }
    }
}