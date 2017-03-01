package no.domain.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.domain.boaviagem.constantes.Constantes;
import no.domain.boaviagem.domain.Viagem;
import no.domain.boaviagem.repository.BoaViagemRepository;

/**
 * Created by Felipe on 06/06/2016.
 */
public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {

    private AlertDialog alertDialog, dialogConfirmacao;
    private List<Map<String, Object>> viagens;
    private int viagemSelecionada;

    //private DatabaseHelper helper;
    private BoaViagemRepository boaViagemRepository;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Double valorLimite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.listarViagens()));
        ListView listView = getListView();
        listView.setOnItemClickListener(this);*/

        //helper = new DatabaseHelper(this);
        boaViagemRepository = new BoaViagemRepository(this);

        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        String valor = preferencias.getString("valor_limite", "-1");
        valorLimite = Double.valueOf(valor);

        String[] de = {"imagem", "destino", "data", "total", "barraProgresso"};
        int[] para = {R.id.tipoViagem, R.id.destino, R.id.data, R.id.valor, R.id.barraProgresso};

        SimpleAdapter adapter = new SimpleAdapter(this, this.listarViagens(), R.layout.lista_viagem, de, para);
        adapter.setViewBinder(new ViagemViewBinder());
        this.setListAdapter(adapter);
        this.getListView().setOnItemClickListener(this);

        // Define o menu de contexto a ser exibido quando um item da listagem for selecionado
        this.registerForContextMenu(this.getListView());

        this.alertDialog = this.criaAlertDialog();
        this.dialogConfirmacao = this.criaDialogConfirmacao();
    }

    private AlertDialog criaDialogConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmacao_exclusao_viagem);
        builder.setPositiveButton(getString(R.string.sim), this);
        builder.setNegativeButton(getString(R.string.nao), this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Intent intent;
        String id = (String) viagens.get(viagemSelecionada).get("id");

        switch (item) {
            case 0: // Editar a viagem selecionada
                intent = new Intent(this, ViagemActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                this.startActivity(intent);
                break;

            case 1: // Definir gasto para viagem selecionada
                //this.startActivity(new Intent(this, GastoActivity.class));
                intent = new Intent(this, GastoActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                this.startActivity(intent);
                break;

            case 2:
                System.out.println(" -------------------- Preparando para exibir a lista de gastos para viagem id: " + id);
                //this.startActivity(new Intent(this, GastoListActivity.class));
                intent = new Intent(this, GastoListActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                this.startActivity(intent);
                break;

            case 3:
                this.dialogConfirmacao.show();
                break;

            case DialogInterface.BUTTON_POSITIVE: // Excluir informações
                viagens.remove(viagemSelecionada);
                this.removerViagem(id);
                this.getListView().invalidateViews();
                Toast.makeText(this, this.getString(R.string.registro_removido), Toast.LENGTH_SHORT).show();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                dialogConfirmacao.dismiss();
                break;

            case 4:
                this.startActivity(new Intent(this, DashboardActivity.class));
                break;
        }
    }

    private void removerViagem(String id) {
        /*SQLiteDatabase db = helper.getWritableDatabase();
        String where[] = new String[] {
                id
        };
        db.delete(DatabaseHelper.GASTO_TBL, "viagem_id = ?", where); // Remove registros filhos: gastos
        db.delete(DatabaseHelper.VIAGEM_TBL, "_id = ?", where); // Remove registro pai: viagem*/
        System.out.println(" #### Preparando para remover viagem id = " + id);

        try {
            boolean removido = boaViagemRepository.removerGastosViagem(Long.parseLong(id)); // Remove gastos
            System.out.println("-------- Gastos removidos? " + removido);

            removido = boaViagemRepository.removerViagem(Long.parseLong(id)); // Remove viagem
            System.out.println("-------- Viagem removida? " + removido);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private AlertDialog criaAlertDialog() {
        final CharSequence[] items = {getString(R.string.editar), getString(R.string.novo_gasto), getString(R.string.gastos_realizados), getString(R.string.remover), getString(R.string.menu_principal)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.opcoes);
        builder.setItems(items, this);
        return builder.create();
    }

    private List<Map<String, Object>> listarViagens() {
        viagens = new ArrayList<Map<String, Object>>();

        for (Viagem viagem : boaViagemRepository.listarViagens()) {
            Map<String, Object> item = new HashMap<String, Object>();

            String id = viagem.getId().toString();
            int tipoViagem = viagem.getTipoViagem();
            String destino = viagem.getDestino();
            long dataChegada = viagem.getDataChegada().getTime();
            long dataSaida = viagem.getDataSaida().getTime();
            double orcamento = viagem.getOrcamento();

            item.put("id", id);

            if (tipoViagem == Constantes.VIAGEM_LAZER) {
                item.put("imagem", R.drawable.lazer);
            } else {
                item.put("imagem", R.drawable.negocios);
            }
            item.put("destino", destino);

            Date dataChegadaDate = new Date(dataChegada);
            Date dataSaidaDate = new Date(dataSaida);
            String periodo = dateFormat.format(dataChegadaDate) + " a " + dateFormat.format(dataSaidaDate);
            item.put("data", periodo);

            double totalGasto = boaViagemRepository.calcularTotalGasto(viagem);
            item.put("total", "Gasto total R$ " + totalGasto);

            double alerta = orcamento * valorLimite / 100;
            Double[] valores = new Double[] {
                    orcamento,
                    alerta,
                    totalGasto
            };
            item.put("barraProgresso", valores);

            viagens.add(item);
        }

        return viagens;
    }

    /*private List<Map<String, Object>> listarViagens() {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id, tipo_viagem, destino, " +
                "data_chegada, data_saida, orcamento FROM viagem", null);

        // move o cursor para o primeiro registro
        cursor.moveToFirst();

        viagens = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < cursor.getCount(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();

            String id = cursor.getString(0);
            int tipoViagem = cursor.getInt(1);
            String destino = cursor.getString(2);
            long dataChegada = cursor.getLong(3);
            long dataSaida = cursor.getLong(4);
            double orcamento = cursor.getDouble(5);

            item.put("id", id);

            if (tipoViagem == Constantes.VIAGEM_LAZER) {
                item.put("imagem", R.drawable.lazer);
            } else {
                item.put("imagem", R.drawable.negocios);
            }
            item.put("destino", destino);

            Date dataChegadaDate = new Date(dataChegada);
            Date dataSaidaDate = new Date(dataSaida);
            String periodo = dateFormat.format(dataChegadaDate) + " a " + dateFormat.format(dataSaidaDate);
            item.put("data", periodo);

            double totalGasto = this.calcularTotalGasto(db, id);
            item.put("total", "Gasto total R$ " + totalGasto);

            double alerta = orcamento * valorLimite / 100;
            Double[] valores = new Double[] {
                    orcamento,
                    alerta,
                    totalGasto
            };
            item.put("barraProgresso", valores);

            viagens.add(item);

            cursor.moveToNext();
        }
        cursor.close();

        return viagens;
    }*/

    /*private double calcularTotalGasto(SQLiteDatabase db, String id) {
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM gasto WHERE viagem_id = ?", new String[] {
                id
        });
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();
        return total;
    }*/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Map<String, Object> map = viagens.get(position);
        String destino = (String) map.get("destino");
        String mensagem = "Viagem selecionada: " + destino;
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
        this.startActivity(new Intent(this, GastoListActivity.class))*/
        ;
        this.viagemSelecionada = position;
        alertDialog.show();
    }

    private class ViagemViewBinder implements SimpleAdapter.ViewBinder {
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            if (view.getId() == R.id.barraProgresso) {
                Double valores[] = (Double[]) data;
                ProgressBar progressBar = (ProgressBar) view;
                progressBar.setMax(valores[0].intValue());
                progressBar.setSecondaryProgress(valores[1].intValue());
                progressBar.setProgress(valores[2].intValue());
                return true;
            }
            return false;
        }
    }
}
