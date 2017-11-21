package com.example.ariel.validarcep;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button btnCep;
    private EditText cep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCep = (Button) findViewById(R.id.btnVerifica);
        cep = (EditText) findViewById(R.id.edtCep);

        cep.addTextChangedListener(Mascara.insert("#####-###", cep));

        btnCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(cep.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this, "Cep em branco", Toast.LENGTH_SHORT).show();
                } else if (cep.getText().toString().length() < 9) {
                    Toast.makeText(MainActivity.this, "Cep invalido, faltando digitos", Toast.LENGTH_SHORT).show();

                } else {
                    VerificaCep(cep.getText().toString());
                }

            }
        });

    }

    public void VerificaCep(String cep) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://viacep.com.br/ws/" + cep + "/json/", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(final JSONObject response) {
                Log.d("Log", response.toString());
                try {
                    final String localidade = response.getString("localidade");
                    final String estado = response.getString("uf");
                    final String bairro = response.getString("bairro");

                    if ("".equals(localidade)) {
                        Toast.makeText(MainActivity.this, "Falta dígitos nesse CEP, tente novamente.", Toast.LENGTH_SHORT).show();

                    } else if("".equals(bairro)){
                        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                        dlg.setMessage("A cidade correspondente a este CEP é " +localidade+ ", do estado de " +estado+ " porém o bairro não foi encontrado.");
                        dlg.setNeutralButton("OK",null);
                        dlg.show();
                    }else{
                        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                        dlg.setMessage("A cidade correspondente a este CEP é " +localidade+ ", do estado de " +estado+ ", bairro " +bairro+".");
                        dlg.setNeutralButton("OK",null);
                        dlg.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "CEP inválido, tente novamente.", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("LOG", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }
}
