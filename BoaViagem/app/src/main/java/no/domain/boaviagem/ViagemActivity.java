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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import no.domain.boaviagem.constantes.Constantes;
import no.domain.boaviagem.domain.Viagem;
import no.domain.boaviagem.repository.BoaViagemRepository;

/**
 * Created by Felipe on 06/06/2016.
 */
public class ViagemActivity extends Activity {

    private int ano, mes, dia;
    private Button dataSaidaBt, dataChegadaBt;
    private Date dtSaida, dtChegada;

    //private DatabaseHelper helper;
    private BoaViagemRepository boaViagemRepository;
    private EditText destino, quantidadePessoas, orcamento;
    private RadioGroup radioGroup;

    private String id;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.viagem);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        // Inicializa com a data atual as datas 'chegada' e 'saida'
        dtSaida = new Date();
        dtChegada = new Date();

        dataSaidaBt = (Button) this.findViewById(R.id.dataSaida);
        dataSaidaBt.setText(dia + "/" + (mes + 1) + "/" + ano);

        dataChegadaBt = (Button) this.findViewById(R.id.dataChegada);
        dataChegadaBt.setText(dia + "/" + (mes + 1) + "/" + ano);

        destino = (EditText) this.findViewById(R.id.destino);
        quantidadePessoas = (EditText) this.findViewById(R.id.quantidadePessoas);
        orcamento = (EditText) findViewById(R.id.orcamento);
        radioGroup = (RadioGroup) findViewById(R.id.tipoViagem);

        // prepara acesso ao banco de dados
        //helper = new DatabaseHelper(this);
        boaViagemRepository = new BoaViagemRepository(this);

        id = this.getIntent().getStringExtra(Constantes.VIAGEM_ID);
        if (id != null) {
            this.prepararEdicao();
        }
    }

    private void prepararEdicao() {
        /*SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tipo_viagem, destino, data_chegada, "
                + "data_saida, quantidade_pessoas, orcamento "
                + "FROM viagem WHERE _id = ?", new String[]{
                id
        });
        cursor.moveToFirst();

        if (cursor.getInt(0) == Constantes.VIAGEM_LAZER) {
            radioGroup.check(R.id.lazer);
        } else {
            radioGroup.check(R.id.negocios);
        }
        destino.setText(cursor.getString(1));
        dtChegada = new Date(cursor.getLong(2));
        dtSaida = new Date(cursor.getLong(3));
        dataChegadaBt.setText(sdf.format(dtChegada));
        dataSaidaBt.setText(sdf.format(dtSaida));
        quantidadePessoas.setText(cursor.getString(4));
        orcamento.setText(cursor.getString(5));

        cursor.close();*/

        Viagem viagem = boaViagemRepository.buscarViagemPorId(Integer.valueOf(id));

        if (viagem != null) {
            if (viagem.getTipoViagem() == Constantes.VIAGEM_LAZER) {
                radioGroup.check(R.id.lazer);
            } else {
                radioGroup.check(R.id.negocios);
            }
            destino.setText(viagem.getDestino());
            dtChegada = viagem.getDataChegada();
            dtSaida = viagem.getDataSaida();
            dataChegadaBt.setText(sdf.format(dtChegada));
            dataSaidaBt.setText(sdf.format(dtSaida));
            quantidadePessoas.setText(viagem.getQuantidadePessoas().toString());
            orcamento.setText(viagem.getOrcamento().toString());
        } else {
            // Exibir mensagem
            Toast.makeText(this, this.getString(R.string.viagem_nao_encontrada), Toast.LENGTH_SHORT).show();
        }
    }

    public void selecionarData(View view) {
        this.showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case R.id.dataSaida:
                return new DatePickerDialog(this, dataSaidaListener, ano, mes, dia);

            case R.id.dataChegada:
                return new DatePickerDialog(this, dataChegadaListener, ano, mes, dia);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viagem_menu, menu);
        return true;
    }

    private DatePickerDialog.OnDateSetListener dataChegadaListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
            dataChegadaBt.setText(diaSelecionado + "/" + (mesSelecionado + 1) + "/" + anoSelecionado);

            try {
                dtChegada = sdf.parse(dataChegadaBt.getText().toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println(" -------------- dtChegada: " + dtChegada);
        }
    };

    private DatePickerDialog.OnDateSetListener dataSaidaListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {
            dataSaidaBt.setText(diaSelecionado + "/" + (mesSelecionado + 1) + "/" + anoSelecionado);

            try {
                dtSaida = sdf.parse(dataSaidaBt.getText().toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println(" ----------- dtSaida: " + dtSaida);
        }
    };

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.novo_gasto:
                this.startActivity(new Intent(this, GastoActivity.class));
                return true;

            case R.id.remover:
                //remover viagem do banco de dados
                return true;

            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    public void salvarViagem(View view) {
        /*SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("destino", destino.getText().toString());
        values.put("data_chegada", dtChegada.getTime());
        values.put("data_saida", dtSaida.getTime());
        values.put("orcamento", orcamento.getText().toString());
        values.put("quantidade_pessoas", quantidadePessoas.getText().toString());

        int tipo = radioGroup.getCheckedRadioButtonId();
        if (tipo == R.id.lazer) {
            values.put("tipo_viagem", Constantes.VIAGEM_LAZER);
        } else {
            values.put("tipo_viagem", Constantes.VIAGEM_NEGOCIOS);
        }*/

        System.out.println(" ##### dtChegada: " + sdf.format(dtChegada));
        System.out.println(" ##### dtSaida: " + sdf.format(dtSaida));

        Viagem viagem = new Viagem();
        viagem.setDestino(destino.getText().toString());
        viagem.setDataChegada(new Date(dtChegada.getTime()));
        viagem.setDataSaida(new Date(dtSaida.getTime()));
        viagem.setOrcamento(Double.valueOf(orcamento.getText().toString()));
        viagem.setQuantidadePessoas(Integer.parseInt(quantidadePessoas.getText().toString()));

        int tipo = radioGroup.getCheckedRadioButtonId();
        if (tipo == R.id.lazer) {
            viagem.setTipoViagem(Constantes.VIAGEM_LAZER);
        } else {
            viagem.setTipoViagem(Constantes.VIAGEM_NEGOCIOS);
        }

        if (id != null) {
            viagem.setId(Long.parseLong(id));
        }

        Long resultado = boaViagemRepository.inserir(viagem);

        if (resultado != -1) {
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
            this.startActivity(new Intent(this, ViagemListActivity.class));
        } else {
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        boaViagemRepository.close();
        super.onDestroy();
    }
}
