package no.domain.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Felipe on 06/06/2016.
 */
public class BoaViagemActivity extends Activity {

    private static final String MANTER_CONECTADO = "manter_conectado";
    private EditText usuario, senha;
    private CheckBox manterConectado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);

        // Recupera referências dos campos de textos
        usuario = (EditText) this.findViewById(R.id.usuario);
        senha = (EditText) this.findViewById(R.id.senha);
        manterConectado = (CheckBox) findViewById(R.id.manterConectado);

        SharedPreferences preferencias = getPreferences(MODE_PRIVATE);
        boolean conectado = preferencias.getBoolean(MANTER_CONECTADO, false);
        if (conectado) {
            this.startActivity(new Intent(this, DashboardActivity.class));
        }

    }

    public void entrarOnClick(View v) {
        String usuarioInformado = usuario.getText().toString();
        String senhaInformada = senha.getText().toString();

        if ("leitor".equalsIgnoreCase(usuarioInformado)
                && "123".equals(senhaInformada)) {

            SharedPreferences preferencias = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putBoolean(MANTER_CONECTADO, manterConectado.isChecked());
            editor.commit();

            // vai para outra activity
            this.startActivity(new Intent(this, DashboardActivity.class));
        } else {
            String mensagemErro = this.getString(R.string.erro_autenticacao);
            Toast toast = Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}