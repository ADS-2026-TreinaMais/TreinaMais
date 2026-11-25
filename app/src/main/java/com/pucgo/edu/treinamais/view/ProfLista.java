package com.pucgo.edu.treinamais.view;

import android.app.AppComponentFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pucgo.edu.treinamais.databinding.ProfListaBinding;
import com.pucgo.edu.treinamais.security.SessionManager;

public class ProfLista extends AppCompatActivity {
    private ProfListaBinding B;
    //private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle saveInstanceBundle){
        super.onCreate(saveInstanceBundle);
        B = ProfListaBinding.inflate(getLayoutInflater());
        setContentView(B.getRoot());


    }
}
