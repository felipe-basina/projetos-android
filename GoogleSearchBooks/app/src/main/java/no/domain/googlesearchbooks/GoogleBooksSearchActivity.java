package no.domain.googlesearchbooks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import no.domain.googlesearchbooks.utils.HTTPUtils;

public class GoogleBooksSearchActivity extends Activity {

    private ListView lista;
    private EditText texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.google_books_search);

        lista = (ListView) this.findViewById(R.id.lista);
        texto = (EditText) this.findViewById(R.id.texto);
    }

    public void buscar(View v) {
        String filtro = texto.getText().toString();
        new GoogleBooksTask().execute(filtro);
    }

    public void limpar(View v) {
        texto.setText(""); // Limpar a caixa de texto
        lista.setAdapter(null); // Limpar a lista de resultados
    }

    private class GoogleBooksTask extends AsyncTask<String, String, String[]> {

        ProgressDialog dialog;

        @Override
        protected String[] doInBackground(String... params) {
            try {

                String filtro = params[0];
                if (TextUtils.isEmpty(filtro)) {
                    return new String[] { "ERROR", "Entre com um nome de autor para pesquisa!" };
                }

                // Substitui espaços em branco pelo caractere %20
                filtro = filtro.replaceAll(" ", "%20");

                String urlGoogleBooks = "https://www.googleapis.com/books/v1/volumes?q=";
                String url = Uri.parse(urlGoogleBooks + filtro).toString();
                //System.out.println(" --- url: " + url);

                String conteudo = HTTPUtils.acessar(url);
               //System.out.println(" --- conteudo: " + conteudo);

                // Recupera o conteúdo de retorno
                JSONObject jsonObject = new JSONObject(conteudo);
                JSONArray resultados = jsonObject.getJSONArray("items");

                int totalRegistros = resultados.length();
                System.out.println(" ### Total de registros: " + totalRegistros);

                // Monta a lista final com as informações a serem exibidas
                String[] books = new String[resultados.length()];
                for (int i = 0; i < totalRegistros; i++) {
                    JSONObject book = resultados.getJSONObject(i);
                    //System.out.println(" --- book: " + book);

                    JSONObject info = book.getJSONObject("volumeInfo");
                    //System.out.println(" --- info: " + info);

                    String titulo = this.getContent(info, "title");
                    String anoLancamento = this.getContent(info, "publishedDate");

                    books[i] = anoLancamento + " - " + titulo;
                }

                String[] newArray = new String[books.length * 2];

                // Teste para adicionar mais itens
                /*for (int i = 0; i < newArray.length; i++) {
                    if (i >= totalRegistros) {
                        String titulo = "titulo " + i;
                        String anoLancamento = "01/" + i + "/2001";
                        newArray[i] = anoLancamento + " - " + titulo;
                    } else {
                        newArray[i] = books[i];
                    }
                }

                return newArray;*/

                return books;

            } catch (Exception e) {
                System.out.println("\nERRO:\n" + e.getMessage());
                return new String[] { "ERROR", "Nenhum registro foi encontrado!" };
            }
        }

        private String getContent(JSONObject object, String content) {
            String contentValue = "";
            try {
                contentValue = object.getString(content);
            } catch (Exception ex) {
                System.out.println("Conteudo não identificado content [" + content
                        + "] no  objeto\n object[" + object + "]");
            }
            return contentValue;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(GoogleBooksSearchActivity.this);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null
                    && !"ERROR".equalsIgnoreCase(result[0])) {
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getBaseContext(),
                                android.R.layout.simple_list_item_1, result);
                lista.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), result[1], Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        }
    }
}
