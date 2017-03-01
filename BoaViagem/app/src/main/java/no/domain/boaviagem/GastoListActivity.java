package no.domain.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.domain.boaviagem.constantes.Constantes;
import no.domain.boaviagem.domain.Gasto;
import no.domain.boaviagem.domain.Viagem;
import no.domain.boaviagem.repository.BoaViagemRepository;

/**
 * Created by Felipe on 06/06/2016.
 */
public class GastoListActivity extends ListActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {

    private AlertDialog alertDialog, dialogConfirmacao;
    private List<Map<String, Object>> gastos;
    private String dataAnterior = "";
    private int gastoSelecionado;

    private String viagemId;
    private Viagem viagem;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private BoaViagemRepository boaViagemRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boaViagemRepository = new BoaViagemRepository(this);

        /*setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.listarGastos()));
        ListView listView = getListView();
        listView.setOnItemClickListener(this);*/
        String[] de = {"data", "descricao", "valor", "categoria"};
        int[] para = {R.id.data, R.id.descricao, R.id.valor, R.id.categoria};

        viagemId = this.getIntent().getStringExtra(Constantes.VIAGEM_ID);
        this.definirViagem();

        SimpleAdapter adapter = new SimpleAdapter(this, this.listarGastos(), R.layout.lista_gasto, de, para);
        adapter.setViewBinder(new GastoViewBinder());
        this.setListAdapter(adapter);
        this.getListView().setOnItemClickListener(this);

        // Define o menu de contexto a ser exibido quando um item da listagem for selecionado
        this.registerForContextMenu(this.getListView());

        this.alertDialog = this.criaAlertDialog();
        this.dialogConfirmacao = this.criaDialogConfirmacao();
    }

    private AlertDialog criaAlertDialog() {
        final CharSequence[] items = {getString(R.string.editar), getString(R.string.remover), getString(R.string.novo_gasto),
                getString(R.string.minhas_viagens), getString(R.string.menu_principal)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.opcoes);
        builder.setItems(items, this);
        return builder.create();
    }

    private AlertDialog criaDialogConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmacao_exclusao_gasto);
        builder.setPositiveButton(getString(R.string.sim), this);
        builder.setNegativeButton(getString(R.string.nao), this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Intent intent;
        String id = String.valueOf(gastos.get(gastoSelecionado).get("id"));

        System.out.println("--- viagem relacionada:\n" + this.viagem);

        switch (item) {
            case 0: // Editar o gasto selecionado
                intent = new Intent(this, GastoActivity.class);
                intent.putExtra(Constantes.GASTO_ID, id);
                intent.putExtra(Constantes.VIAGEM_ID, String.valueOf(viagem.getId()));
                this.startActivity(intent);
                break;

            case 1:
                this.dialogConfirmacao.show();
                break;

            case DialogInterface.BUTTON_POSITIVE: // Excluir informações
                gastos.remove(gastoSelecionado);
                this.removerGasto(id);
                this.getListView().invalidateViews();
                Toast.makeText(this, this.getString(R.string.gasto_removido), Toast.LENGTH_SHORT).show();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                dialogConfirmacao.dismiss();
                break;

            case 2: // Definir um novo gasto
                intent = new Intent(this, GastoActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, String.valueOf(viagem.getId()));
                this.startActivity(intent);
                break;

            case 3: // Exibir lista de viagens
                this.startActivity(new Intent(this, ViagemListActivity.class));
                break;

            case 4: // Voltar para a tela principal
                this.startActivity(new Intent(this, DashboardActivity.class));
                break;
        }
    }

    private void removerGasto(String id) {
        boaViagemRepository.removerGasto(Long.parseLong(id)); // Remove gastos
    }

    private void definirViagem() {
        if (viagemId != null
                && !"".equals(viagemId)) {
            this.viagem = boaViagemRepository.buscarViagemPorId(Integer.parseInt(viagemId));
            System.out.println(" ---------- Viagem identificada:\n" + this.viagem);
        }
    }

    /**
     * Utilizado para exibir o menu com opções ao selecionar um item da lista
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*TextView textView = (TextView) view;
        Toast.makeText(this, "Gasto selecionado: " + textView.getText(), Toast.LENGTH_SHORT).show();*/
        /*Map<String, Object> map = gastos.get(position);
        String descricao = (String) map.get("descricao");
        String mensagem = "Gasto selecionado: " + descricao;
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();*/

        // Preparar para edição do item selecionado
        this.gastoSelecionado = position;
        this.alertDialog.show();
    }

    private List<Map<String, Object>> listarGastos() {
        gastos = new ArrayList<Map<String, Object>>();

        if (viagem != null) {
            for (Gasto gasto : boaViagemRepository.listarGastos(viagem)) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("data", sdf.format(gasto.getData()));
                item.put("descricao", gasto.getDescricao());
                item.put("valor", gasto.getValor());
                item.put("id", gasto.getId());

                switch (gasto.getCategoria()) {
                    case "Alimentação":
                        item.put("categoria", R.color.categoria_alimentacao);
                        break;

                    case "Combustível":
                        item.put("categoria", R.color.categoria_combustivel);
                        break;

                    case "Transporte":
                        item.put("categoria", R.color.categoria_transporte);
                        break;

                    case "Hospedagem":
                        item.put("categoria", R.color.categoria_hospedagem);
                        break;

                    default:
                        item.put("categoria", R.color.categoria_outros);
                        break;
                }

                gastos.add(item);
            }
        }
        return gastos;
    }

    private class GastoViewBinder implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.getId() == R.id.data) {
                if (!dataAnterior.equals(data)) {
                    TextView textView = (TextView) view;
                    textView.setText(textRepresentation);
                    dataAnterior = textRepresentation;
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                return true;
            }

            if (view.getId() == R.id.categoria) {
                Integer id = (Integer) data;
                view.setBackgroundColor(getResources().getColor(id));
                return true;
            }
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.remover) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            gastos.remove(info.position);
            getListView().invalidateViews();
            dataAnterior = "";
            // remover do banco de dados
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        boaViagemRepository.close();
        super.onDestroy();
    }
}