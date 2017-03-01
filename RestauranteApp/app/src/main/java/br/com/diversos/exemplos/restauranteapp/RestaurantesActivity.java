package br.com.diversos.exemplos.restauranteapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.diversos.exemplos.restauranteapp.modelo.Restaurante;

/**
 * Referência: blog http://www.rafaeltoledo.net/
 ***/
// Continuar a partir de http://www.rafaeltoledo.net/tutorial-android-6-utilizando-abas/
public class RestaurantesActivity extends AppCompatActivity {

    private List<Restaurante> listaRestaurantes = new ArrayList<Restaurante>();
    private AdaptadorRestaurante adaptador = null;
    private static Map<String, Integer> iconeMapa = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantes);

        // Define o cursor no campo específico
        ((EditText) findViewById(R.id.nome)).requestFocus();

        Button salvar = (Button) findViewById(R.id.salvar);
        salvar.setOnClickListener(onSave); // Adiciona um listener para o evento clicar

        ListView lista = (ListView) findViewById(R.id.restaurantes);
        adaptador = new AdaptadorRestaurante();
        lista.setAdapter(adaptador);

        // Define um mapa com os valores referentes aos ícones
        iconeMapa.put("rodizio", R.drawable.rodizio);
        iconeMapa.put("fast_food", R.drawable.fast_food);
        iconeMapa.put("a_domicilio", R.drawable.entrega);
    }

    private View.OnClickListener onSave = new View.OnClickListener() {

        private EditText nome = null;
        private EditText endereco = null;
        RadioGroup tipos = null;

        public void onClick(View arg0) {
            Restaurante r = new Restaurante(); // Representa o restaurante a ser cadastrado

            // Recupera valores da tela
            nome = (EditText) findViewById(R.id.nome);
            endereco = (EditText) findViewById(R.id.end);

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
            this.limpar();
        }

        public void limpar() {
            nome.setText("");
            nome.requestFocus(); // Define o cursor
            endereco.setText("");
            tipos.clearCheck();
        }
    };

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
