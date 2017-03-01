package no.domain.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import no.domain.boaviagem.constantes.Constantes;
import no.domain.boaviagem.domain.Gasto;
import no.domain.boaviagem.domain.Viagem;
import no.domain.boaviagem.repository.BoaViagemRepository;

/**
 * Created by Felipe on 06/06/2016.
 */
public class GastoActivity extends Activity {

    private int ano, mes, dia;
    private Button dataGasto;
    private Spinner categoria;
    private String viagemId;
    private Viagem viagem;
    private Date dtGasto;

    private BoaViagemRepository boaViagemRepository;
    private EditText valorGasto, descricaoGasto, localGasto;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private String id;

    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gasto);

        boaViagemRepository = new BoaViagemRepository(this);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        // Inicializa 'dtGasto' com a data atual
        dtGasto = new Date();

        dataGasto = (Button) findViewById(R.id.data);
        dataGasto.setText(dia + "/" + (mes + 1) + "/" + ano);

        valorGasto = (EditText) this.findViewById(R.id.valor);
        descricaoGasto = (EditText) this.findViewById(R.id.descricao);
        localGasto = (EditText) this.findViewById(R.id.local);

        // Define os valores a serem apresentados no list box 'categoria'
        adapter = ArrayAdapter.createFromResource(this, R.array.categoria_gasto, android.R.layout.simple_spinner_item);
        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapter);

        viagemId = this.getIntent().getStringExtra(Constantes.VIAGEM_ID);
        this.definirViagem();
        if (viagem != null) {
            TextView destino = (TextView) this.findViewById(R.id.destino);
            destino.setText(viagem.getDestino());
        }

        id = this.getIntent().getStringExtra(Constantes.GASTO_ID);
        if (id != null) {
            this.prepararEdicao();
        }
    }

    private void definirViagem() {
        if (viagemId != null
                && !"".equals(viagemId)) {
            this.viagem = boaViagemRepository.buscarViagemPorId(Integer.parseInt(viagemId));
        }
    }

    public void registrarGasto(View view) {
        Gasto gasto = new Gasto();
        gasto.setData(this.dtGasto);
        gasto.setDescricao(this.descricaoGasto.getText().toString());
        gasto.setLocal(this.localGasto.getText().toString());
        gasto.setValor(Double.parseDouble(this.valorGasto.getText().toString()));
        gasto.setViagemId(Integer.parseInt(this.viagem.getId().toString()));

        System.out.println("---------------- categoria selecionada:" + categoria.getSelectedItem().toString());
        gasto.setCategoria(this.categoria.getSelectedItem().toString());

        if (id != null) {
            gasto.setId(Long.parseLong(id));
        }

        Long resultado = boaViagemRepository.inserir(gasto);

        if (resultado != -1) {
            Toast.makeText(this, getString(R.string.gasto_salvo), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, GastoListActivity.class);
            intent.putExtra(Constantes.VIAGEM_ID, viagemId);
            this.startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.gasto_erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }

    public void selecionarData(View view) {
        this.showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (R.id.data == id) {
            return new DatePickerDialog(this, this.listener, ano, mes, dia);
        }
        return null;
    }

    /**
     * Responsável por identificar o valor definido para data de gasto
     */
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataGasto.setText(dia + "/" + (mes + 1) + "/" + ano);

            try {
                dtGasto = sdf.parse(dataGasto.getText().toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remover:
                //remover gasto do banco de dados
                return true;

            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    private void prepararEdicao() {
        Gasto gasto = boaViagemRepository.buscarGastoPorId(Integer.valueOf(id));

        if (gasto != null) {
            int categoriaPosition = this.adapter.getPosition(gasto.getCategoria());
            this.categoria.setSelection(categoriaPosition);
            this.valorGasto.setText(gasto.getValor().toString());
            this.dtGasto = gasto.getData();
            this.dataGasto.setText(sdf.format(this.dtGasto));
            this.descricaoGasto.setText(gasto.getDescricao());
            this.localGasto.setText(gasto.getLocal());
        } else {
            // Exibir mensagem
            Toast.makeText(this, this.getString(R.string.gasto_nao_encontrado), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        boaViagemRepository.close();
        super.onDestroy();
    }
}