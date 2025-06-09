package com.example.petshop_teste02;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.petshop_teste02.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FormLogin extends AppCompatActivity {

    private EditText edit_email;
    private EditText edit_senha;
    private Button bt_entrar;
    private ProgressBar progressBar;
    String[] mensagens = {"Preencha todos os campos", "Pronto! Login feito com sucesso."};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.logo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

           //código abaixo é para remover a barra de ação.

            //getSupportActionBar().hide(); Isso esconde a barra de ação do app
            InicializarComponentes();
            //evento de clique no botao "Entrar"
            bt_entrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = edit_email.getText().toString();
                    String senha = edit_senha.getText().toString();

                    if (email.isEmpty() || senha.isEmpty()) {
                        Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    } else {
                        AutenticarUsuario(v);
                    }
                }
            });
            return insets;
        });
    } //FIM DO ONCREATE

    //função usada no texto "cadastrar", id:text_tela_cadastro
    public void trocar_para_Tela_Cadastro(View v){
        Intent intent = new Intent(this, Cadastro.class);
        startActivity(intent);
    }//FIM DO trocar_para_Tela_Cadastro

    private void AutenticarUsuario(View view) {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        //autenticando o usuario.
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //task autentica.
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TelaCatalogo();
                        }
                    }, 4000); //tempo de carregamento do progress.
                } else {
                    String erro;

                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        erro = "Erro ao logar usuário";
                    }
                    Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    //função para ao abrir o aplicativo novamente, não precisar fazer o login novamente.
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser(); //usuario atual

        if (usuarioAtual != null) {
            TelaCatalogo();//TROQUE PARA A TELA DE CATALOGO.
        }
    }

    private void TelaCatalogo() {
        //Trocar para a tela de produtos quando estiver disponivel.
        Intent intent = new Intent(FormLogin.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void InicializarComponentes() {
        edit_email = findViewById(R.id.campoEmailLogin);
        edit_senha = findViewById(R.id.campoSenhaLogin);
        bt_entrar = findViewById(R.id.botaoEntrarLogin);
        progressBar = findViewById(R.id.barra_de_progresso_login);

    }

}//FIM DO FormLogin



