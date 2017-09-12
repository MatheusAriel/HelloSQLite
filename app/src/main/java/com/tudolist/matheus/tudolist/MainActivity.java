package com.tudolist.matheus.tudolist;

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

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemLongClickListener
{

    private EditText edtTarefas;
    private Button btnAdicionar;
    private ListView lsvTarefas;
    private SQLiteDatabase bancoDados;
    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTarefas = (EditText) findViewById(R.id.edtTarefas);
        btnAdicionar = (Button) findViewById(R.id.btnAdd);
        lsvTarefas = (ListView) findViewById(R.id.lsvTarefas);


        try
        {
            //criar bd
            bancoDados = openOrCreateDatabase("app_tarefas", MODE_PRIVATE, null);

            //criar tabelas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR) ");

            btnAdicionar.setOnClickListener(this);
            lsvTarefas.setOnItemLongClickListener(this);

            recuperarTarefas();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.btnAdd)
        {
            String textoTarefa = edtTarefas.getText().toString();
            salvarTarefa(textoTarefa);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        if(adapterView.getId() == R.id.lsvTarefas)
        {
            removerTarefa(ids.get(i));
            return true;
        }
        return false;
    }

    private void salvarTarefa(String textoTarefa)
    {
        try
        {
            if(textoTarefa.length()>0)
            {
                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES ('" + textoTarefa + "')");
                Toast.makeText(getApplicationContext(), "Adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                edtTarefas.setText("");
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Por Favor, informe uma tarefa", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void recuperarTarefas()
    {
        try
        {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC",null);

            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_2,android.R.id.text2,itens);

            lsvTarefas.setAdapter(itensAdaptador);

            cursor.moveToFirst();

            while (cursor!=null)
            {
                Log.i("RESULTADO - ","Tarefa: "+cursor.getString(indiceColunaTarefa));
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt( cursor.getString(indiceColunaId) ));

                cursor.moveToNext();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void removerTarefa(Integer id)
    {
        try
        {
            bancoDados.execSQL("DELETE FROM tarefas WHERE id = "+id);
            Toast.makeText(getApplicationContext(), "Removido com sucesso!", Toast.LENGTH_SHORT).show();
            recuperarTarefas();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
