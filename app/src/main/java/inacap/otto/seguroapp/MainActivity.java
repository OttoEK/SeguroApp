package inacap.otto.seguroapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String ES_ASEGURABLE = "ES ASEGURABLE";
    public static final String NO_ES_ASEGURABLE = "NO ES ASEGURABLE";
    public static final String SEGURO_APP_ASEGURATE = "Seguro App - Asegurate!";
    private EditText edtPatente;
    //private EditText edtMarca;
    private EditText edtModelo;
    private EditText edtAnho;
    private EditText edtValoruf;
    private Button btnCalcular;
    private Spinner spnMarca;
    private ArrayAdapter adapter;
    JSONObject objetoJSON;
    String marcaFabricante;
    ArrayList<String> listaMarcas = new ArrayList<String>();

    private static final String URL_STRING = "https://raw.githubusercontent.com/OttoEK/OttoEK.github.io/master/automarcas.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtPatente = (EditText) findViewById(R.id.edt_patente);
        //edtMarca = (EditText) findViewById(R.id.edt_marca);
        edtModelo = (EditText) findViewById(R.id.edt_modelo);
        edtAnho = (EditText) findViewById(R.id.edt_anho);
        edtValoruf = (EditText) findViewById(R.id.edt_valoruf);
        btnCalcular = (Button) findViewById(R.id.btn_calcular);
        btnCalcular.setOnClickListener(this);
        spnMarca = (Spinner) findViewById(R.id.edt_marca);
        //adaptador para spinner
        List<String> listMarcas = new ArrayList<>();
        listMarcas.add("Ford");
        listMarcas.add("Toyota");
        listMarcas.add("Fiat");
        listMarcas.add("Renault");
        listMarcas.add("Chevrolet");
        listMarcas.add("Otras");

        adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, listMarcas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spnMarca.setAdapter(adapter);
        spnMarca.setOnItemSelectedListener(this);

        this.restaurarDatos();
        setTitle(SEGURO_APP_ASEGURATE);

        //A través del métdo execute ejecutamos ta tarea asíncrona
        new ProcessJSON().execute(URL_STRING);
    }

    private void restaurarDatos() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            Double rValorUF = bundle.getDouble("valoruf");
            edtPatente.setText(bundle.getString("patente"));
            //edtMarca.setText(bundle.getString("marca"));
            spnMarca.setSelection(adapter.getPosition(bundle.getString("marca")));
            edtModelo.setText(bundle.getString("modelo"));
            edtAnho.setText(bundle.getString("anho"));
            edtValoruf.setText("" + rValorUF);
        }
    }

    public void calcularSeguro() {

        if (!this.validarCampos(edtPatente, edtModelo, edtAnho, edtValoruf)) {
            return;
        } else if (Integer.parseInt(edtAnho.getText().toString())> Calendar.getInstance().get(Calendar.YEAR)) {
            Toast.makeText(this, "Año incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        int antiguedad = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(edtAnho.getText().toString());
        String validez = (antiguedad>10 ? NO_ES_ASEGURABLE : ES_ASEGURABLE);
        double valorSeguro = 0d;
        if(antiguedad<=10){
            antiguedad = antiguedad<1?1:antiguedad;
            valorSeguro = 0.1* Double.parseDouble(edtValoruf.getText().toString()) *antiguedad;
        }

        Intent intent = new Intent(this, ResultActivity.class);
        /* patente, marca, modelo, año, valor uf, asegurable, valor seguro)*/
        intent.putExtra("patente", edtPatente.getText().toString());
//        intent.putExtra("marca", edtMarca.getText().toString());
        intent.putExtra("marca", spnMarca.getSelectedItem().toString());
        intent.putExtra("modelo", edtModelo.getText().toString());
        intent.putExtra("anho", edtAnho.getText().toString());
        intent.putExtra("valoruf", Double.parseDouble(edtValoruf.getText().toString()));
        intent.putExtra("valorSeguro", valorSeguro);
        intent.putExtra("esAsegurable", validez);
        startActivity(intent);
    }

    private boolean validarCampos(EditText... args) {
        for (EditText editText : args) {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(this, editText.getHint() + " incorrecto", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        calcularSeguro();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Sub Clase asíncrona que permite ejecutar la consulta de datos a un servidor remoto
    private class ProcessJSON extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings){
            String stream = null;
            String urlString = strings[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            return stream;
        }

        protected void onPostExecute(String stream){

            if(stream !=null){
                try{

                    //Obtenemos los datos recibidos desde el servidor remoto y los incorporamos a un objeto JSONArray
                    JSONArray reader= new JSONArray(stream);

                    //Efectuamos un recorrido de los datos y se incorporan a una lista
                    for (int i = 0; i < reader.length(); i++) {
                        objetoJSON          = reader.getJSONObject(i);
                        marcaFabricante = objetoJSON.getString("make_display");
                        listaMarcas.add(marcaFabricante);


                    }

                    /*Una vez hecho el recorrido, se establece que la lista de regiones
                    será la fuente para el adaptador del Spinner.
                     */
                    spnMarca.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, listaMarcas));




                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        }
    }

}


