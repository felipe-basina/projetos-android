package no.domain.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Felipe on 06/06/2016.
 */
public class DashboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dashboard);
    }

    public void selecionarOpcao(View view) { /* com base na view que foi clicada iremos tomar a ação correta */
        /*TextView textView = (TextView) view;
        String opcao = "Opção: " + textView.getText().toString();
        Toast.makeText(this, opcao, Toast.LENGTH_LONG).show();*/
        switch (view.getId()) {

            case R.id.novo_gasto:
                this.startActivity(new Intent(this, GastoActivity.class));
                break;

            case R.id.nova_viagem:
                this.startActivity(new Intent(this, ViagemActivity.class));
                break;

            case R.id.minhas_viagens:
                this.startActivity(new Intent(this, ViagemListActivity.class));
                break;

            case R.id.configuracoes:
                this.startActivity(new Intent(this, ConfiguracoesActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        // Realiza o logoff do usuário
        this.finish();
        return true;
    }

}
