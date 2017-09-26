package inacap.otto.seguroapp;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txv_patente;
    private TextView txv_valorseguro;
    private TextView txv_esasegurable;
    private TextView txv_marca;
    private TextView txv_modelo;
    private TextView txv_anho;
    private TextView txv_valoruf;
    private ImageView imv_resultado;
    private Button btn_volver;
    private TextView txv_antiguedad;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        bundle = this.getIntent().getExtras();
        setTitle("Seguro App - Asegurate!");
        txv_patente = (TextView) findViewById(R.id.txv_patente);
        txv_marca = (TextView) findViewById(R.id.txv_marca);
        txv_modelo = (TextView) findViewById(R.id.txv_modelo);
        txv_anho = (TextView) findViewById(R.id.txv_anho);
        txv_valoruf = (TextView) findViewById(R.id.txv_valoruf);
        txv_esasegurable = (TextView) findViewById(R.id.txv_esasegurable);
        txv_valorseguro = (TextView) findViewById(R.id.txv_valorseguro);
        imv_resultado = (ImageView) findViewById(R.id.imv_resultado);
        btn_volver = (Button) findViewById(R.id.btn_volver);
        btn_volver.setOnClickListener(this);
        txv_antiguedad = (TextView) findViewById(R.id.txv_antiguedad);

        String patente = bundle.getString("patente");
        String marca = bundle.getString("marca");
        String modelo = bundle.getString("modelo");
        String anho = bundle.getString("anho");
        Integer antiguedad = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(anho);
        if(antiguedad==0){
            antiguedad = 1;
        }
        Double valorUf = bundle.getDouble("valoruf");
        Double valorSeguro = bundle.getDouble("valorSeguro");
        String esAsegurable = bundle.getString("esAsegurable");

        txv_patente.setText(patente);
        txv_marca.setText(marca);
        txv_modelo.setText(modelo);
        txv_anho.setText(anho);
        txv_valoruf.setText("$"+valorUf.toString());
        txv_valorseguro.setText("$"+valorSeguro.toString());
        txv_esasegurable.setText(esAsegurable);
        txv_antiguedad.setText(antiguedad + " año(s)");
        if(esAsegurable.equals("ES ASEGURABLE")){
            imv_resultado.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.asegurado));
            Toast.makeText(getApplicationContext(), "ES ASEGURABLE", Toast.LENGTH_SHORT).show();
        } else {
            imv_resultado.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_noasegurado));
            Toast.makeText(getApplicationContext(), "NO ES ASEGURABLE", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        volver(bundle);
    }

    private void volver(Bundle bundle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("patente", bundle.getString("patente"));
        intent.putExtra("marca", bundle.getString("marca"));
        intent.putExtra("modelo", bundle.getString("modelo"));
        intent.putExtra("anho", bundle.getString("anho"));
        intent.putExtra("valoruf", bundle.getDouble("valoruf"));
        startActivity(intent);
    }
}
