package com.example.petshop_teste02;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Cadastro extends AppCompatActivity {

    private EditText edit_nome;
    private EditText edit_email;
    private EditText edit_senha;
    private Button bt_cadastrar;

    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"}; //mensagens de sucesso e de error.
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);


            //adicionando os campos da interface(activity_cadastro.xml)
            // as variáveis.
            edit_nome = findViewById(R.id.edit_nome);
            edit_email = findViewById(R.id.edit_email);
            edit_senha = findViewById(R.id.edit_senha);
            bt_cadastrar = findViewById(R.id.bt_cadastrar);

            bt_cadastrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nome = edit_nome.getText().toString();
                    String email = edit_email.getText().toString();
                    String senha = edit_senha.getText().toString();

                    if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                        Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    } else {
                        CadastrarUsuario(v);
                    }
                }
            });

            return insets;
        });
    }//FIM do ONCREATE

    private void CadastrarUsuario(View v) {

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        //Iniciar o firebase para o cadastro | getIntance(): é a instancia do servidor Firebase | createUser...(): cadastra os usuários.
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            //O objeto task é responsável pelo cadastro e autenticação
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //se o cadastro for realizado com sucesso através da função .isSuccessuful() vamos mostrar a mensagem.
                //task: terá o resultado do cadastro.
                //Nesssa classe Cadastro.java a task terá função de cadastrar.
                //e na classe Login. terá a função de autenticação.
                if (task.isSuccessful()) {

                    //salvar dados que são além do provedor de e-mail e senha, salvar dados como nome, telefone e etc no banco.
                    SalvarDadosUsuario();

                    Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    //armazena todas as mensagens possíveis.
                    String erro;
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        //Exceção com senha menor que 6 caracteres.
                        erro = "Digite uma senha com no mínimo 6 caracteres";
                    } catch (FirebaseAuthUserCollisionException e) {
                        //Exceção de erro repetido.
                        erro = "Esta conta já foi cadastrada";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        //verifica se o email está correto.
                        erro = "E-mail inválido";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar usuário";
                    }

                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }//FIM DO ELSE
            }
        });
    }

    private void SalvarDadosUsuario() {
        String nome = edit_nome.getText().toString();
        //String telefone = edit_telefone.getText().toString();

        //iniciar o banco de dados FireStore.
        //recupera a instancia do servidor usando getIntance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //map de usuario: para nome, telefone e etc
        Map<String, Object> usuarios = new HashMap<>();
        //a chave é nome.
        usuarios.put("nome", nome);
        //caso queira adicionar mais:
        //usuario.put("telefone", telefone);

        //tem a instancia do banco, usuario atual e o ID do usuario.
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //cada usuario terá um documento especifico. Esse documento será referenciado pelo usuarioID.
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);

        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //O log descreverá o error
                Log.d("db","Sucesso ao salvar os dados");
            }
        })
          .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Log.d("db_erro", "Erro ao salvar os dados" + e.toString());
              }
          });
    }
}
/* Uma alternativa é criar uma função para os componentes.
    private void inicializarComponentes() {
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
    }
    */
