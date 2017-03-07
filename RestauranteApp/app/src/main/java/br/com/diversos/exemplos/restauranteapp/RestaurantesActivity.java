package br.com.diversos.exemplos.restauranteapp;

import android.app.TabActivity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.diversos.exemplos.restauranteapp.modelo.Restaurante;

/**
 * Referência: blog http://www.rafaeltoledo.net/
 ***/
// Continuar a partir de http://www.rafaeltoledo.net/tutorial-android-7-menus-e-mensagens/
public class RestaurantesActivity extends TabActivity {

    private List<Restaurante> listaRestaurantes = new ArrayList<Restaurante>();
    private AdaptadorRestaurante adaptador = null;
    private static Map<String, Integer> iconeMapa = new HashMap<String, Integer>();

    // Valores para edição
    private EditText nome = null;
    private EditText endereco = null;
    private RadioGroup tipos = null;

    private static int ABA_LISTAGEM = 0;
    private static int ABA_DETALHES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantes);

        // Define o cursor no campo específico
        ((EditText) findViewById(R.id.nome)).requestFocus();

        Button salvar = (Button) findViewById(R.id.salvar);
        salvar.setOnClickListener(onSave); // Adiciona um listener para o evento clicar

        Button limpar = (Button) findViewById(R.id.limpar);
        limpar.setOnClickListener(onClear);

        ListView lista = (ListView) findViewById(R.id.restaurantes);
        adaptador = new AdaptadorRestaurante();
        lista.setAdapter(adaptador);
        lista.setOnItemClickListener(onItemClickListener); // Adiciona o listener para edição de registros

        // Define um mapa com os valores referentes aos ícones
        iconeMapa.put("rodizio", R.drawable.rodizio);
        iconeMapa.put("fast_food", R.drawable.fast_food);
        iconeMapa.put("a_domicilio", R.drawable.entrega);

        // Define os atributos para edição
        nome = (EditText) findViewById(R.id.nome);
        endereco = (EditText) findViewById(R.id.end);
        tipos = (RadioGroup) findViewById(R.id.tipos);

        // Controle das abas
        TabHost.TabSpec descritor = getTabHost().newTabSpec("tag1");
        descritor.setContent(R.id.restaurantes);
        Drawable d1 = ContextCompat.getDrawable(this, R.drawable.lista);
        descritor.setIndicator("Lista", d1);
        getTabHost().addTab(descritor);

        descritor = getTabHost().newTabSpec("tag2");
        descritor.setContent(R.id.detalhes);

        Drawable d2 = ContextCompat.getDrawable(this, R.drawable.restaurante);
        descritor.setIndicator("Detalhes", d2);
        getTabHost().addTab(descritor);

        getTabHost().setCurrentTab(ABA_LISTAGEM); // Define a aba inicial
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Recupera o registro selecionado na lista
            Restaurante selecionado = listaRestaurantes.get(position);
            nome.setText(selecionado.getNome());
            endereco.setText(selecionado.getEndereco());

            if (selecionado.getTipo().equalsIgnoreCase("rodizio")) {
                tipos.check(R.id.rodizio);
            } else if (selecionado.getTipo().equalsIgnoreCase("fast_food")) {
                tipos.check(R.id.fast_food);
            } else {
                tipos.check(R.id.a_domicilio);
            }

            getTabHost().setCurrentTab(ABA_DETALHES);
        }
    };

    private View.OnClickListener onClear = new View.OnClickListener() {
        public void onClick(View arg0) {
            // Limpa os valores do formulário
            limpar();
        }
    };

    private View.OnClickListener onSave = new View.OnClickListener() {

        private EditText nome = null;
        private EditText endereco = null;
        RadioGroup tipos = null;

        public void onClick(View arg0) {
            Restaurante r = new Restaurante(); // Representa o restaurante a ser cadastrado

            // Recupera valores da tela
            nome = (EditText) findViewById(R.id.nome);
            endereco = (EditText) findViewById(R.id.end);

            /**
             * Caso o nome a ser salvo contenha valores que existam na listagem,
             * remove o registro da listagem e cadastra o novo valor
             * Caso contrário, permite adicionar um novo item na lista
             **/
            for (int restauranteIndex = 0; restauranteIndex < adaptador.getCount(); restauranteIndex++) {
                Restaurante existente = (Restaurante) adaptador.getItem(restauranteIndex);
                if (nome.getText().toString().toUpperCase().contains(existente.getNome().toUpperCase())) {
                    listaRestaurantes.remove(restauranteIndex);
                    break;
                }
            }

            // Define os valores recuperados da tela no objeto 'restaurante'
            r.setNome(nome.getText().toString());
            r.setEndereco(endereco.getText().toString());

            tipos = (RadioGroup) findViewById(R.id.tipos);

            switch (tipos.getCheckedRadioButtonId()) {
                case R.id.rodizio:
                    r.setTipo("rodizio");
                    break;
                case R.id.fast_food:
                    r.setTipo("fast_food");
                    break;
                case R.id.a_domicilio:
                    r.setTipo("a_domicilio");
                    break;
            }

            adaptador.add(r);

            // Limpa os valores do formulário
            limpar();

            // Exibe a aba de listagem
            getTabHost().setCurrentTab(ABA_LISTAGEM);
        }
    };

    private void limpar() {
        nome.setText("");
        nome.requestFocus(); // Define o cursor
        endereco.setText("");
        tipos.clearCheck();
    }

    class AdaptadorRestaurante extends ArrayAdapter {
        AdaptadorRestaurante() {
            super(RestaurantesActivity.this, android.R.layout.simple_list_item_1,
                    listaRestaurantes);
        }

        /**
         * Método responsável por exibir os itens na tela
         * Referência: http://www.rafaeltoledo.net/tutorial-android-5-aprimorando-a-lista/
         * **/
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View linha = convertView; // Representa uma linha da listagem apresentada na tela
            ArmazenadorRestaurante armazenador = null;

            if (linha == null) {
                LayoutInflater inflater = getLayoutInflater();
                linha = inflater.inflate(R.layout.activity_linha, parent, false);
                armazenador = new ArmazenadorRestaurante(linha);
                linha.setTag(armazenador);
            } else {
                armazenador = (ArmazenadorRestaurante) linha.getTag();
            }

            armazenador.popularFormulario(listaRestaurantes.get(position));

            return linha;
        }
    }

    static class ArmazenadorRestaurante {
        private TextView nome = null;
        private TextView endereco = null;
        private ImageView icone = null;

        ArmazenadorRestaurante(View linha) {
            nome = (TextView) linha.findViewById(R.id.titulo);
            endereco = (TextView) linha.findViewById(R.id.endereco);
            icone = (ImageView) linha.findViewById(R.id.icone);
        }

        void popularFormulario(Restaurante r) {
            nome.setText(r.getNome());
            endereco.setText(r.getEndereco());
            icone.setImageResource(iconeMapa.get(r.getTipo()));
        }
    }
}
