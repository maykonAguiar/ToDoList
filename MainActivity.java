package todolist.cursoandroid.com.todolist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity{

    private EditText textoTarefa;
    private Button botaoAdicionar;
    private ListView listaTarefa;
    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String > itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {


            textoTarefa = findViewById(R.id.textoId);
            botaoAdicionar = findViewById(R.id.botaoAdicionarId);

            //LISTA
            listaTarefa = findViewById(R.id.listViewId);


            //CRIAR BANCO DE DADOS - nome: apptarefas
            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            //CRIAR TABELA - tarefas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR )");

            //CRIAR O BOTAO SALVAR
            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TEXTO DIGITADO PELO USUARIO
                    String textoDigitado = textoTarefa.getText().toString();
                    salvarTarefa(textoDigitado);


                }
            });

            //CRIAR EVENTO DE CLIQUE PARA DELETAR TAREFA - NO LISTA TAREFA
            listaTarefa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    removerTarefa(ids.get(i));
                }
            });

            //CHAMAR METODO RECUPERAR TAREFA
            recuperarTarefas();


        }catch (Exception e){
            e.printStackTrace();
            //SE OCORRER ALGUM ERRO SERÁ EXECUTADO AQUI
        }
    } //FIM DO METODO ON CREATE

    //CRIAR UM MÉTODO PARA ADICIONAR AO BANCO DE DADOS

    private void salvarTarefa(String texto){

        try {

            if (texto.equals("")){
                Toast.makeText(MainActivity.this, "Digite uma tarefa", Toast.LENGTH_SHORT).show();
            }else {
                //ADICIONAR O TEXTO AO BANCO DE DADOS
                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES(' " + texto + " ')");
                Toast.makeText(MainActivity.this, "Tarefa salva com Sucesso!", Toast.LENGTH_SHORT).show();
                //ADICIONAR A NOVA TABELA CRIADA
                recuperarTarefas();
                //APAGAR O QUE FOI DIGITADO
                textoTarefa.setText("");
            }



        }catch (Exception e){
            e.printStackTrace();
        }



    }

    //METODO RECUPERAR TAREFAS
    private void recuperarTarefas(){
        try {

            //RECUPERAR DADOS DA TABELA - tarefas                   -ORDENAR POR ID DECRESCENTE
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //RECUPERAR OS IDS DAS COLUNAS
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");



            //CRIAR ADAPTADOR
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                    itens);
            listaTarefa.setAdapter(itensAdaptador);

            //LISTAR AS TAREFAS
            cursor.moveToFirst();

            while (cursor != null){

                Log.i("Resultado - "," Id Tarefa: " + cursor.getString(indiceColunaId) + " Tarefa: " + cursor.getString(indiceColunaTarefa ));
                //ADICIONAR ELEMENTOS DENTRO DO ARREY LIST
                itens.add(cursor.getString(indiceColunaTarefa));
                //RECUPERAR OS IDS GERADOS AUTOMATICAMENTE
                ids.add( Integer.parseInt( cursor.getString(indiceColunaId)));

                cursor.moveToNext();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //METODO REMOVER TAREFAS

    private void removerTarefa (Integer id){
        try {
            //DELETAR TAREFAS
            bancoDados.execSQL("DELETE FROM tarefas WHERE id=" + id);
            Toast.makeText(MainActivity.this, "Tarefa removida com Sucesso!", Toast.LENGTH_SHORT).show();
            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
